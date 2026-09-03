package com.fire.mangareader.domain.model.user

data class CustomUserList(
    val _id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val thumbnail: String? = null,
    var isInCustomList: Boolean? = null
)
