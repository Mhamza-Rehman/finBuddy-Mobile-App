package com.example.finbuddy.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finbuddy.data.repository.AccountRepository
import com.example.finbuddy.data.repository.AccountRepositoryImpl
import com.example.finbuddy.data.repository.AuthRepository
import com.example.finbuddy.data.repository.AuthRepositoryImpl
import com.example.finbuddy.data.repository.TransactionRepository
import com.example.finbuddy.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.launch

sealed interface TransactionUiState {
    data object Idle : TransactionUiState
    data object Loading : TransactionUiState
    data object Success : TransactionUiState
    data class Error(val message: String) : TransactionUiState
}

class TransactionViewModel(
    private val transactionRepo: TransactionRepository = TransactionRepositoryImpl(),
    private val accountRepo: AccountRepository = AccountRepositoryImpl(),
    private val authRepo: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var uiState by mutableStateOf<TransactionUiState>(TransactionUiState.Idle)
        private set

    fun saveTransaction(amountStr: String, type: String, category: String) {
        val amount = amountStr.toDoubleOrNull()
        val userId = authRepo.getCurrentUserId()

        if (amount == null || amount <= 0 || userId == null || category.isBlank()) {
            uiState = TransactionUiState.Error("Invalid input details")
            return
        }

        uiState = TransactionUiState.Loading

        viewModelScope.launch {
            accountRepo.getPrimaryAccountId(userId).onSuccess { accountId ->
                if (accountId != null) {
                    transactionRepo.addManualTransaction(userId, accountId, amount, type, category)
                        .onSuccess {
                            uiState = TransactionUiState.Success
                        }
                        .onFailure { error ->
                            uiState = TransactionUiState.Error(error.localizedMessage ?: "Failed to save")
                        }
                } else {
                    uiState = TransactionUiState.Error("No primary wallet found")
                }
            }.onFailure {
                uiState = TransactionUiState.Error("Account verification failed")
            }
        }
    }

    fun resetState() {
        uiState = TransactionUiState.Idle
    }
}
