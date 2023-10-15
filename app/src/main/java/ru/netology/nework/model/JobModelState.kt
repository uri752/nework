package ru.netology.nework.model

data class JobModelState (
    val loading: Boolean = false,
    val loadError: Boolean = false,
    val removeError: Boolean = false,
    val saveError: Boolean = false
)