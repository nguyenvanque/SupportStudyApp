package com.example.suportstudy.fragment.addNewsFeed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suportstudy.model.AddNewsFeed
import com.example.suportstudy.service.NewsFeedAPI
import com.example.suportstudy.until.Constrain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddsNewsFeedViewModel : ViewModel() {

    val postData = MutableLiveData<Boolean>()
    val postLike = MutableLiveData<Boolean>()

    fun addNewsFeedWithImage(des: RequestBody?, userId: RequestBody??, image: MultipartBody.Part?){
        val newsFeedApi = Constrain.createRetrofit(NewsFeedAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            newsFeedApi.createNewsFeedWithImage(des, userId, image).enqueue(object : Callback<AddNewsFeed> {
                override fun onResponse(call: Call<AddNewsFeed>, response: Response<AddNewsFeed>) {
                    postData.postValue(true)
                }
                override fun onFailure(call: Call<AddNewsFeed>, t: Throwable) {
                    postData.postValue(false)
                }
            })
        }
    }

    fun addLike(like: Boolean, userId: String){

    }

//    fun addNewsFeedNoImage(des: String, userId: String?, image: MultipartBody.Part?){
//        val newsFeedApi = Constrain.createRetrofit(NewsFeedAPI::class.java)
//        CoroutineScope(Dispatchers.IO).launch {
//            newsFeedApi.createNewsFeedWithImage(des, userId, image).enqueue(object : Callback<AddNewsFeed> {
//                override fun onResponse(call: Call<AddNewsFeed>, response: Response<AddNewsFeed>) {
//                    Log.d("son", "post thanh cong ${response.body()}")
//                }
//                override fun onFailure(call: Call<AddNewsFeed>, t: Throwable) {
//                    Log.d("son", t.toString())
//                }
//            })
//        }
//    }
}