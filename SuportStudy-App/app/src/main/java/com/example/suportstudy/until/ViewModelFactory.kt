package com.example.suportstudy.until

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.suportstudy.fragment.addNewsFeed.AddsNewsFeedViewModel
import com.example.suportstudy.fragment.newsfeed.NewsFeedViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(NewsFeedViewModel::class.java) -> NewsFeedViewModel()
                isAssignableFrom(AddsNewsFeedViewModel::class.java) -> AddsNewsFeedViewModel()
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}