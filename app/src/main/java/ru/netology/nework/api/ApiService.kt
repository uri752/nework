package ru.netology.nework.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*
import ru.netology.nework.model.AuthModel

interface ApiService {
    @GET("/api/events/")
    suspend fun getAllEvents(): Response<List<Event>>

    @POST("/api/events/")
    suspend fun saveEvent(@Body eventCreate: EventCreate): Response<Event>

    @GET("/api/events/latest/")
    suspend fun getLatestEvent(@Query("count") count: Int): Response<List<Event>>

    @GET("/api/events/{event_id}/")
    suspend fun getEventById(@Path("event_id") eventId: Long): Response<Event>

    @DELETE("/api/events/{event_id}/")
    suspend fun deleteEventById(@Path("event_id") eventId: Long): Response<Unit>

    @GET("/api/events/{event_id}/after/")
    suspend fun getEventAfter(
        @Path("event_id") eventId: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("/api/events/{event_id}/after/")
    suspend fun getEventBefore(
        @Path("event_id") eventId: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @POST("/api/events/{event_id}/likes/")
    suspend fun likeEventById(@Path("event_id") eventId: Long): Response<Event>

    @DELETE("/api/events/{event_id}/likes/")
    suspend fun unlikeEventById(@Path("event_id") eventId: Long): Response<Event>

    @GET("/api/events/{event_id}/newer/")
    suspend fun getEventNewer(@Path("event_id") eventId: Long): Response<List<Event>>

    @POST("/api/events/{event_id}/participants/")
    suspend fun addParticipants(@Path("event_id") eventId: Long): Response<List<Event>>

    @DELETE("/api/events/{event_id}/participants/")
    suspend fun deleteParticipants(@Path("event_id") eventId: Long): Response<List<Event>>

    @Multipart
    @POST("/api/media/")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<Media>

    @GET("/api/my/jobs/")
    suspend fun getMyJobs(): Response<List<Job>>

    @POST("/api/my/jobs/")
    suspend fun saveMyJob(@Body job: Job): Response<Job>

    @DELETE("/api/my/jobs/{job_id}/")
    suspend fun deleteMyJob(@Path("job_id") jobId: Long): Response<Unit>

    @GET("/api/{user_id}/jobs/")
    suspend fun getUserJob(@Path("user_id") userId: Long): Response<List<Job>>

    @GET("/api/my/wall/")
    suspend fun getMyWall(): Response<List<Wall>>

    @GET("/api/my/wall/latest/")
    suspend fun getLatestWall(@Query("count") count: Int): Response<List<Post>>

    @GET("/api/my/wall/{post_id}/after/")
    suspend fun getAfterWall(
        @Path("post_id") postId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/my/wall/{post_id}/before/")
    suspend fun getBeforeWall(
        @Path("post_id") postId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/my/wall/{post_id}/newer/")
    suspend fun getNewerWall(@Path("post_id") postId: Long): Response<List<Post>>

    //Posts
    @GET("/api/posts/")
    suspend fun getAllPosts(): Response<List<Post>>

    @POST("/api/posts/")
    suspend fun savePost(@Body post: PostCreate): Response<Post>

    @GET("/api/posts/latest/")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("/api/posts/{post_id}/")
    suspend fun getPostById(@Path("post_id") postId: Long): Response<Post>

    @DELETE("/api/posts/{post_id}/")
    suspend fun deletePostById(@Path("post_id") postId: Long): Response<Unit>

    @GET("/api/posts/{post_id}/after/")
    suspend fun getAfterPosts(
        @Path("post_id") postId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/posts/{post_id}/before/")
    suspend fun getBeforePosts(
        @Path("post_id") postId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/posts/{post_id}/newer/")
    suspend fun getNewerPosts(@Path("post_id") postId: Long): Response<List<Post>>

    @POST("/api/posts/{post_id}/likes/")
    suspend fun likePostById(@Path("post_id") postId: Long): Response<Post>

    @DELETE("/api/posts/{post_id}/likes/")
    suspend fun unlikePostById(@Path("post_id") postId: Long): Response<Post>

    @GET("/api/users/")
    suspend fun getUsers(): Response<List<User>>

    @GET("/api/users/{user_id}/")
    suspend fun getUserById(@Path("user_id") userId: Long): Response<User>

    @FormUrlEncoded
    @POST("/api/users/authentication/")
    suspend fun authUser(
        @Field("login") login: String,
        @Field("password") password: String
    ): Response<AuthModel>

    @FormUrlEncoded
    @POST("/api/users/registration/")
    suspend fun registrationUser(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Response<AuthModel>

    @Multipart
    @POST("/api/users/registration/")
    suspend fun registerWithAvatar(
        @Part("login") login: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<AuthModel>

    @GET("/api/{author_id}/wall/")
    suspend fun getWallById(@Path("author_id") authorId: Long): Response<List<Wall>>

    @GET("/api/{author_id}/wall/latest/")
    suspend fun getLatestWallById(
        @Path("author_id") authorId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{author_id}/wall/{post_id}}/after/")
    suspend fun getAfterWallById(
        @Path("author_id") authorId: Long,
        @Path("post_id") postId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{author_id}/wall/{post_id}}/before/")
    suspend fun getBeforeWallById(
        @Path("author_id") authorId: Long,
        @Path("post_id") postId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{author_id}/wall/{post_id}}/newer/")
    suspend fun getNewerWallById(
        @Path("author_id") authorId: Long,
        @Path("post_id") postId: Long
    ): Response<List<Post>>
}