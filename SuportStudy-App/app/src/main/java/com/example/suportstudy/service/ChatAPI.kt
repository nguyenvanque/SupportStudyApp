package com.example.suportstudy.service

import com.example.suportstudy.model.Chat
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatAPI {

    @GET("/api/allChat")
    suspend fun getAllMessage(): Response<List<Chat>>

    @POST("/api/insertChat")
    @FormUrlEncoded
    fun saveChat(
        @Field("senderUid") senderUid: String?,
        @Field("receiverUid") receiverUid: String?,
        @Field("timeSend") timeSend: String,
        @Field("messageType") messageType: String,
        @Field("message") message: String,
    ): Call<Chat>

}