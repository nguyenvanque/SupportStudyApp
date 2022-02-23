package com.example.suportstudy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.suportstudy.apibodymodel.AddNoteBody
import com.example.suportstudy.apiresponsemodel.NoteResponse
import com.example.suportstudy.database.AppDatabase
import com.example.suportstudy.model.Note
import com.example.suportstudy.service.NoteAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private var noteDAO = AppDatabase.getInstance(application).getNoteDao()
    val liveDataNoteResponse = MutableLiveData<NoteResponse>()
    val liveDataAddNoteResponse = MutableLiveData<Note>()
    val liveDataDeleteNoteResponse = MutableLiveData<Note>()
    val messageFail = MutableLiveData<String>()
    val liveDataNote = MutableLiveData<List<Note>>()
    val liveDataUserAva = MutableLiveData<String>()

    //Get Note from API
    fun getListNote(isGroupNote: Int, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            NoteAPI().getListNote(isGroupNote, userId).enqueue(object : Callback<NoteResponse> {
                override fun onResponse(
                    call: Call<NoteResponse>,
                    response: Response<NoteResponse>
                ) {
                    if (response.isSuccessful) {
                        liveDataNoteResponse.postValue(response.body())
                    } else {
                        messageFail.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                    messageFail.postValue(t.message)
                }

            })
        }
    }

    //Add Note to server
    fun addNote(addNoteBody: AddNoteBody) {
        CoroutineScope(Dispatchers.IO).launch {
            NoteAPI().addNote(addNoteBody).enqueue(object : Callback<Note> {
                override fun onResponse(
                    call: Call<Note>,
                    response: Response<Note>
                ) {
                    if (response.isSuccessful) {
                        liveDataAddNoteResponse.postValue(response.body())
                    } else {
                        messageFail.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<Note>, t: Throwable) {
                    messageFail.postValue(t.message)
                }

            })
        }
    }
    // delete Note on server
    fun deleteNote(id:String){
        CoroutineScope(IO).launch {
            NoteAPI().deleteNote(id).enqueue(object :Callback<Note>{
                override fun onResponse(
                    call: Call<Note>,
                    response: Response<Note>
                ) {
                    if (response.isSuccessful) {
                        liveDataDeleteNoteResponse.postValue(response.body())
                    } else {
                        messageFail.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<Note>, t: Throwable) {
                    messageFail.postValue(t.message)
                }

            })
        }
    }
    //Insert Note to DB
    fun insertNote(listNote: List<Note>) {
        CoroutineScope(Dispatchers.IO).launch {
            noteDAO.clearDataByNoteType(0)
            noteDAO.insert(listNote)
            liveDataNote.postValue(noteDAO.getSelfNote())
        }
    }

    fun insertGroupNote(listNote: List<Note>) {
        CoroutineScope(Dispatchers.IO).launch {
            noteDAO.clearDataByNoteType(1)
            noteDAO.insert(listNote)
            liveDataNote.postValue(noteDAO.getGroupNote())
        }
    }
    fun deleteSelfNoteFromDB(id: String){
        CoroutineScope(IO).launch {
            noteDAO.deleteNoteById(id)
            liveDataNote.postValue(noteDAO.getSelfNote())
        }
    }
    fun deleteGroupNoteFromDB(id: String){
        CoroutineScope(IO).launch {
            noteDAO.deleteNoteById(id)
            liveDataNote.postValue(noteDAO.getGroupNote())
        }
    }
    // Get info User
    fun getUserAva() {
        CoroutineScope(Dispatchers.IO).launch {
            noteDAO.getUserAva()
            liveDataUserAva.postValue(noteDAO.getUserAva())
        }
    }

    fun getSelfNoteFromDB(){
        CoroutineScope(IO).launch {
            liveDataNote.postValue(noteDAO.getSelfNote())
        }
    }

    fun getGroupNoteFromDB(){
        CoroutineScope(IO).launch {
            liveDataNote.postValue(noteDAO.getGroupNote())
        }
    }
}