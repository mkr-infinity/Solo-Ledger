package com.solo.ledger.data.repository

import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.data.local.entity.GoalEntity
import org.json.JSONArray
import org.json.JSONObject

/** Serializes the full ledger to/from a portable JSON document. No third-party libraries. */
class BackupManager(private val repo: LedgerRepository) {

    suspend fun exportJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())
        val exArr = JSONArray()
        repo.allExpensesSnapshot().forEach { e ->
            exArr.put(JSONObject().apply {
                put("id", e.id); put("title", e.title); put("amount", e.amount)
                put("category", e.category); put("dateEpochDay", e.dateEpochDay)
                put("timeMinutes", e.timeMinutes); put("notes", e.notes)
                put("attachmentUri", e.attachmentUri ?: JSONObject.NULL)
                put("isDeleted", e.isDeleted); put("deletedAt", e.deletedAt ?: JSONObject.NULL)
                put("createdAt", e.createdAt)
            })
        }
        root.put("expenses", exArr)
        val catArr = JSONArray()
        repo.allCategoriesSnapshot().forEach { c ->
            catArr.put(JSONObject().apply {
                put("name", c.name); put("iconKey", c.iconKey)
                put("colorArgb", c.colorArgb); put("isDefault", c.isDefault)
            })
        }
        root.put("categories", catArr)
        val goalArr = JSONArray()
        repo.allGoalsSnapshot().forEach { g ->
            goalArr.put(JSONObject().apply {
                put("id", g.id); put("title", g.title); put("targetAmount", g.targetAmount)
                put("savedAmount", g.savedAmount); put("iconKey", g.iconKey); put("createdAt", g.createdAt)
            })
        }
        root.put("goals", goalArr)
        return root.toString(2)
    }

    suspend fun importJson(text: String): Int {
        val root = JSONObject(text)
        val expenses = mutableListOf<ExpenseEntity>()
        root.optJSONArray("expenses")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                expenses += ExpenseEntity(
                    id = o.optLong("id"),
                    title = o.optString("title"),
                    amount = o.optDouble("amount"),
                    category = o.optString("category"),
                    dateEpochDay = o.optLong("dateEpochDay"),
                    timeMinutes = o.optInt("timeMinutes"),
                    notes = o.optString("notes", ""),
                    attachmentUri = if (o.isNull("attachmentUri")) null else o.optString("attachmentUri"),
                    isDeleted = o.optBoolean("isDeleted", false),
                    deletedAt = if (o.isNull("deletedAt")) null else o.optLong("deletedAt"),
                    createdAt = o.optLong("createdAt", System.currentTimeMillis())
                )
            }
        }
        val categories = mutableListOf<CategoryEntity>()
        root.optJSONArray("categories")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                categories += CategoryEntity(
                    o.optString("name"), o.optString("iconKey", "category"),
                    o.optLong("colorArgb", 0xFF64748B), o.optBoolean("isDefault", false)
                )
            }
        }
        val goals = mutableListOf<GoalEntity>()
        root.optJSONArray("goals")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                goals += GoalEntity(
                    o.optLong("id"), o.optString("title"), o.optDouble("targetAmount"),
                    o.optDouble("savedAmount", 0.0), o.optString("iconKey", "savings"),
                    o.optLong("createdAt", System.currentTimeMillis())
                )
            }
        }
        if (categories.isNotEmpty()) repo.restoreCategories(categories)
        if (expenses.isNotEmpty()) repo.restoreExpenses(expenses)
        if (goals.isNotEmpty()) repo.restoreGoals(goals)
        return expenses.size
    }
}
