package ru.netology.nework.dto

data class PostCreate(
    val id: Long,
    val content: String,
    val coords: Coordinates? = null,
    val link: String,
    var attachment: Attachment? = null,
    val mentionIds: List<Long> = emptyList(),
)
