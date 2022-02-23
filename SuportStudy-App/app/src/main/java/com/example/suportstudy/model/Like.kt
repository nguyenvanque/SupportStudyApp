package com.example.suportstudy.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Like(
    @SerializedName("_id")
    var _id: String,
    @SerializedName("like")
    var like : Boolean,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("postId")
    var postId: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("updatedAt")
    var updatedAt: String   ,
    @SerializedName("__v")
    var __v: Int,
)