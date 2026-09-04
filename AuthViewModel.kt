package com.example.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.FoodGuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
  val isLoginMode: Boolean = true,
  val selectedRole: UserRole = UserRole.PROVIDER,
  val fullName: String = "",
  val email: String = "",
  val phoneNumber: String = "",
  val password: String = "",
  val address: String = "",
  val city: String = "Bengaluru",
  val state: String = "Karnataka",
  val pinCode: String = "560001",
  val establishmentName: String = "",
  val establishmentType: String = "Mess",
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val successMessage: String? = null
)

class AuthViewModel(private val repository: FoodGuardRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(AuthUiState())
  val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

  val currentUser = repository.currentUser

  fun toggleMode(isLogin: Boolean) {
    _uiState.value = _uiState.value.copy(
      isLoginMode = isLogin,
      errorMessage = null,
      successMessage = null
    )
  }

  fun setRole(role: UserRole) {
    _uiState.value = _uiState.value.copy(selectedRole = role)
  }

  fun onFullNameChange(value: String) {
    _uiState.value = _uiState.value.copy(fullName = value, errorMessage = null)
  }

  fun onEmailChange(value: String) {
    _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
  }

  fun onPhoneChange(value: String) {
    _uiState.value = _uiState.value.copy(phoneNumber = value, errorMessage = null)
  }

  fun onPasswordChange(value: String) {
    _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
  }

  fun onAddressChange(value: String) {
    _uiState.value = _uiState.value.copy(address = value, errorMessage = null)
  }

  fun onCityChange(value: String) {
    _uiState.value = _uiState.value.copy(city = value)
  }

  fun onStateChange(value: String) {
    _uiState.value = _uiState.value.copy(state = value)
  }

  fun onPinCodeChange(value: String) {
    _uiState.value = _uiState.value.copy(pinCode = value)
  }

  fun onEstablishmentNameChange(value: String) {
    _uiState.value = _uiState.value.copy(establishmentName = value)
  }

  fun onEstablishmentTypeChange(value: String) {
    _uiState.value = _uiState.value.copy(establishmentType = value)
  }

  fun login(onSuccess: () -> Unit) {
    val state = _uiState.value
    if (state.email.isBlank() || state.password.isBlank()) {
      _uiState.value = state.copy(errorMessage = "Please enter both email and password.")
      return
    }

    _uiState.value = state.copy(isLoading = true, errorMessage = null)
    viewModelScope.launch {
      val result = repository.loginUser(state.email, state.password)
      result.fold(
        onSuccess = {
          _uiState.value = _uiState.value.copy(isLoading = false)
          onSuccess()
        },
        onFailure = { error ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = error.message ?: "Authentication failed."
          )
        }
      )
    }
  }

  fun register(onSuccess: () -> Unit) {
    val state = _uiState.value
    if (state.fullName.isBlank() || state.email.isBlank() || state.phoneNumber.isBlank() || state.password.isBlank() || state.establishmentName.isBlank()) {
      _uiState.value = state.copy(errorMessage = "Please fill in all required fields including Establishment Name.")
      return
    }

    _uiState.value = state.copy(isLoading = true, errorMessage = null)
    viewModelScope.launch {
      val result = repository.registerUser(
        fullName = state.fullName,
        email = state.email,
        phoneNumber = state.phoneNumber,
        passwordHash = state.password,
        role = UserRole.PROVIDER,
        address = state.address.ifBlank { "City Center Main Street" },
        city = state.city,
        state = state.state,
        pinCode = state.pinCode,
        establishmentName = state.establishmentName,
        establishmentType = state.establishmentType
      )
      result.fold(
        onSuccess = {
          _uiState.value = _uiState.value.copy(isLoading = false)
          onSuccess()
        },
        onFailure = { error ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = error.message ?: "Registration failed."
          )
        }
      )
    }
  }
}
