package com.example.suportstudy.apiresponsemodel

import com.example.suportstudy.model.NewsFeedById
import com.google.gson.annotations.SerializedName

data class NewsFeedByIdResponse(
    @SerializedName("payload")
    var data: NewsFeedById
)