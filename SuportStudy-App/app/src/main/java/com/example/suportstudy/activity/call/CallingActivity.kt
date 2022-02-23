package com.example.suportstudy.activity.call

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.activity.chat.ChatOneActivity
import com.example.suportstudy.call_api.Common
import com.example.suportstudy.call_api.StringeeAudioManager
import com.example.suportstudy.until.Constrain
import com.stringee.call.StringeeCall
import com.stringee.common.StringeeConstant
import com.stringee.listener.StatusListener
import org.json.JSONObject
import java.util.*

class CallingActivity : AppCompatActivity(), View.OnClickListener {
    var context=this@CallingActivity
    private var mLocalViewContainer: FrameLayout? = null
    private var mRemoteViewContainer: FrameLayout? = null
    private var tvTo: TextView? = null
    private var tvState: TextView? = null
    private var btnMute: ImageButton? = null
    private var btnSpeaker: ImageButton? = null
    private var btnVideo: ImageButton? = null
    private var btnSwitch: ImageButton? = null
    private var avatarIv: ImageView? = null
    private var mStringeeCall: StringeeCall? = null
    private var from: String? = null
    private var to: String? = null
    var sendID: String? =null
         var receiveID:kotlin.String? = null
    var name:kotlin.String? = null
    var image:kotlin.String? = null
    var isVideoCall = false
    private var isMute = false
    private var isSpeaker = false
    private var isVideo = false

    private var mMediaState: StringeeCall.MediaState? = null
    private var mSignalingState: StringeeCall.SignalingState? = null

    val REQUEST_PERMISSION_CALL = 1
    var uid:String?=null

    var userSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        uid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
        Common.isInCall = true

        from = intent.getStringExtra("from")
        to = intent.getStringExtra("to")
        isVideoCall = intent.getBooleanExtra("is_video_call", false)
        name = intent.getStringExtra("hisName")
        receiveID = intent.getStringExtra("hisUid")
        sendID = uid
        image = intent.getStringExtra("hisImage")

        mLocalViewContainer = findViewById<View>(R.id.v_local) as FrameLayout
        mRemoteViewContainer = findViewById<View>(R.id.v_remote) as FrameLayout

        tvTo = findViewById<View>(R.id.tv_to) as TextView
        tvTo!!.setText(name)

        tvState = findViewById<View>(R.id.tv_state) as TextView

        avatarIv = findViewById<View>(R.id.avatarIv) as ImageView
        btnMute = findViewById<View>(R.id.btn_mute) as ImageButton
        btnMute!!.setOnClickListener(context)
        btnSpeaker = findViewById<View>(R.id.btn_speaker) as ImageButton
        btnSpeaker!!.setOnClickListener(this)
        btnVideo = findViewById<View>(R.id.btn_video) as ImageButton
        btnVideo!!.setOnClickListener(this)
        btnSwitch = findViewById<View>(R.id.btn_switch) as ImageButton
        btnSwitch!!.setOnClickListener(this)
        isSpeaker =isVideoCall
        btnSpeaker!!.setBackgroundResource(if (isSpeaker) R.drawable.btn_speaker_on else R.drawable.btn_speaker_off)

        isVideo = isVideoCall
        btnVideo!!.setImageResource(if (isVideo) R.drawable.btn_video else R.drawable.btn_video_off)

        btnVideo!!.setVisibility(if (isVideo) View.VISIBLE else View.GONE)
        btnSwitch!!.setVisibility(if (isVideo) View.VISIBLE else View.GONE)

