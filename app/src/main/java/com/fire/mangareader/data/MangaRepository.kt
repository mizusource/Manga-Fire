package com.fire.mangareader.data

import android.content.Context
import android.content.SharedPreferences
import com.fire.mangareader.engine.DilarEngine
import com.fire.mangareader.engine.MadaraEngine
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.model.MangaSourceId
import com.fire.mangareader.model.ReaderPage
import com.fire.mangareader.model.ReadingProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class MangaRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("manga_reader_prefs", Context.MODE_PRIVATE)

    private val starzEngine = MadaraEngine(MangaSourceId.MANGA_STARZ)
    private val sparkEngine = MadaraEngine(MangaSourceId.MANGA_SPARK)
    private val mangalikEngine = MadaraEngine(MangaSourceId.MANGALIK)
    private val dilarEngine = DilarEngine()

    private val _favorites = MutableStateFlow<List<MangaItem>>(emptyList())
    val favorites: StateFlow<List<MangaItem>> = _favorites.asStateFlow()

    private val _history = MutableStateFlow<Map<String, ReadingProgress>>(emptyMap())
    val history: StateFlow<Map<String, ReadingProgress>> = _history.asStateFlow()

    init {
        loadFavorites()
        loadHistory()
    }

    suspend fun getMangaList(sourceId: MangaSourceId, page: Int = 1, order: String = "latest"): List<MangaItem> {
        return when (sourceId) {
            MangaSourceId.MANGA_STARZ -> starzEngine.getMangaList(page, order)
            MangaSourceId.MANGA_SPARK -> sparkEngine.getMangaList(page, order)
            MangaSourceId.MANGALIK -> mangalikEngine.getMangaList(page, order)
            MangaSourceId.DILAR_TUBE -> dilarEngine.getMangaList(page)
        }
    }

    suspend fun searchManga(sourceId: MangaSourceId, query: String, page: Int = 1): List<MangaItem> {
        return when (sourceId) {
            MangaSourceId.MANGA_STARZ -> starzEngine.searchManga(query, page)
            MangaSourceId.MANGA_SPARK -> sparkEngine.searchManga(query, page)
            MangaSourceId.MANGALIK -> mangalikEngine.searchManga(query, page)
            MangaSourceId.DILAR_TUBE -> dilarEngine.searchManga(query)
        }
    }

    suspend fun getMangaDetails(sourceId: MangaSourceId, url: String): MangaDetails {
        return when (sourceId) {
            MangaSourceId.MANGA_STARZ -> starzEngine.getMangaDetails(url)
            MangaSourceId.MANGA_SPARK -> sparkEngine.getMangaDetails(url)
            MangaSourceId.MANGALIK -> mangalikEngine.getMangaDetails(url)
            MangaSourceId.DILAR_TUBE -> dilarEngine.getMangaDetails(url)
        }
    }

    suspend fun getChapterPages(sourceId: MangaSourceId, chapterUrl: String): List<ReaderPage> {
        return when (sourceId) {
            MangaSourceId.MANGA_STARZ -> starzEngine.getChapterPages(chapterUrl)
            MangaSourceId.MANGA_SPARK -> sparkEngine.getChapterPages(chapterUrl)
            MangaSourceId.MANGALIK -> mangalikEngine.getChapterPages(chapterUrl)
            MangaSourceId.DILAR_TUBE -> dilarEngine.getChapterPages(chapterUrl)
        }
    }

    fun toggleFavorite(manga: MangaItem) {
        val current = _favorites.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == manga.id }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        } else {
            current.add(0, manga)
        }
        _favorites.value = current
        saveFavorites(current)
    }

    fun isFavorite(mangaId: String): Boolean {
        return _favorites.value.any { it.id == mangaId }
    }

    fun saveProgress(progress: ReadingProgress) {
        val current = _history.value.toMutableMap()
        current[progress.mangaId] = progress
        _history.value = current
        saveHistory(current)
    }

    fun getProgress(mangaId: String): ReadingProgress? {
        return _history.value[mangaId]
    }

    private fun loadFavorites() {
        val jsonStr = prefs.getString("favorites_list", "[]") ?: "[]"
        try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<MangaItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MangaItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        url = obj.getString("url"),
                        coverUrl = obj.getString("coverUrl"),
                        latestChapter = obj.optString("latestChapter").takeIf { it.isNotBlank() },
                        rating = obj.optString("rating").takeIf { it.isNotBlank() },
                        sourceId = MangaSourceId.valueOf(obj.getString("sourceId"))
                    )
                )
            }
            _favorites.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveFavorites(list: List<MangaItem>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("url", item.url)
                put("coverUrl", item.coverUrl)
                put("latestChapter", item.latestChapter ?: "")
                put("rating", item.rating ?: "")
                put("sourceId", item.sourceId.name)
            }
            array.put(obj)
        }
        prefs.edit().putString("favorites_list", array.toString()).apply()
    }

    private fun loadHistory() {
        val jsonStr = prefs.getString("reading_history", "{}") ?: "{}"
        try {
            val jsonObject = JSONObject(jsonStr)
            val map = mutableMapOf<String, ReadingProgress>()
            for (key in jsonObject.keys()) {
                val obj = jsonObject.getJSONObject(key)
                map[key] = ReadingProgress(
                    mangaId = obj.getString("mangaId"),
                    chapterId = obj.getString("chapterId"),
                    chapterName = obj.getString("chapterName"),
                    pageIndex = obj.optInt("pageIndex", 0),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            }
            _history.value = map
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveHistory(map: Map<String, ReadingProgress>) {
        val jsonObject = JSONObject()
        for ((key, item) in map) {
            val obj = JSONObject().apply {
                put("mangaId", item.mangaId)
                put("chapterId", item.chapterId)
                put("chapterName", item.chapterName)
                put("pageIndex", item.pageIndex)
                put("timestamp", item.timestamp)
            }
            jsonObject.put(key, obj)
        }
        prefs.edit().putString("reading_history", jsonObject.toString()).apply()
    }
}
