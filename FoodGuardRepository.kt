package com.example.data.repository

import com.example.data.local.FoodGuardDao
import com.example.data.local.SeedData
import com.example.data.local.SessionManager
import com.example.data.model.HistoricalLogEntity
import com.example.data.model.NgoEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PredictionResult
import com.example.data.model.RescueRequestEntity
import com.example.data.model.RescueStatus
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ml.SurplusPredictionEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FoodGuardRepository(
  private val dao: FoodGuardDao,
  private val sessionManager: SessionManager? = null
) {

  private val coroutineScope = CoroutineScope(Dispatchers.IO)
  private val secureRandom = SecureRandom()

  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  private val _isSessionLoaded = MutableStateFlow(false)
  val isSessionLoaded: StateFlow<Boolean> = _isSessionLoaded.asStateFlow()

  val allNgos: Flow<List<NgoEntity>> = dao.getAllNgos()

  init {
    coroutineScope.launch {
      // Sync verified NGO directory
      dao.insertNgos(SeedData.getVerifiedNgos())

      // Restore authenticated session if exists
      val savedUserId = sessionManager?.getSessionUserId()
      if (!savedUserId.isNullOrBlank()) {
        val user = dao.getUserById(savedUserId)
        if (user != null) {
          _currentUser.value = user
        } else {
          sessionManager?.clearSession()
        }
      }
      _isSessionLoaded.value = true
    }
  }

  // --- Authentication ---

  suspend fun registerUser(
    fullName: String,
    email: String,
    phoneNumber: String,
    passwordHash: String,
    role: UserRole,
    address: String,
    city: String,
    state: String,
    pinCode: String,
    establishmentName: String?,
    establishmentType: String?
  ): Result<UserEntity> {
    val existing = dao.getUserByEmail(email.trim().lowercase(Locale.ROOT))
    if (existing != null) {
      return Result.failure(Exception("An account with this email already exists."))
    }

    val userId = "usr_" + UUID.randomUUID().toString().take(12)
    val newUser = UserEntity(
      userId = userId,
      fullName = fullName.trim(),
      email = email.trim().lowercase(Locale.ROOT),
      phoneNumber = phoneNumber.trim(),
      passwordHash = passwordHash,
      role = role,
      address = address.trim(),
      city = city.trim(),
      state = state.trim(),
      pinCode = pinCode.trim(),
      establishmentName = if (role == UserRole.PROVIDER) establishmentName?.trim() else null,
      establishmentType = if (role == UserRole.PROVIDER) establishmentType?.trim() else null,
      isGoogleAccount = false
    )
    dao.insertUser(newUser)

    sessionManager?.saveSession(newUser.userId)
    _currentUser.value = newUser
    return Result.success(newUser)
  }

  suspend fun loginUser(email: String, password: String): Result<UserEntity> {
    val user = dao.getUserByEmail(email.trim().lowercase(Locale.ROOT))
      ?: return Result.failure(Exception("Account not found. Please register first."))

    if (user.passwordHash.isNotEmpty() && user.passwordHash != password) {
      return Result.failure(Exception("Invalid email or password."))
    }

    sessionManager?.saveSession(user.userId)
    _currentUser.value = user
    return Result.success(user)
  }

  suspend fun updateUserProfile(updatedUser: UserEntity): Result<Unit> {
    val current = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
    // Prevent modification of protected fields (UID, account ownership, role)
    val safeUser = updatedUser.copy(
      userId = current.userId,
      role = current.role,
      isGoogleAccount = current.isGoogleAccount,
      createdAt = current.createdAt
    )
    dao.updateUser(safeUser)
    _currentUser.value = safeUser
    return Result.success(Unit)
  }

  fun logout() {
    sessionManager?.clearSession()
    _currentUser.value = null
  }

  suspend fun deleteAccount(userId: String): Result<Unit> {
    val current = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
    if (current.userId != userId) {
      return Result.failure(Exception("Unauthorized account deletion."))
    }
    // Delete only user's own data
    dao.deleteLogsForProvider(userId)
    dao.deleteRescuesForProvider(userId)
    dao.deleteNotificationsForUser(userId)
    dao.deleteUserById(userId)
    sessionManager?.clearSession()
    _currentUser.value = null
    return Result.success(Unit)
  }

  // --- Historical Logs & Prediction ---

  fun getHistoricalLogs(providerId: String): Flow<List<HistoricalLogEntity>> {
    return dao.getLogsForProvider(providerId)
  }

  suspend fun addHistoricalLog(log: HistoricalLogEntity) {
    dao.insertHistoricalLog(log)
  }

  suspend fun predictDemand(
    providerId: String,
    mealType: String,
    currentPlanned: Int
  ): PredictionResult {
    val logs = dao.getLogsForProviderSync(providerId)
    return SurplusPredictionEngine.calculatePrediction(
      historicalLogs = logs,
      mealType = mealType,
      currentPlannedQuantity = currentPlanned
    )
  }

  // --- Rescue Requests ---

  fun getProviderRescues(providerId: String): Flow<List<RescueRequestEntity>> {
    return dao.getRescuesForProvider(providerId)
  }

  fun getAllRescues(): Flow<List<RescueRequestEntity>> {
    return dao.getAllRescues()
  }

  suspend fun createRescueRequest(
    providerId: String,
    providerName: String,
    providerPhone: String,
    foodItems: String,
    mealType: String,
    quantity: String,
    quantityNumeric: Double,
    pickupDeadline: String,
    pickupDeadlineTimestamp: Long,
    pickupAddress: String,
    additionalInstructions: String?
  ): RescueRequestEntity {
    // Generate secure random 4-digit PIN (1000 - 9999)
    val randomPinInt = 1000 + secureRandom.nextInt(9000)
    val verificationPin = randomPinInt.toString()

    val rescue = RescueRequestEntity(
      rescueId = "res_" + UUID.randomUUID().toString().take(8),
      providerId = providerId,
      providerName = providerName,
      providerPhone = providerPhone,
      foodItems = foodItems.trim(),
      mealType = mealType,
      quantity = quantity.trim(),
      quantityNumeric = quantityNumeric,
      preparationTime = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(Date()),
      pickupDeadline = pickupDeadline,
      pickupDeadlineTimestamp = pickupDeadlineTimestamp,
      pickupAddress = pickupAddress.trim(),
      additionalInstructions = additionalInstructions?.trim(),
      verificationPin = verificationPin,
      status = RescueStatus.OPEN
    )
    dao.insertRescue(rescue)

    // Notify provider
    dao.insertNotification(
      NotificationEntity(
        userId = providerId,
        title = "Food Rescue Created",
        message = "Your rescue listing for ${rescue.quantity} of ${rescue.foodItems} is now active. PIN: $verificationPin",
        type = "RESCUE_CREATED"
      )
    )

    return rescue
  }

  suspend fun assignNgoToRescue(rescueId: String, ngo: NgoEntity) {
    val rescue = dao.getRescueById(rescueId) ?: return
    val updated = rescue.copy(
      ngoId = ngo.ngoId,
      ngoName = ngo.name,
      ngoPhone = ngo.phone,
      status = RescueStatus.CONTACTED,
      ngoResponse = "CONTACTED"
    )
    dao.updateRescue(updated)

    dao.insertNotification(
      NotificationEntity(
        userId = rescue.providerId,
        title = "NGO Contacted: ${ngo.name}",
        message = "Donation alert dispatched to ${ngo.name}. Awaiting pickup schedule.",
        type = "NGO_RESPONSE"
      )
    )
  }

  suspend fun updateRescueStatus(rescueId: String, status: RescueStatus) {
    val rescue = dao.getRescueById(rescueId) ?: return
    val updated = rescue.copy(
      status = status,
      completedAt = if (status == RescueStatus.COMPLETED) System.currentTimeMillis() else rescue.completedAt
    )
    dao.updateRescue(updated)
  }

  suspend fun verifyPickupAndComplete(
    rescueId: String,
    enteredPin: String,
    actualRescued: Double,
    actualWasted: Double
  ): Result<Unit> {
    val rescue = dao.getRescueById(rescueId) ?: return Result.failure(Exception("Rescue not found"))

    if (rescue.verificationPin.trim() != enteredPin.trim()) {
      return Result.failure(Exception("Invalid verification PIN. Please confirm PIN with collector."))
    }

    val updated = rescue.copy(
      status = RescueStatus.COMPLETED,
      completedAt = System.currentTimeMillis(),
      actualRescuedQuantity = actualRescued,
      actualWastedQuantity = actualWasted
    )
    dao.updateRescue(updated)

    // ML Feedback Loop: record completed outcome into historical logs for future training
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val dayOfWeekStr = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    dao.insertHistoricalLog(
      HistoricalLogEntity(
        providerId = rescue.providerId,
        date = todayStr,
        dayOfWeek = dayOfWeekStr,
        mealType = rescue.mealType,
        foodItems = rescue.foodItems,
        quantityPrepared = rescue.quantityNumeric,
        quantityConsumed = maxOf(0.0, rescue.quantityNumeric - actualRescued - actualWasted),
        quantityRemaining = actualRescued + actualWasted,
        quantityRescued = actualRescued,
        quantityWasted = actualWasted,
        ordersCount = (rescue.quantityNumeric * 0.9).toInt()
      )
    )

    dao.insertNotification(
      NotificationEntity(
        userId = rescue.providerId,
        title = "Rescue Completed & Verified",
        message = "Pickup successfully verified with PIN. ${actualRescued.toInt()} plates recorded as rescued!",
        type = "PICKUP_COMPLETED"
      )
    )

    return Result.success(Unit)
  }

  suspend fun checkAndExpireRescues() {
    val now = System.currentTimeMillis()
    val all = dao.getAllRescuesSync()
    all.forEach { r ->
      if ((r.status == RescueStatus.OPEN || r.status == RescueStatus.CONTACTED) &&
        r.pickupDeadlineTimestamp > 0 &&
        now > r.pickupDeadlineTimestamp
      ) {
        dao.updateRescue(r.copy(status = RescueStatus.EXPIRED))
        dao.insertNotification(
          NotificationEntity(
            userId = r.providerId,
            title = "Rescue Request Expired",
            message = "Pickup deadline for listing '${r.foodItems}' has passed without collection.",
            type = "EXPIRATION_ALERT"
          )
        )
      }
    }
  }

  // --- Notifications ---

  fun getNotifications(userId: String): Flow<List<NotificationEntity>> {
    return dao.getNotificationsForUser(userId)
  }

  suspend fun markNotificationRead(id: Long) {
    dao.markNotificationAsRead(id)
  }
}
