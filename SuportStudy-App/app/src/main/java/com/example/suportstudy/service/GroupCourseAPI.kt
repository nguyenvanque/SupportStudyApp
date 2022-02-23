package com.example.suportstudy.service

import com.example.suportstudy.model.GroupCourse
import com.example.suportstudy.model.Participant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface GroupCourseAPI {


    @GET("/api/getAllGrroup")
    fun getAllGroup(): Call<List<GroupCourse>>

    @POST("/api/getAllGroupByCourseID")
    @FormUrlEncoded
    fun getAllGroupByCourseID(@Field("courseId") courseId: String
    ): Call<List<GroupCourse>>

    @POST("/api/getAllGroupByID")
    @FormUrlEncoded
    fun getAllGroupByID(@Field("_id") groupId: String
    ): Call<List<GroupCourse>>
    @POST("/api/createGroupCourse")
    @FormUrlEncoded
    fun createGroupNoImage(
        @Field("createBy") createBy: String?,
        @Field("groupName") groupName: String,
        @Field("groupDescription") groupDescription: String,
        @Field("groupImage") groupImage: String,
        @Field("courseId") courseId: String,
    ): Call<GroupCourse>

    @Multipart
    @POST("/api/createGroupWithImage")
    fun createGroupWithImage(
        @Part("createBy") createBy: RequestBody?,
        @Part("groupName") groupName: RequestBody,
        @Part("groupDescription") groupDescription: RequestBody,
        @Part groupImage: MultipartBody.Part?,
        @Part("courseId") courseId: RequestBody,

        ): Call<GroupCourse>

    @POST("/api/updateGroupName")
    @FormUrlEncoded
    fun updateGroupName(
        @Field("_id") groupId: String?,
        @Field("groupName") groupName: String,
    ): Call<GroupCourse>

    @POST("/api/deleteUserGroup")
    @FormUrlEncoded
    fun deleteUserGroup(
        @Field("_id") _id: String?,
    ): Call<GroupCourse>

    @POST("/api/deleteGroup")
    @FormUrlEncoded
    fun deleteGroup(
        @Field("_id") groupId: String?,
    ): Call<GroupCourse>

    @POST("/api/joinGroup")
    @FormUrlEncoded
    fun joinGroup(
        @Field("groupId") groupId: String?,
        @Field("uid") uid: String,
        @Field("jointime") jointime: String,

    ): Call<Participant>


    @Multipart
    @POST("/api/updateGroupImage")
    fun updateGroupImage(
        @Part("_id") groupId:RequestBody,
        @Part newimage: MultipartBody.Part?,
        @Part("old_image") old_image:RequestBody
    ): Call<GroupCourse>
}