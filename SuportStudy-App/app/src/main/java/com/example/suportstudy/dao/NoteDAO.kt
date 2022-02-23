package com.example.suportstudy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.suportstudy.model.Note

@Dao
interface NoteDAO {

    //INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<Note>)

    //QUERY
    @Query("SELECT * FROM NOTE WHERE IS_GROUP_NOTE = 0")
    suspend fun getSelfNote(): List<Note>

    @Query("SELECT * FROM NOTE WHERE IS_GROUP_NOTE = 1")
    suspend fun getGroupNote(): List<Note>

    @Query("SELECT USER_IMAGE FROM NOTE")
    suspend fun getUserAva(): String

    @Query("DELETE FROM NOTE")
    suspend fun clearDataSelfNote()
    @Query("DELETE FROM NOTE WHERE IS_GROUP_NOTE =:isGroupNote")
    suspend fun clearDataByNoteType(isGroupNote:Int)
    @Query("DELETE FROM NOTE WHERE ID =:id")
    suspend fun deleteNoteById(id: String)
}