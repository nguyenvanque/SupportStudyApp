package com.example.suportstudy.model

data  class GroupCourse (
    var _id:String?=null,
    var createBy:String?=null,
    var groupName:String?=null,
    var groupDescription:String?=null,
    var groupImage:String?=null,
    var courseId:String?=null,
    var participant:List<Participant>?=null,
        )