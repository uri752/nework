package ru.netology.nework.model

data class EventModelState(
    val loading: Boolean = false,
    val loadError: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val likeError: Boolean = false,
    val removeError: Boolean = false,
    val saveError: Boolean = false
)