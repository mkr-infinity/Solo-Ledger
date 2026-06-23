package com.solo.ledger.domain.model

data class CategorySummary(val category: String, val total: Double, val count: Int)
data class BudgetOverview(
    val monthlyBudget: Double,
    val used: Double,
    val remaining: Double,
    val dailyAverage: Double
)
