package com.example.suportstudy.apibodymodel

import com.google.gson.annotations.SerializedName

data class GetNoteBody(
    @SerializedName("userId")
    var userId:String? = null
) {
}