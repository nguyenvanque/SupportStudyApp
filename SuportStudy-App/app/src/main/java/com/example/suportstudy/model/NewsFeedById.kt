package com.example.suportstudy.model

import com.google.gson.annotations.SerializedName

data class NewsFeedById (
    @SerializedName("_id")
    val _id: String,
    @SerializedName("userId")
    val userId: Users,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("__v")
    val __v: String,
    @SerializedName("typeClassId")
    val typeClassId: String,
    @SerializedName("comment")
    val comment: Array<CommentById>?= null,
    @SerializedName("like")
    val like: Array<LikeById>?= null,
    )
