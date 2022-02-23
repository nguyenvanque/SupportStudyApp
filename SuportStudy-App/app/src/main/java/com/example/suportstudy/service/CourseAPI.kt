package com.example.suportstudy.service

import com.example.suportstudy.model.Course
import retrofit2.Response
import retrofit2.http.GET

interface CourseAPI {

    @GET("/api/courses")
    suspend fun getAllCourse(): Response<List<Course>>

}