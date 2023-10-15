package ru.netology.nework.dao.post

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun get(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPaging(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getPost(id: Long): PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(post: PostEntity)

    suspend fun likeById(id: Long, userId: Long) {
        val post = getPost(id)
        val likeUser = post.likeOwnerIds.toMutableList()
        likeUser.add(userId)
        save(post.copy(likeOwnerIds = likeUser))
    }

    suspend fun unLikeById(id: Long, userId: Long) {
        val post = getPost(id)
        val likeUser = post.likeOwnerIds.toMutableList()
        likeUser.remove(userId)
        save(post.copy(likeOwnerIds = likeUser))
    }
}