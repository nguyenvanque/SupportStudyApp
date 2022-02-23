package com.example.suportstudy.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.suportstudy.model.Note
import com.example.suportstudy.until.TimeConverter

@BindingAdapter("setCreateDate")
fun TextView.setCreateDate(note: Note) {
    val createDate = note.createdAt
    text = if (!createDate.isNullOrEmpty()) {
        TimeConverter.convertToDateTime(createDate)
    } else {
        ""
    }
}
