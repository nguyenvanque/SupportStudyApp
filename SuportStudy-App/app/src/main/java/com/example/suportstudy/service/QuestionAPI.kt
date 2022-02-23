package com.example.suportstudy.service

import com.example.suportstudy.model.Question
import retrofit2.Response
import retrofit2.http.GET


interface QuestionAPI {
    @GET("/api/questions")
    suspend fun getAllQuestion(): Response<List<Question>>
}