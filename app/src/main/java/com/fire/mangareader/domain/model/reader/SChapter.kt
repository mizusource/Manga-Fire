package com.fire.mangareader.domain.model.reader

import java.io.Serializable

interface SChapter : Serializable {
    var url: String
    var name: String
    var chapterId: String
    var chapter_number: Float
    var mangaId: Int?
    var sourceId: Int

    fun copyFrom(other: SChapter) {
        name = other.name
        url = other.url
        chapterId = other.chapterId
        chapter_number = other.chapter_number
        mangaId = other.mangaId
        sourceId = other.sourceId
    }

    companion object {
        fun create(): SChapter = SChapterImpl()
    }
}

class SChapterImpl : SChapter {
    override var url: String = ""
    override var name: String = ""
    override var chapterId: String = ""
    override var chapter_number: Float = -1f
    override var mangaId: Int? = null
    override var sourceId: Int = 0
}
