package com.example.suportstudy.service

import com.example.suportstudy.apibodymodel.AddNoteBody
import com.example.suportstudy.apiresponsemodel.NoteResponse
import com.example.suportstudy.model.Note
import com.example.suportstudy.until.Constrain
import retrofit2.Call
import retrofit2.http.*

interface NoteAPI {
    @POST("/api/note/addnote")
    fun addNote(@Body addNoteBody: AddNoteBody):Call<Note>

    @DELETE("/api/note/")
    fun deleteNote(@Query("id") id:String):Call<Note>

    @GET("/api/note/getByType")
    fun getListNote(
        @Query("isGroupNote") isGroupNote: Int,
        @Query("userId") userId: String
    ): Call<NoteResponse>

    companion object {
        operator fun invoke(): NoteAPI {
            return Constrain.createRetrofit(NoteAPI::class.java)
        }
    }
}