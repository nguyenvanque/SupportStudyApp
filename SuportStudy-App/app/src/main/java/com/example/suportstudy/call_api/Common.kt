package com.example.suportstudy.call_api

import android.media.Ringtone
import com.stringee.call.StringeeCall
import java.util.*

object Common {
    var callsMap: HashMap<String, StringeeCall> = HashMap<String, StringeeCall>()
    var audioManager: StringeeAudioManager? = null
    var isInCall = false
    var ringtone: Ringtone? = null
    var isAppInBackground = false
}