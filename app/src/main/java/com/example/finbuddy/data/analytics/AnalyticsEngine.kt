package com.example.finbuddy.data.analytics

import com.example.finbuddy.data.model.Transaction
import kotlin.math.roundToInt

data class FinancialScoreResult(
    val score: Int,
    val tier: String
)

object AnalyticsEngine {
    fun calculateFinancialScore(transactions: List<Transaction>): FinancialScoreResult {
        val totalIncome = transactions
            .filter { it.type.equals("Income", ignoreCase = true) }
            .sumOf { it.amount }

        val totalExpense = transactions
            .filter { it.type.equals("Expense", ignoreCase = true) }
            .sumOf { it.amount }

        val savingsPoints = if (totalIncome > 0) {
            val savingsRatio = (totalIncome - totalExpense) / totalIncome
            if (savingsRatio <= 0.0) {
                0
            } else {
                val scaled = (savingsRatio / 0.30).coerceIn(0.0, 1.0)
                (scaled * 500.0).roundToInt()
            }
        } else {
            0
        }

        val manualCount = transactions.count { it.source.equals("Manual", ignoreCase = true) }
        val stabilityPoints = (500 - (manualCount * 15)).coerceAtLeast(100)

        val finalScore = (savingsPoints + stabilityPoints).coerceIn(0, 1000)
        val tier = when (finalScore) {
            in 850..1000 -> "Excellent"
            in 700..849 -> "Good"
            in 550..699 -> "Fair"
            else -> "Critical"
        }

        return FinancialScoreResult(score = finalScore, tier = tier)
    }
}
