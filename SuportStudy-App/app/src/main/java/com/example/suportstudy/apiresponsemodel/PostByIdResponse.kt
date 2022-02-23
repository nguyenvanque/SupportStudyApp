package com.example.suportstudy.apiresponsemodel

import com.example.suportstudy.model.NewsFeed
import com.google.gson.annotations.SerializedName

class PostByIdResponse (
    @SerializedName("payload")
    var data: ArrayList<NewsFeed>
)