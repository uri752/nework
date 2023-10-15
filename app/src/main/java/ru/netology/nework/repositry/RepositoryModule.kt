package ru.netology.nework.repositry

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.repositry.auth.AuthRepository
import ru.netology.nework.repositry.auth.AuthRepositoryImpl
import ru.netology.nework.repositry.event.EventRepository
import ru.netology.nework.repositry.event.EventRepositoryImpl
import ru.netology.nework.repositry.job.JobRepository
import ru.netology.nework.repositry.job.JobRepositoryImpl
import ru.netology.nework.repositry.post.PostRepository
import ru.netology.nework.repositry.post.PostRepositoryImpl
import ru.netology.nework.repositry.user.UserRepository
import ru.netology.nework.repositry.user.UserRepositoryImpl
import ru.netology.nework.repositry.wall.WallRepository
import ru.netology.nework.repositry.wall.WallRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    abstract fun bindWallRepository(impl: WallRepositoryImpl): WallRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindJobRepository(impl: JobRepositoryImpl): JobRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}