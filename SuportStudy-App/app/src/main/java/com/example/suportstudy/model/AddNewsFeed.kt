package com.example.suportstudy.model

import com.google.gson.annotations.SerializedName

data class AddNewsFeed (
    @SerializedName("description")
    val description: String,
    @SerializedName("post")
    val image:  String?,
    @SerializedName("userId")
    val userId: String,
)