package com.example.suportstudy.model

import androidx.room.*
import com.example.suportstudy.apiresponsemodel.UserResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "NOTE")
data class Note(
    @SerializedName("_id")
    @PrimaryKey
    @ColumnInfo(name = "ID")
    var id: String,

    @SerializedName("title")
    @ColumnInfo(name = "TITLE")
    var title: String? = null,

    @SerializedName("content")
    @ColumnInfo(name = "CONTENT")
    var content: String? = null,

    @Embedded
    @SerializedName("userId")
    var users: UserResponse? = null,

    @SerializedName("isGroupNote")
    @ColumnInfo(name = "IS_GROUP_NOTE")
    var isGroupNote: Int? = null,

    @SerializedName("createdAt")
    @ColumnInfo(name = "CREATED_AT")
    var createdAt: String? = null,

    @SerializedName("updatedAt")
    @ColumnInfo(name = "UPDATED_AT")
    var updatedAt: String? = null
) : Serializable {
}