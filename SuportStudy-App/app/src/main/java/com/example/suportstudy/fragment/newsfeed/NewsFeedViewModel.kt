package com.example.suportstudy.fragment.newsfeed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suportstudy.apiresponsemodel.NewsFeedResponse
import com.example.suportstudy.model.NewsFeed
import com.example.suportstudy.service.NewsFeedAPI
import com.example.suportstudy.until.Constrain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFeedViewModel : ViewModel() {

    val list = MutableLiveData<ArrayList<NewsFeed>>()

    fun getAllProducts(){
        val newsFeedApi = Constrain.createRetrofit(NewsFeedAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            newsFeedApi.getAllNewsFeed().enqueue(object : Callback<NewsFeedResponse> {
                override fun onResponse(call: Call<NewsFeedResponse>, response: Response<NewsFeedResponse>) {
                    list.postValue(response.body()!!.data)
                }
                override fun onFailure(call: Call<NewsFeedResponse>, t: Throwable) {
                    Log.d("son", t.toString())
                }
            })
        }
    }
}