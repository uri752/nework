package ru.netology.nework.repositry.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.user.UserDao
import ru.netology.nework.dto.User
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.toDto
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
) : UserRepository {

    override val userData: LiveData<List<User>> = userDao.get()
        .map(List<UserEntity>::toDto)

    private val _userData = MutableLiveData<User>()

    override val user: LiveData<User>
        get() = _userData

    override suspend fun getUsers() {
        try {
            val response = apiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val users = response.body().orEmpty()
            userDao.insert(users.map(UserEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getUserById(id: Long) {
        _userData.value = User(0, "NoLogin", "Noname", null)
        try {
            val user = apiService.getUserById(id)
            if (!user.isSuccessful) {
                throw ApiError(user.code(), user.message())
            }
            _userData.value = user.body() ?: null
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}