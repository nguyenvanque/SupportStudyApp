package com.example.suportstudy.model

import com.google.gson.annotations.SerializedName

class AddLike (
    @SerializedName("like")
    val like: Boolean,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("postId")
    val postId: String,
)