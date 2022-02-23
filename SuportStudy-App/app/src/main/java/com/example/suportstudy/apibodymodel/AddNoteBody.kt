package com.example.suportstudy.apibodymodel
import com.google.gson.annotations.SerializedName

data class AddNoteBody(
    @SerializedName("title")
    var title: String? = null,

    @SerializedName("content")
    var content: String? = null,

    @SerializedName("userId")
    var userId: String? = null,

    @SerializedName("isGroupNote")
    var isGroupNote: Int? = null
) {
}