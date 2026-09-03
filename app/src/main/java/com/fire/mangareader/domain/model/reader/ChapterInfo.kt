package com.fire.mangareader.domain.model.reader

data class ChapterInfo(
    var key: String,
    var name: String,
    var chapterId: String,
    var number: Float = -1f,
    var scanlator: Int? = null,
    var sourceId: Int
)
