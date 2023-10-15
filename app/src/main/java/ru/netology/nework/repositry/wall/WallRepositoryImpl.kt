package ru.netology.nework.repositry.wall

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.wall.WallDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Wall
import ru.netology.nework.entity.WallEntity
import ru.netology.nework.entity.toDto
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    private val appDb: AppDb,
) : WallRepository {

    override val wallData: LiveData<List<Wall>> = wallDao.get()
        .map(List<WallEntity>::toDto)

    override suspend fun getMyWall() {
        try {
            val wallResponse = apiService.getMyWall()
            if (!wallResponse.isSuccessful) {
                throw ApiError(wallResponse.code(), wallResponse.message())
            }
            val walls = wallResponse.body().orEmpty()
            wallDao.removeAll()
            wallDao.insert(walls.map(WallEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getWallByAuthorId(id: Long) {
        try {
            val wallResponse = apiService.getWallById(id)
            if (!wallResponse.isSuccessful) {
                throw ApiError(wallResponse.code(), wallResponse.message())
            }
            val walls = wallResponse.body().orEmpty()
            wallDao.removeAll()
            wallDao.insert(walls.map(WallEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeWallPostDao(id: Long) {
        try {
            wallDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeDb() {
        try {
            wallDao.removeAll()
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun likeByIdWall(id: Long) {
        try {
            wallDao.likeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun unlikeByIdWall(id: Long) {
        try {
            wallDao.unLikeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

}