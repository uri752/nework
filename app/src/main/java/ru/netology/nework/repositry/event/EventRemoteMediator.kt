package ru.netology.nework.repositry.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.event.EventDao
import ru.netology.nework.dao.event.EventRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.EventRemoteKeyEntity
import ru.netology.nework.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: ApiService,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
    private val eventDao: EventDao,
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): RemoteMediator.MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
               //    val id = eventRemoteKeyDao.max() ?: return RemoteMediator.MediatorResult.Success(true)
                        apiService.getLatestEvent(state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return RemoteMediator.MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return RemoteMediator.MediatorResult.Success(false)
                    apiService.getEventBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val eventBody = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (eventDao.isEmpty()) {
                            eventRemoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        EventRemoteKeyEntity.KeyType.AFTER,
                                        eventBody.first().id
                                    ),
                                    EventRemoteKeyEntity(
                                        EventRemoteKeyEntity.KeyType.BEFORE,
                                        eventBody.last().id
                                    ),
                                )
                            )
                        } else {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.KeyType.AFTER,
                                    eventBody.first().id
                                ),
                            )
                        }
                    }
                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                eventBody.first().id
                            ),
                        )
                    }
                    LoadType.APPEND -> {
                        eventDao.insert(eventBody.map(EventEntity::fromDto))
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                eventBody.last().id
                            ),
                        )
                    }
                }
            }
            eventDao.insert(eventBody.map(EventEntity::fromDto))

            return RemoteMediator.MediatorResult.Success(eventBody.isEmpty())
        } catch (e: IOException) {
            return RemoteMediator.MediatorResult.Error(e)
        }
    }
}