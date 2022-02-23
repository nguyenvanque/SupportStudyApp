package com.example.suportstudy.until

import java.text.SimpleDateFormat

object TimeConverter {
    fun convertToDateTime(input: String): String {
        val newInput = input.replace("T", " ")
        val finalInput = newInput.substring(0, 22)
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = sdf.parse(finalInput)
        sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(date)
    }
}