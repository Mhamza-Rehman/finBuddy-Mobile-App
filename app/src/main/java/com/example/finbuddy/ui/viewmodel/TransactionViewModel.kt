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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface TransactionUiState {
    data object Idle : TransactionUiState
    data object Loading : TransactionUiState
    data object Success : TransactionUiState
    data class Error(val message: String) : TransactionUiState
}

sealed interface AddTransactionEvent {
    data object Completed : AddTransactionEvent
}

class TransactionViewModel(
    private val transactionRepo: TransactionRepository = TransactionRepositoryImpl(),
    private val accountRepo: AccountRepository = AccountRepositoryImpl(),
    private val authRepo: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {
    private val currentDate = LocalDate.now()
    private val displayFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")

    val dateState = mutableStateOf(currentDate.format(displayFormatter))
    val datePayloadState = mutableStateOf(currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE))

    var uiState by mutableStateOf<TransactionUiState>(TransactionUiState.Idle)
        private set

    private val _events = MutableSharedFlow<AddTransactionEvent>()
    val events = _events.asSharedFlow()

    fun saveTransaction(amountStr: String, type: String, category: String, transactionDate: String) {
        if (uiState == TransactionUiState.Loading) return

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
                    transactionRepo.addManualTransaction(
                        userId = userId,
                        accountId = accountId,
                        amount = amount,
                        type = type,
                        category = category,
                        transactionDate = transactionDate
                    )
                        .onSuccess {
                            uiState = TransactionUiState.Success
                            _events.emit(AddTransactionEvent.Completed)
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
