package com.example.suportstudy.model

data class Course (
    var _id:String,
    var name:String,
    var description:String,
    var image:String,
    var courseType: CourseType,
    var createdAt:String
        )