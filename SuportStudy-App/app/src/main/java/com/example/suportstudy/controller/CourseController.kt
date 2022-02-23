package com.example.suportstudy.controller

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.example.suportstudy.model.Course
import com.example.suportstudy.model.CourseType
import com.example.suportstudy.service.CourseAPI
import com.example.suportstudy.service.CourseTypeAPI
import com.example.suportstudy.until.Constrain
import kotlinx.coroutines.*

object CourseController {
    fun getAllCourse(context: Activity): MutableLiveData<List<Course>> {
        var  courseAPI = Constrain.createRetrofit(CourseAPI::class.java)
        val coursedata = MutableLiveData<List<Course>>()
        val chatFetchJob = Job()
        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
        val scope = CoroutineScope(chatFetchJob + Dispatchers.IO)
        scope.launch(errorHandler) {
            val responce = courseAPI!!.getAllCourse()
            if(responce.isSuccessful){
                coursedata.postValue(responce.body())
            }
        }
        return coursedata
    }




    fun getAllCourseType(context: Activity): MutableLiveData<List<CourseType>> {
        var  courseTypeAPI = Constrain.createRetrofit(CourseTypeAPI::class.java)
        val coursedata = MutableLiveData<List<CourseType>>()
        val chatFetchJob = Job()
        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
        val scope = CoroutineScope(chatFetchJob + Dispatchers.IO)
        scope.launch(errorHandler) {
            var responce=courseTypeAPI.getAllCourseType()
            if(responce.isSuccessful){
                coursedata.postValue(responce.body())
            }
        }
        return coursedata
    }

}