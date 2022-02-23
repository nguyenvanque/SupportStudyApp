package com.example.suportstudy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.suportstudy.dao.NoteDAO
import com.example.suportstudy.model.Note
import java.io.File

@Database(
    entities = [
       Note::class
    ], version = 1, exportSchema = false
)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getNoteDao():NoteDAO
    companion object {
        private const val DB_NAME = "SUPPORT_STUDY.db"

        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            File(context.applicationContext.getExternalFilesDir("SupportStudy")?.absolutePath).takeIf { !it.exists() }?.mkdirs()
            return instance ?: synchronized(this) {
                val dbTemp = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.applicationContext.getExternalFilesDir("SupportStudy")?.absolutePath + "/" + DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = dbTemp
                dbTemp
            }
        }
    }
}