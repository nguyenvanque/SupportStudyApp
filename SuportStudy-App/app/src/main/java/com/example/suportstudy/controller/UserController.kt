package com.example.suportstudy.controller

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.suportstudy.model.Users
import com.example.suportstudy.service.UserAPI
import com.example.suportstudy.until.Constrain
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserController {
   fun getUserProfile(context:Context,uid:String):MutableLiveData<List<Users>>{
       var  userAPI = Constrain.createRetrofit(UserAPI::class.java)
       val dataUsers = MutableLiveData<List<Users>>()
       val chatFetchJob = Job()
       val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
           throwable.printStackTrace()
           Constrain.showToast(context, "Data error")
       }
       val scope = CoroutineScope(chatFetchJob + Dispatchers.Main)

       scope.launch(errorHandler) {
           userAPI!!.getAllUsersByID(uid).enqueue(object : Callback<List<Users>> {
               override fun onResponse(
                   call: Call<List<Users>>,
                   response: Response<List<Users>>
               ) {
                   if (response.isSuccessful) {
                       dataUsers.postValue(response.body())
                   }
               }
               override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                   Log.e("err", t.message.toString())
               }

           })
       }
       return dataUsers
   }
}