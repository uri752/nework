package ru.netology.nework.repositry.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.event.EventDao
import ru.netology.nework.dao.event.EventRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.*
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.model.MediaModel
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    private val appDb: AppDb,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(10, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.getEventPaging() },
        remoteMediator = EventRemoteMediator(
            apiService = apiService,
            eventRemoteKeyDao = eventRemoteKeyDao,
            appDb = appDb,
            eventDao = eventDao
        )
    ).flow
        .map { it.map(EventEntity::toDto) }

    override suspend fun getEvent() {
        try {
            val response = apiService.getAllEvents()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val events = response.body().orEmpty()
            eventDao.insert(events.map(EventEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun likeById(id: Long) {

        try {
            val eventResponse = apiService.likeEventById(id)
            if (!eventResponse.isSuccessful) {
                throw ApiError(eventResponse.code(), eventResponse.message())
            }
            eventDao.likeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun unlikeById(id: Long) {
        try {
            val eventResponse = apiService.unlikeEventById(id)
            if (!eventResponse.isSuccessful) {
                throw ApiError(eventResponse.code(), eventResponse.message())
            }
           eventDao.unLikeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun cancelLike(id: Long) {
        eventDao.unLikeById(id, appAuth.getAuthId())
    }

    override suspend fun removeEventById(id: Long) {
        try {
            val eventResponse = apiService.deleteEventById(id)
            if (!eventResponse.isSuccessful) {
                throw ApiError(eventResponse.code(), eventResponse.message())
            }
            eventDao.removeById(id)
        } catch (_: Exception) {
        }
    }


    override suspend fun save(event: EventCreate) {
        try {
            val eventResponse = apiService.saveEvent(event)
            if (!eventResponse.isSuccessful) {
                throw ApiError(eventResponse.code(), eventResponse.message())
            }
            val body = eventResponse.body() ?: throw HttpException(eventResponse)

            eventDao.insert(EventEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun saveWithAttachment(event: EventCreate, mediaModel: MediaModel) {
        try {
            val media = upload(mediaModel)
            val events = event.copy(
                attachment = Attachment(
                    media.url,
                    type = mediaModel.type
                )
            )
            val response = apiService.saveEvent(events)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun upload(upload: MediaModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file?.name, upload.file?.asRequestBody() ?: throw NetworkError
            )

            val response = apiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}