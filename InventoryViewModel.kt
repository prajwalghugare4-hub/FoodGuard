package com.example.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.HistoricalLogEntity
import com.example.data.repository.FoodGuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class InventoryUiState(
  val logs: List<HistoricalLogEntity> = emptyList(),
  val totalPrepared: Double = 0.0,
  val totalRescued: Double = 0.0,
  val totalWasted: Double = 0.0,
  val wastePreventedPct: Int = 0,
  val isLoading: Boolean = false
)

class InventoryViewModel(private val repository: FoodGuardRepository) : ViewModel() {

  val currentUser = repository.currentUser

  val uiState: StateFlow<InventoryUiState> = currentUser.flatMapLatest { user ->
    if (user == null) flowOf(InventoryUiState())
    else {
      repository.getHistoricalLogs(user.userId).combine(flowOf(user)) { logs, _ ->
        val prepared = logs.sumOf { it.quantityPrepared }
        val rescued = logs.sumOf { it.quantityRescued }
        val wasted = logs.sumOf { it.quantityWasted }
        val pct = if (prepared > 0) ((rescued / prepared) * 100).toInt() else 0

        InventoryUiState(
          logs = logs,
          totalPrepared = prepared,
          totalRescued = rescued,
          totalWasted = wasted,
          wastePreventedPct = pct,
          isLoading = false
        )
      }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = InventoryUiState()
  )

  fun addMealLog(
    dateStr: String,
    dayOfWeek: String,
    mealType: String,
    foodItems: String,
    prepared: Double,
    consumed: Double,
    rescued: Double,
    wasted: Double,
    orders: Int,
    weather: String?,
    onComplete: () -> Unit
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val remaining = maxOf(0.0, prepared - consumed)
      val log = HistoricalLogEntity(
        providerId = user.userId,
        date = dateStr,
        dayOfWeek = dayOfWeek,
        mealType = mealType,
        foodItems = foodItems,
        quantityPrepared = prepared,
        quantityConsumed = consumed,
        quantityRemaining = remaining,
        quantityRescued = rescued,
        quantityWasted = wasted,
        ordersCount = orders,
        weatherContext = weather
      )
      repository.addHistoricalLog(log)
      onComplete()
    }
  }
}
