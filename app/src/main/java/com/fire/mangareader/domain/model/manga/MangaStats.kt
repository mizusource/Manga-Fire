package com.fire.mangareader.domain.model.manga

data class MangaStats(
    val ratings: Map<Int, Int>,
    val userListStats: Map<String, Int>,
    val mangaRate: Map<String, Double>
)
