package com.fire.mangareader.domain.model.chapter

data class ChapterData(
    val data: DataX? = null,
    val success: Boolean? = false
)

data class DataX(
    val data: DataXX? = null,
    val message: String = ""
)

data class DataXX(
    val content: String? = null,
    val nav: String? = null
)
