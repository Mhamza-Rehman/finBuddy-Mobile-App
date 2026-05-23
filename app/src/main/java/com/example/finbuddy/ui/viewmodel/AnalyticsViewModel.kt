package com.example.finbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finbuddy.data.analytics.AnalyticsEngine
import com.example.finbuddy.data.analytics.FinancialScoreResult
import com.example.finbuddy.data.model.Transaction
import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data class Success(val result: FinancialScoreResult) : AnalyticsUiState
    data class Error(val message: String) : AnalyticsUiState
}

class AnalyticsViewModel : ViewModel() {
    private val postgrest = SupabaseClient.client.postgrest

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        fetchFinancialScore()
    }

    fun fetchFinancialScore() {
        _uiState.value = AnalyticsUiState.Loading
        viewModelScope.launch {
            runCatching {
                postgrest.from("transactions")
                    .select()
                    .decodeList<Transaction>()
            }.onSuccess { transactions ->
                val result = AnalyticsEngine.calculateFinancialScore(transactions)
                _uiState.value = AnalyticsUiState.Success(result)
            }.onFailure { throwable ->
                _uiState.value = AnalyticsUiState.Error(
                    throwable.localizedMessage ?: "Failed to load analytics"
                )
            }
        }
    }
}
