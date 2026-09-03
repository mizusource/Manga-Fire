package com.fire.mangareader.data.parser

import android.content.Context
import android.content.SharedPreferences
import com.fire.mangareader.domain.model.parser.SourceConfig
import com.fire.mangareader.util.MangaOkHttp
import com.google.gson.Gson
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ParserConfigManager {
    private const val PREFS_NAME = "parser_config_prefs"
    private const val KEY_CONFIG_JSON = "config_json"
    private const val KEY_SYNC_URL = "sync_url"
    
    private val gson = Gson()
    
    var currentConfig: SourceConfig? = null
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CONFIG_JSON, null)
        if (json != null) {
            try {
                currentConfig = gson.fromJson(json, SourceConfig::class.java)
                // Update IP mapping in okhttp
                MangaOkHttp.directIpInterceptor.updateMapping(currentConfig?.ip_mapping)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSyncUrl(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SYNC_URL, "") ?: ""
    }

    suspend fun syncConfig(context: Context, url: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = MangaOkHttp.getClient().newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
            }
            
            val jsonStr = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
            
            // Validate JSON
            val config = gson.fromJson(jsonStr, SourceConfig::class.java)
            if (config.extractors.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("Invalid config format: extractors missing"))
            }
            
            // Save to prefs
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_CONFIG_JSON, jsonStr)
                .putString(KEY_SYNC_URL, url)
                .apply()
                
            currentConfig = config
            MangaOkHttp.directIpInterceptor.updateMapping(config.ip_mapping)
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getExtractor(name: String): com.fire.mangareader.domain.model.parser.ExtractorConfig? {
        return currentConfig?.extractors?.find { it.name == name }
    }
}
