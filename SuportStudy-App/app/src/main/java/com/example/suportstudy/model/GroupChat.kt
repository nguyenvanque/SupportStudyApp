package com.example.suportstudy.model

data  class GroupChat (
     var _id:String? = null,
     var senderUid:String? = null,
     var senderName:String? = null,
     val message: String? = null,
     var timeSend:String? = null,
     var typeMessage:String? = null,
     var groupId:String? = null
)