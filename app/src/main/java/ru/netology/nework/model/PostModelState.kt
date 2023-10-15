package ru.netology.nework.model

data class PostModelState(
    val loading: Boolean = false,
    val loadError: Boolean = false,
    val refreshing: Boolean = false,
    val likeError: Boolean = false,
    val removeError: Boolean = false,
    val saveError: Boolean = false
)
