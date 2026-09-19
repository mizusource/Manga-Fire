package com.fire.mangareader.model

enum class MangaSourceId(val displayName: String, val baseUrl: String, val isMadara: Boolean) {
    MANGA_STARZ("Manga Starz", "https://manga-starz.net", true),
    MANGA_SPARK("Manga Spark", "https://manga-spark.net", true),
    MANGALIK("Mangalik", "https://mangalik.net", true),
    DILAR_TUBE("Dilar Tube", "https://dilar.tube", false)
}

data class MangaItem(
    val id: String,
    val title: String,
    val url: String,
    val coverUrl: String,
    val latestChapter: String? = null,
    val rating: String? = null,
    val sourceId: MangaSourceId
)

data class MangaDetails(
    val id: String,
    val title: String,
    val url: String,
    val coverUrl: String,
    val description: String,
    val author: String? = null,
    val status: String? = null,
    val genres: List<String> = emptyList(),
    val chapters: List<Chapter> = emptyList(),
    val sourceId: MangaSourceId
)

data class Chapter(
    val id: String,
    val name: String,
    val url: String,
    val date: String? = null,
    val number: Float = 0f
)

data class ReaderPage(
    val index: Int,
    val imageUrl: String,
    val referer: String
)

data class ReadingProgress(
    val mangaId: String,
    val chapterId: String,
    val chapterName: String,
    val pageIndex: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
