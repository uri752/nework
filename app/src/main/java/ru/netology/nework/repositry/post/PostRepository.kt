package ru.netology.nework.repositry.post

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.*
import ru.netology.nework.model.MediaModel

interface PostRepository {

    val data: Flow<PagingData<Post>>

    suspend fun getPosts()

    suspend fun likeById(id: Long)

    suspend fun unlikeById(id: Long)

    suspend fun cancelLike(id: Long)

    suspend fun removePostById(id: Long)

    suspend fun save(post: PostCreate)

    suspend fun saveWithAttachment(post: PostCreate, media: MediaModel)

    suspend fun upload(upload: MediaModel): Media

}