package com.example.ui.screens.ngos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.NgoEntity
import com.example.data.repository.FoodGuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class NgoFilter {
  ALL,
  COOKED,
  RAW,
  PACKAGED,
  PICKUP_ONLY
}

data class NgoUiState(
  val searchQuery: String = "",
  val selectedFilter: NgoFilter = NgoFilter.ALL,
  val ngos: List<NgoEntity> = emptyList(),
  val filteredNgos: List<NgoEntity> = emptyList(),
  val isLoading: Boolean = false
)

class NgoViewModel(private val repository: FoodGuardRepository) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _selectedFilter = MutableStateFlow(NgoFilter.ALL)

  val uiState: StateFlow<NgoUiState> = combine(
    repository.allNgos,
    _searchQuery,
    _selectedFilter
  ) { ngos, query, filter ->
    val filtered = ngos.filter { ngo ->
      val matchesQuery = query.isBlank() ||
        ngo.name.contains(query, ignoreCase = true) ||
        ngo.city.contains(query, ignoreCase = true) ||
        ngo.serviceArea.contains(query, ignoreCase = true) ||
        ngo.description.contains(query, ignoreCase = true)

      val matchesFilter = when (filter) {
        NgoFilter.ALL -> true
        NgoFilter.COOKED -> ngo.acceptsCookedFood
        NgoFilter.RAW -> ngo.acceptsRawFood
        NgoFilter.PACKAGED -> ngo.acceptsPackedFood
        NgoFilter.PICKUP_ONLY -> ngo.pickupAvailable
      }

      matchesQuery && matchesFilter
    }

    NgoUiState(
      searchQuery = query,
      selectedFilter = filter,
      ngos = ngos,
      filteredNgos = filtered,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = NgoUiState()
  )

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  fun onFilterSelect(filter: NgoFilter) {
    _selectedFilter.value = filter
  }
}
