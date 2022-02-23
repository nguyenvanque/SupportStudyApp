package com.example.suportstudy.service

import com.example.suportstudy.model.Users
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserAPI {

    @GET("/api/getAllUser")
     fun getAllUsers(): Call<List<Users>>

    @POST("/api/register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String,
        @Field("image") image: String,
        @Field("isTurtor") isTurtor: Boolean,
    ): Call<Users>

    @POST("/api/findUserEmailPassWord")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String,
    ): Call<List<Users>>

    @POST("/api/getAllUseId")
    @FormUrlEncoded
    fun getAllUsersByID( @Field("_id") _id:String ?): Call<List<Users>>



    @Multipart
    @POST("/api/updateImage")
    fun editImage(
        @Part("_id") id:RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("old_image") old_image:RequestBody
    ): Call<Users>

    @POST("/api/updateName")
    @FormUrlEncoded
    fun editName(
        @Field("_id") id: String?,
        @Field("name") name: String?,
    ):Call<Users>


    @POST("/api/updatePassword")
    @FormUrlEncoded
    fun editPassword(
        @Field("_id") id: String?,
        @Field("password") password: String?,
    ):Call<Users>

}