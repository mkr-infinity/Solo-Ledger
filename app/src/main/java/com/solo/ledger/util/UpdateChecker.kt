package com.solo.ledger.util

import com.solo.ledger.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ReleaseInfo(
    val tagName: String,
    val name: String,
    val body: String,
    val htmlUrl: String,
    val publishedAt: String
)

object UpdateChecker {
    private const val API_URL = "https://api.github.com/repos/mkr-infinity/Solo-Ledger/releases/latest"

    suspend fun checkForUpdate(): Result<ReleaseInfo?> = withContext(Dispatchers.IO) {
        try {
            val url = URL(API_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            if (conn.responseCode != 200) {
                conn.disconnect()
                return@withContext Result.success(null)
            }

            val json = conn.inputStream.bufferedReader().readText()
            conn.disconnect()

            val obj = JSONObject(json)
            val tagName = obj.getString("tag_name").removePrefix("v")
            val releaseInfo = ReleaseInfo(
                tagName = tagName,
                name = obj.optString("name", tagName),
                body = obj.optString("body", ""),
                htmlUrl = obj.getString("html_url"),
                publishedAt = obj.optString("published_at", "")
            )
            val isNewer = isVersionNewer(tagName, BuildConfig.VERSION_NAME)
            Result.success(if (isNewer) releaseInfo else null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isVersionNewer(remote: String, current: String): Boolean {
        val remoteParts = remote.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(remoteParts.size, currentParts.size)) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
