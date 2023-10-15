package ru.netology.nework.repositry.user

import androidx.lifecycle.LiveData
import ru.netology.nework.dto.User

interface UserRepository {

    val userData: LiveData<List<User>>

    val user: LiveData<User>

    suspend fun getUsers()

    suspend fun getUserById(id: Long)
}