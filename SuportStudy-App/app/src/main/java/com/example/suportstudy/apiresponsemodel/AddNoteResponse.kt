package com.example.suportstudy.apiresponsemodel

import com.example.suportstudy.model.Note
import com.google.gson.annotations.SerializedName

data class AddNoteResponse(
    @SerializedName("payload")
    var data: Note
) {
}