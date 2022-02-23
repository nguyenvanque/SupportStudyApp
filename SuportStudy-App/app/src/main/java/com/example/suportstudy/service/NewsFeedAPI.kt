package com.example.suportstudy.service

import com.example.suportstudy.apiresponsemodel.NewsFeedByIdResponse
import com.example.suportstudy.apiresponsemodel.NewsFeedResponse
import com.example.suportstudy.model.AddLike
import com.example.suportstudy.model.AddNewsFeed
import com.example.suportstudy.model.Comment
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NewsFeedAPI {

    @GET("/api/newsfeed")
    fun getAllNewsFeed(): Call<NewsFeedResponse>

    @POST("/api/newsfeed/")
    @FormUrlEncoded
    fun createNewsFeedNoImage(
        @Field("description") description: String,
        @Field("userId") userId: String,
        @Field("image") image: String,
    ): Call<AddNewsFeed>

    @POST("/api/newsfeed/like/id")
    @FormUrlEncoded
    fun addLike(
        @Field("like") like: Boolean,
        @Field("userId") userId: String,
        @Field("postId") postId: String,
    ): Call<AddLike>

    @POST("/api/newsfeed/addComment")
    @FormUrlEncoded
    fun addComment(
        @Field("content") content: String,
        @Field("userId") userId: String,
        @Field("postId") postId: String,
    ): Call<Comment>

    @Multipart
    @POST("/api/newsfeed/")
    fun createNewsFeedWithImage(
        @Part("description") description: RequestBody??,
        @Part("userId") userId: RequestBody??,
        @Part image: MultipartBody.Part?,
        ): Call<AddNewsFeed>

    @PUT("/api/newsfeed/byId")
    @FormUrlEncoded
    fun getPostById(
        @Field("postId") id: String,
    ): Call<NewsFeedByIdResponse>
}