        val btnEnd = findViewById<View>(R.id.btn_end) as ImageButton
        btnEnd.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val lstPermissions: MutableList<String> = ArrayList()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                lstPermissions.add(Manifest.permission.RECORD_AUDIO)
            }
            if (isVideoCall) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    lstPermissions.add(Manifest.permission.CAMERA)
                }
            }
            if (lstPermissions.size > 0) {
                val permissions = arrayOfNulls<String>(lstPermissions.size)
                for (i in lstPermissions.indices) {
                    permissions[i] = lstPermissions[i]
                }
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_PERMISSION_CALL
                )
                return
            }
        }

        //create audio manager to control audio device

        //create audio manager to control audio device
        Common.audioManager = StringeeAudioManager.create(context)
        Common.audioManager!!.start(object : StringeeAudioManager.AudioManagerEvents {
            override fun onAudioDeviceChanged(
                selectedAudioDevice: StringeeAudioManager.AudioDevice?,
                availableAudioDevices: Set<StringeeAudioManager.AudioDevice>?
            ) {
                Log.d(
                    "StringeeAudioManager",
                    "onAudioManagerDevicesChanged: " + availableAudioDevices + ", "
                            + "selected: " + selectedAudioDevice
                )
            }

        })

        Common!!.audioManager!!.setSpeakerphoneOn(isVideoCall)
        var path = Constrain.baseUrl + "/profile/" + image!!.substring(image!!.lastIndexOf("/")+1)
        Constrain.checkShowImage(context,R.drawable.avatar_default,path,avatarIv!!)
        makeCall()

    }

    override fun onBackPressed() {
        val intent = Intent(this, ChatOneActivity::class.java)
        intent.putExtra("hisName", name)
        intent.putExtra("hisUid", receiveID)
        intent.putExtra("sendID", sendID)
        intent.putExtra("hisImage", image)
        startActivity(intent)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isGranted = false
        if (grantResults.size > 0) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                } else {
                    isGranted = true
                }
            }
        }
        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (!isGranted) {
                finish()
            } else {
                makeCall()
            }
        }
    }

    private fun makeCall() {
        //make new call
        mStringeeCall = StringeeCall(ActionActivity!!.client, from, to)
        mStringeeCall!!.setVideoCall(isVideoCall)
        mStringeeCall!!.setCallListener(object : StringeeCall.StringeeCallListener {
            override fun onSignalingStateChange(
                stringeeCall: StringeeCall,
                signalingState: StringeeCall.SignalingState,
                s: String,
                i: Int,
                s1: String
            ) {
                runOnUiThread {
                    mSignalingState = signalingState
                    when (signalingState) {
                        StringeeCall.SignalingState.CALLING -> {
                            tvState!!.setText(R.string.connecting)
                            avatarIv!!.visibility=View.VISIBLE
                        }
                        StringeeCall.SignalingState.RINGING ->{
                            tvState!!.setText(R.string.ringing)
                            avatarIv!!.visibility=View.VISIBLE

                        }
                        StringeeCall.SignalingState.ANSWERED -> {
                            avatarIv!!.visibility=View.GONE

                            tvState!!.setText(R.string.connected)
                            if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                                tvState!!.setText(R.string.connected)
                                Common.audioManager!!.setSpeakerphoneOn(isVideoCall)
                            }
                        }
                        StringeeCall.SignalingState.BUSY -> {
                            tvState!!.setText(R.string.busy)
                            endCall()
                            tvState!!.setText(R.string.end_call)
                            endCall()
                        }
                        StringeeCall.SignalingState.ENDED -> {
                            tvState!!.setText(R.string.end_call)
                            endCall()
                        }
                    }
                }
            }

            override fun onError(stringeeCall: StringeeCall, i: Int, s: String) {
                runOnUiThread { }
            }

            override fun onHandledOnAnotherDevice(
                stringeeCall: StringeeCall,
                signalingState: StringeeCall.SignalingState,
                s: String
            ) {
            }

            override fun onMediaStateChange(
                stringeeCall: StringeeCall,
                mediaState: StringeeCall.MediaState
            ) {
                runOnUiThread {
                    mMediaState = mediaState
                    if (mediaState == StringeeCall.MediaState.CONNECTED) {
                        if (mSignalingState == StringeeCall.SignalingState.ANSWERED) {
                            //                                tvState.setText("Started");
                            tvState!!.visibility = View.GONE
                            tvTo!!.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onLocalStream(stringeeCall: StringeeCall) {
                runOnUiThread {
                    if (stringeeCall.isVideoCall) {
                        mLocalViewContainer!!.removeAllViews()
                        mLocalViewContainer!!.addView(stringeeCall.localView)
                        stringeeCall.renderLocalView(true)
                    }
                }
            }

            override fun onRemoteStream(stringeeCall: StringeeCall) {
                runOnUiThread {
                    if (stringeeCall.isVideoCall) {
                        mRemoteViewContainer!!.removeAllViews()
                        mRemoteViewContainer!!.addView(stringeeCall.remoteView)
                        stringeeCall.renderRemoteView(false)
                    }
                }
            }

            override fun onCallInfo(stringeeCall: StringeeCall, jsonObject: JSONObject) {}
        })
        mStringeeCall!!.makeCall()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_mute -> {
                isMute = !isMute
                btnMute!!.setBackgroundResource(if (isMute) R.drawable.btn_mute else R.drawable.btn_mic)
                if (mStringeeCall != null) {
                    mStringeeCall!!.mute(isMute)
                }
            }
            R.id.btn_speaker -> {
                isSpeaker = !isSpeaker
                btnSpeaker!!.setBackgroundResource(if (isSpeaker) R.drawable.btn_speaker_on else R.drawable.btn_speaker_off)
                if (Common.audioManager != null) {
                    Common!!.audioManager!!.setSpeakerphoneOn(isSpeaker)
                }
            }
            R.id.btn_end -> endCall()
            R.id.btn_video -> {
                isVideo = !isVideo
                btnVideo!!.setImageResource(if (isVideo) R.drawable.btn_video else R.drawable.btn_video_off)
                if (mStringeeCall != null) {
                    mStringeeCall!!.enableVideo(isVideo)
                    mStringeeCall!!.setQuality(StringeeConstant.QUALITY_FULLHD)
                }
            }
            R.id.btn_switch -> if (mStringeeCall != null) {
                mStringeeCall!!.switchCamera(object : StatusListener() {
                    override fun onSuccess() {}
                })
            }
        }
    }

    private fun endCall() {
        if (Common.audioManager != null) {
            Common.audioManager!!.stop()
            Common.audioManager = null
        }
        mStringeeCall!!.hangup()
        Constrain.postDelay(Runnable {
            Common.isInCall = false
            finish()
        }, 1000)
    }
}
