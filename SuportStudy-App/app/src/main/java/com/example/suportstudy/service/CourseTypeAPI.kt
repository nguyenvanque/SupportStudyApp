package com.example.suportstudy.service

import com.example.suportstudy.model.CourseType
import retrofit2.Response
import retrofit2.http.GET

interface CourseTypeAPI {
    @GET("/api/courseType")
    suspend fun getAllCourseType(): Response<List<CourseType>>
}