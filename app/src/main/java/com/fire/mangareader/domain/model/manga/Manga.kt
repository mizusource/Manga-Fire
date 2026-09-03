package com.fire.mangareader.domain.model.manga

data class Manga(
    val _id: Int,
    val latestChapter: String?,
    val poster: String?,
    val title: String?,
    val rating: Double?,
    val status: Int?,
    val genres: List<String>?,
    var isInCustomList: Boolean? = false
)
