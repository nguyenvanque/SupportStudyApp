package com.example.suportstudy.model

data class Users(
    var _id:String,
    var name:String,
    var image:String,
    var email:String,
    var password: String?,
    var isTurtor:Boolean,
)