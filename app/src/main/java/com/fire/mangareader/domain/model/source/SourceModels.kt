package com.fire.mangareader.domain.model.source

data class Source(
    val title: String?,
    val altTitle: List<String>? = null,
    val externalSources: List<String>? = null,
    val url: String?,
    val poster: String?,
    val source: String,
    val slug: String?,
    val requiresCaptcha: Boolean = false,
    val chapters: Int? = 0,
    val latestChapterLabel: String? = null,
    val isMain: Boolean? = false
)

data class SourceChapter(
    val url: String?,
    val name: String?
)

data class SourceManga(
    val url: String?,
    val name: String?,
    val poster: String?,
    val latestChapter: String? = null,
    val timestamp: Long? = null
) {
    fun toWaitList(): WaitListItem {
        val slug = name?.lowercase()?.replace(Regex("[^a-z0-9]"), "-")?.replace(Regex("-+"), "-")?.trim('-') ?: ""
        return WaitListItem(
            title = name,
            slug = slug,
            source = null,
            url = url,
            isNew = true,
            foundInLatest = true
        )
    }
}

data class SourceResponse(
    val sourceUrl: String?,
    val sourceChapters: List<SourceChapter>? = null
)

data class GroupedSource(
    var id: Int?,
    val slug: String,
    val sources: List<Source>
) {
    fun toWaitList(): WaitListItem {
        val firstSource = sources.firstOrNull()
        return WaitListItem(
            title = firstSource?.title,
            slug = slug,
            source = sources,
            url = null,
            isNew = false,
            foundInLatest = true
        )
    }
}

data class MatchedGroupedSource(
    val group: GroupedSource,
    val timestamp: Long?
)

data class WaitListItem(
    val title: String?,
    val slug: String?,
    val source: List<Source>?,
    val url: String?,
    val isNew: Boolean?,
    val foundInLatest: Boolean?
)
