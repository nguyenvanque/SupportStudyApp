package com.example.suportstudy.apiresponsemodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "USER_RESPONSE")
data class UserResponse(
    @PrimaryKey
    @SerializedName("_id")
    @ColumnInfo(name = "USER_ID")
    var id: String,

    @SerializedName("name")
    @ColumnInfo(name = "USER_NAME")
    var name: String? = null,

    @SerializedName("image")
    @ColumnInfo(name = "USER_IMAGE")
    var image: String? = null
) {
}