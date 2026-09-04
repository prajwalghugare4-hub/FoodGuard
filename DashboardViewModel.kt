package com.example.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.NgoEntity
import com.example.data.model.PredictionResult
import com.example.data.model.RescueRequestEntity
import com.example.data.model.SurplusRiskLevel
import com.example.data.model.UserEntity
import com.example.data.repository.FoodGuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
  val user: UserEntity? = null,
  val selectedMealType: String = "Lunch",
  val currentPlannedQuantity: Int = 100,
  val prediction: PredictionResult = PredictionResult.InsufficientData(
    message = "Insufficient data for reliable prediction",
    requiredEntries = 2,
    currentEntries = 0
  ),
  val nearbyNgos: List<NgoEntity> = emptyList(),
  val activeRescues: List<RescueRequestEntity> = emptyList(),
  val unreadNotificationsCount: Int = 0,
  val isLoading: Boolean = false
)

class DashboardViewModel(private val repository: FoodGuardRepository) : ViewModel() {

  private val _selectedMealType = MutableStateFlow("Lunch")
  private val _currentPlannedQuantity = MutableStateFlow(100)
  private val _predictionState = MutableStateFlow<PredictionResult>(
    PredictionResult.InsufficientData(
      message = "Insufficient data for reliable prediction",
      requiredEntries = 2,
      currentEntries = 0
    )
  )

  val currentUser = repository.currentUser

  val uiState: StateFlow<DashboardUiState> = combine(
    repository.currentUser,
    _selectedMealType,
    _currentPlannedQuantity,
    _predictionState,
    repository.allNgos
  ) { user, mealType, planned, prediction, ngos ->
    DashboardUiState(
      user = user,
      selectedMealType = mealType,
      currentPlannedQuantity = planned,
      prediction = prediction,
      nearbyNgos = ngos.take(3),
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DashboardUiState()
  )

  init {
    viewModelScope.launch {
      currentUser.collect { user ->
        if (user != null) {
          recalculatePrediction(user.userId, _selectedMealType.value, _currentPlannedQuantity.value)
        }
      }
    }
  }

  fun updatePlannedQuantity(quantity: Int) {
    _currentPlannedQuantity.value = maxOf(10, quantity)
    val user = currentUser.value
    if (user != null) {
      recalculatePrediction(user.userId, _selectedMealType.value, _currentPlannedQuantity.value)
    }
  }

  fun updateMealType(mealType: String) {
    _selectedMealType.value = mealType
    val user = currentUser.value
    if (user != null) {
      recalculatePrediction(user.userId, mealType, _currentPlannedQuantity.value)
    }
  }

  private fun recalculatePrediction(providerId: String, mealType: String, planned: Int) {
    viewModelScope.launch {
      val result = repository.predictDemand(providerId, mealType, planned)
      _predictionState.value = result
    }
  }
}
