package com.example.ui.screens.rescue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.NgoEntity
import com.example.data.model.RescueRequestEntity
import com.example.data.model.RescueStatus
import com.example.data.repository.FoodGuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CreateRescueUiState(
  val foodItems: String = "Rice, Dal Tadka, Matar Paneer, Chapati",
  val quantity: String = "18 Plates",
  val mealType: String = "Lunch",
  val generatedPin: String = "",
  val pickupDeadlineTime: String = "04:30 PM",
  val pickupAddress: String = "Grand Hotel Catering Entrance, 4th Cross, Downtown",
  val additionalInstructions: String = "Ask for Raj at back gate. Bring clean containers.",
  val selectedNgo: NgoEntity? = null,
  val isSafetyConfirmed: Boolean = false,
  val isPublishing: Boolean = false,
  val errorMessage: String? = null
)

class RescueViewModel(private val repository: FoodGuardRepository) : ViewModel() {

  private val _createState = MutableStateFlow(CreateRescueUiState())
  val createState: StateFlow<CreateRescueUiState> = _createState.asStateFlow()

  val currentUser = repository.currentUser

  val nearbyNgos: StateFlow<List<NgoEntity>> = repository.allNgos.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val providerRescues: StateFlow<List<RescueRequestEntity>> = combine(
    repository.currentUser,
    repository.getAllRescues()
  ) { user, allRescues ->
    if (user == null) emptyList()
    else allRescues.filter { it.providerId == user.userId }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val allPublicRescues: StateFlow<List<RescueRequestEntity>> = repository.getAllRescues().stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  init {
    regeneratePin()
    // Auto-prefill address from current user
    viewModelScope.launch {
      currentUser.collect { user ->
        if (user != null && _createState.value.pickupAddress.isBlank()) {
          _createState.value = _createState.value.copy(pickupAddress = user.address)
        }
      }
    }
  }

  fun regeneratePin() {
    val randomPin = 1000 + SecureRandom().nextInt(9000)
    _createState.value = _createState.value.copy(generatedPin = randomPin.toString())
  }

  fun onFoodItemsChange(value: String) {
    _createState.value = _createState.value.copy(foodItems = value, errorMessage = null)
  }

  fun onQuantityChange(value: String) {
    _createState.value = _createState.value.copy(quantity = value, errorMessage = null)
  }

  fun onMealTypeChange(value: String) {
    _createState.value = _createState.value.copy(mealType = value)
  }

  fun onPickupDeadlineChange(value: String) {
    _createState.value = _createState.value.copy(pickupDeadlineTime = value, errorMessage = null)
  }

  fun onPickupAddressChange(value: String) {
    _createState.value = _createState.value.copy(pickupAddress = value, errorMessage = null)
  }

  fun onInstructionsChange(value: String) {
    _createState.value = _createState.value.copy(additionalInstructions = value)
  }

  fun onSelectNgo(ngo: NgoEntity?) {
    _createState.value = _createState.value.copy(selectedNgo = ngo)
  }

  fun onSafetyDeclarationToggle(checked: Boolean) {
    _createState.value = _createState.value.copy(isSafetyConfirmed = checked, errorMessage = null)
  }

  fun generateDynamicAlertFromCurrentState(ngo: NgoEntity? = _createState.value.selectedNgo): String {
    val state = _createState.value
    val user = currentUser.value
    val providerName = user?.establishmentName ?: user?.fullName ?: "Food Provider"
    val providerPhone = user?.phoneNumber ?: "+91 98765 43210"

    return com.example.ui.util.FoodRescueAlertHelper.generateFoodRescueAlertMessage(
      providerName = providerName,
      foodItems = state.foodItems,
      quantity = state.quantity,
      mealType = state.mealType,
      pickupDeadline = state.pickupDeadlineTime,
      pickupAddress = state.pickupAddress,
      verificationPin = state.generatedPin,
      providerPhone = providerPhone,
      selectedNgo = ngo
    )
  }

  fun publishRescue(onSuccess: (RescueRequestEntity) -> Unit) {
    val state = _createState.value
    if (state.foodItems.isBlank() || state.quantity.isBlank() || state.pickupDeadlineTime.isBlank() || state.pickupAddress.isBlank()) {
      _createState.value = state.copy(errorMessage = "Please complete all mandatory food rescue fields.")
      return
    }

    if (!state.isSafetyConfirmed) {
      _createState.value = state.copy(errorMessage = "You must confirm the Food Safety Declaration before publishing.")
      return
    }

    val user = currentUser.value
    if (user == null) {
      _createState.value = state.copy(errorMessage = "You must be signed in to create a food rescue.")
      return
    }

    _createState.value = state.copy(isPublishing = true, errorMessage = null)

    viewModelScope.launch {
      val deadlineStr = "${state.pickupDeadlineTime}, Today"
      // 4 hours from now as deadline timestamp
      val deadlineTimestamp = System.currentTimeMillis() + (4 * 60 * 60 * 1000)

      val numericQty = state.quantity.filter { it.isDigit() }.toDoubleOrNull() ?: 18.0

      val rescue = repository.createRescueRequest(
        providerId = user.userId,
        providerName = user.establishmentName ?: user.fullName,
        providerPhone = user.phoneNumber,
        foodItems = state.foodItems,
        mealType = state.mealType,
        quantity = state.quantity,
        quantityNumeric = numericQty,
        pickupDeadline = deadlineStr,
        pickupDeadlineTimestamp = deadlineTimestamp,
        pickupAddress = state.pickupAddress,
        additionalInstructions = state.additionalInstructions
      )

      state.selectedNgo?.let { designatedNgo ->
        repository.assignNgoToRescue(rescue.rescueId, designatedNgo)
      }

      _createState.value = _createState.value.copy(isPublishing = false)
      regeneratePin()
      onSuccess(rescue)
    }
  }

  fun assignNgoToRescue(rescueId: String, ngo: NgoEntity) {
    viewModelScope.launch {
      repository.assignNgoToRescue(rescueId, ngo)
    }
  }

  fun verifyPickupPin(
    rescueId: String,
    enteredPin: String,
    actualRescued: Double,
    actualWasted: Double,
    onResult: (Result<Unit>) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.verifyPickupAndComplete(rescueId, enteredPin, actualRescued, actualWasted)
      onResult(result)
    }
  }

  fun formatStructuredDonationMessage(rescue: RescueRequestEntity, providerName: String, providerPhone: String, ngo: NgoEntity? = null): String {
    return """
🍲 FOOD RESCUE ALERT from $providerName${if (ngo != null) "\n🎯 Designated NGO: ${ngo.name}" else ""}

🍛 Surplus Food Details:
• Items: ${rescue.foodItems}
• Quantity: ${rescue.quantity}
• Meal Type: ${rescue.mealType}

📍 Pickup Address:
${rescue.pickupAddress}

⏰ Pickup Deadline:
${rescue.pickupDeadline}

🔐 Verification PIN:
${rescue.verificationPin}

📞 Provider Contact Number:
$providerPhone
${if (ngo != null) "\n📞 NGO Helpline: ${ngo.phone}\n📍 NGO Address: ${if (ngo.address.isNotEmpty()) ngo.address else "${ngo.city}, ${ngo.state}"}" else ""}
    """.trimIndent()
  }
}
