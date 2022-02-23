package com.example.suportstudy.model

import com.google.gson.annotations.SerializedName

data class Chat(
    @SerializedName("_id")
    var _id:String?=null,
    @SerializedName("senderUid")
    var senderUid:String?=null,
    @SerializedName("senderName")
    var senderName:String?=null,
    @SerializedName("receiverUid")
    var receiverUid:String?=null,
    @SerializedName("timeSend")
    var timeSend:String?=null,
    @SerializedName("messageType")
    var messageType:String?=null,
    @SerializedName("message")
    var message:String?=null
)