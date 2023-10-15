package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nework.dao.event.EventDao
import ru.netology.nework.dao.event.EventRemoteKeyDao
import ru.netology.nework.dao.job.JobDao
import ru.netology.nework.dao.post.PostDao
import ru.netology.nework.dao.post.PostRemoteKeyDao
import ru.netology.nework.dao.wall.WallDao
import ru.netology.nework.dao.user.UserDao
import ru.netology.nework.entity.*
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.EventRemoteKeyEntity
import ru.netology.nework.utils.ListConverter

@TypeConverters(ListConverter::class)
@Database(entities = [PostEntity::class, EventEntity::class, PostRemoteKeyEntity::class, EventRemoteKeyEntity::class, WallEntity::class, JobEntity::class, UserEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun wallDao(): WallDao
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
}