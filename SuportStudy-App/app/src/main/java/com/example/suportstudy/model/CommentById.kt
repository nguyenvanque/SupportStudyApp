package com.example.suportstudy.model

import com.google.gson.annotations.SerializedName

data class CommentById(
    @SerializedName("_id")
    var _id: String,
    @SerializedName("userId")
    var userId : Users,
    @SerializedName("content")
    var content: String,
    @SerializedName("createdAt")
    val createdAt: String,

)