package com.example.finbuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.finbuddy.data.repository.AccountRepository
import com.example.finbuddy.data.repository.AccountRepositoryImpl
import com.example.finbuddy.data.repository.AuthRepository
import com.example.finbuddy.data.repository.AuthRepositoryImpl
import com.example.finbuddy.data.repository.TransactionRepository
import com.example.finbuddy.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SmsReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val transactionRepo: TransactionRepository = TransactionRepositoryImpl()
    private val accountRepo: AccountRepository = AccountRepositoryImpl()
    private val authRepo: AuthRepository = AuthRepositoryImpl()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (sms in messages) {
            val messageBody = sms.messageBody ?: continue
            Log.d("SmsReceiver", "Incoming text caught: $messageBody")
            parseAndLogSmsTransaction(messageBody)
        }
    }

    private fun parseAndLogSmsTransaction(body: String) {
        val normalizedBody = body.replace(",", "")
        val amountRegex = """(?i)(?:Rs\.?|PKR)\s*(\d+(?:\.\d{1,2})?)""".toRegex()
        val matchResult = amountRegex.find(normalizedBody) ?: return
        val amount = matchResult.groupValues[1].toDoubleOrNull() ?: return

        val isExpense = normalizedBody.contains("debited", ignoreCase = true) ||
            normalizedBody.contains("transaction", ignoreCase = true) ||
            normalizedBody.contains("transferred", ignoreCase = true) ||
            normalizedBody.contains("paid", ignoreCase = true)

        val isIncome = normalizedBody.contains("credited", ignoreCase = true) ||
            normalizedBody.contains("received", ignoreCase = true)

        val type = when {
            isExpense -> "Expense"
            isIncome -> "Income"
            else -> return
        }

        val category = if (type == "Expense") "Auto SMS Expense" else "Auto SMS Income"
        val userId = authRepo.getCurrentUserId() ?: return
        val transactionDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        scope.launch {
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
                            Log.d("SmsReceiver", "Successfully auto-logged SMS transaction: $amount $type")
                        }
                        .onFailure {
                            Log.e("SmsReceiver", "Failed to upload SMS transaction data to Supabase", it)
                        }
                }
            }
        }
    }
}
