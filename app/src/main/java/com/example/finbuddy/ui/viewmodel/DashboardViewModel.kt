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
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val metrics: DashboardMetrics) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {
    private val postgrest = SupabaseClient.client.postgrest

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshMetrics()
    }

    fun refreshMetrics() {
        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            runCatching {
                postgrest.from("transactions").select().decodeList<Transaction>()
            }.onSuccess { transactions ->
                val totalIncome = transactions
                    .filter { it.type.equals("Income", ignoreCase = true) }
                    .sumOf { it.amount }
                val totalExpenses = transactions
                    .filter { it.type.equals("Expense", ignoreCase = true) }
                    .sumOf { it.amount }
                val totalBalance = totalIncome - totalExpenses

                _uiState.value = DashboardUiState.Success(
                    DashboardMetrics(
                        totalBalance = totalBalance,
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses
                    )
                )
            }.onFailure { e ->
                _uiState.value = DashboardUiState.Error(
                    e.localizedMessage ?: "Failed to load dashboard metrics"
                )
            }
        }
    }
}
