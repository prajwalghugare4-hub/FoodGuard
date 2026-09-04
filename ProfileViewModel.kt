package com.example.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.repository.FoodGuardRepository
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ThemeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
  val user: UserEntity? = null,
  val notifications: List<NotificationEntity> = emptyList(),
  val isEditing: Boolean = false,
  val isDeletingAccount: Boolean = false,
  val errorMessage: String? = null,
  val successMessage: String? = null
)

class ProfileViewModel(
  private val repository: FoodGuardRepository,
  private val themeManager: ThemeManager? = null
) : ViewModel() {

  val currentUser = repository.currentUser

  val currentThemeMode: StateFlow<AppThemeMode> = themeManager?.themeMode
    ?: MutableStateFlow(AppThemeMode.LIGHT).asStateFlow()

  fun setThemeMode(mode: AppThemeMode) {
    themeManager?.setThemeMode(mode)
  }

  val notifications: StateFlow<List<NotificationEntity>> = currentUser.flatMapLatest { user ->
    if (user == null) flowOf(emptyList())
    else repository.getNotifications(user.userId)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  fun updateUser(
    fullName: String,
    phone: String,
    address: String,
    city: String,
    state: String,
    pinCode: String,
    establishmentName: String?,
    establishmentType: String?,
    onComplete: () -> Unit
  ) {
    val current = currentUser.value ?: return
    val updated = current.copy(
      fullName = fullName.trim(),
      phoneNumber = phone.trim(),
      address = address.trim(),
      city = city.trim(),
      state = state.trim(),
      pinCode = pinCode.trim(),
      establishmentName = establishmentName?.trim(),
      establishmentType = establishmentType?.trim()
    )

    viewModelScope.launch {
      repository.updateUserProfile(updated)
      onComplete()
    }
  }

  fun logout(onLoggedOut: () -> Unit) {
    repository.logout()
    onLoggedOut()
  }

  fun deleteAccount(onDeleted: () -> Unit) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.deleteAccount(user.userId)
      onDeleted()
    }
  }

  fun markNotificationAsRead(id: Long) {
    viewModelScope.launch {
      repository.markNotificationRead(id)
    }
  }
}
