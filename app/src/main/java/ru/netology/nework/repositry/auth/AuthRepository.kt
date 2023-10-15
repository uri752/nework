package ru.netology.nework.repositry.auth

import ru.netology.nework.model.MediaModel

interface AuthRepository {

    suspend fun authorization(login: String, password: String)

    suspend fun registration(login: String, password: String,name: String)

    suspend fun registrationWithAvatar(login: String, passwodr: String, name: String, media: MediaModel)

}