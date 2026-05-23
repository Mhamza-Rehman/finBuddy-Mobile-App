package com.example.finbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finbuddy.data.model.Transaction
import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class SelectedFilter { ALL, DEPOSITS, WITHDRAWALS }

data class ActivityGroup(
    val header: String,
    val transactions: List<Transaction>
)

sealed interface ActivityUiState {
    data object Loading : ActivityUiState
    data class Success(
        val groups: List<ActivityGroup>,
        val searchQuery: String,
        val selectedFilter: SelectedFilter
    ) : ActivityUiState
    data class Error(val message: String) : ActivityUiState
}

class ActivityViewModel : ViewModel() {
    private val _rawTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SelectedFilter.ALL)
    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)

    val uiState: StateFlow<ActivityUiState> = combine(
        _rawTransactions,
        _searchQuery,
        _selectedFilter,
        _error,
        _isLoading
    ) { list, query, filter, error, isLoading ->
        when {
            isLoading -> ActivityUiState.Loading
            error != null -> ActivityUiState.Error(error)
            else -> {
                val filtered = list.filter { transaction ->
                    val matchesFilter = when (filter) {
                        SelectedFilter.ALL -> true
                        SelectedFilter.DEPOSITS -> transaction.type.equals("Income", ignoreCase = true)
                        SelectedFilter.WITHDRAWALS -> transaction.type.equals("Expense", ignoreCase = true)
                    }
                    val matchesSearch = transaction.category.contains(query, ignoreCase = true)
                    matchesFilter && matchesSearch
                }
                ActivityUiState.Success(
                    groups = groupTransactions(filtered),
                    searchQuery = query,
                    selectedFilter = filter
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivityUiState.Loading)

    init {
        fetchTransactions()
    }

    fun fetchTransactions() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            runCatching {
                SupabaseClient.client.postgrest.from("transactions")
                    .select()
                    .decodeList<Transaction>()
                    .sortedByDescending { parseDate(it) ?: LocalDate.MIN }
            }.onSuccess {
                _rawTransactions.value = it
            }.onFailure { e ->
                _error.value = e.localizedMessage ?: "Failed to load activity"
            }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: SelectedFilter) {
        _selectedFilter.value = filter
    }

    private fun groupTransactions(transactions: List<Transaction>): List<ActivityGroup> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val grouped = linkedMapOf<String, MutableList<Transaction>>()

        transactions.forEach { transaction ->
            val date = parseDate(transaction)
            val key = when (date) {
                today -> "TODAY"
                yesterday -> "YESTERDAY"
                null -> "UNKNOWN"
                else -> date.format(DateTimeFormatter.ofPattern("MMMM dd", Locale.US)).uppercase(Locale.US)
            }
            grouped.getOrPut(key) { mutableListOf() }.add(transaction)
        }
        return grouped.map { ActivityGroup(it.key, it.value) }
    }

    fun formatTime(transaction: Transaction): String {
        val raw = transaction.timestamp ?: transaction.created_at ?: return "--:--"
        return runCatching {
            OffsetDateTime.parse(raw).toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
        }.getOrDefault("--:--")
    }

    private fun parseDate(transaction: Transaction): LocalDate? {
        val raw = transaction.timestamp ?: transaction.created_at ?: return null
        return runCatching { OffsetDateTime.parse(raw).toLocalDate() }.getOrNull()
    }
}
