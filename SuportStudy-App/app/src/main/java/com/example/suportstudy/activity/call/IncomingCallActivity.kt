package com.example.suportstudy.activity.call

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.suportstudy.R
import com.example.suportstudy.activity.chat.ChatOneActivity
import com.example.suportstudy.call_api.Common
import com.example.suportstudy.call_api.StringeeAudioManager
import com.example.suportstudy.controller.UserController
import com.example.suportstudy.until.Constrain
import com.stringee.call.StringeeCall
import com.stringee.common.StringeeConstant
import com.stringee.listener.StatusListener
import org.json.JSONObject
import java.util.*

class IncomingCallActivity : AppCompatActivity(), View.OnClickListener {
    private var mLocalViewContainer: FrameLayout? = null
    private var mRemoteViewContainer: FrameLayout? = null
    private var tvFrom: TextView? = null
    private var tvState: TextView? = null
    private var btnAnswer: ImageButton? = null
    private var btnEnd: ImageButton? = null
    private var btnMute: ImageButton? = null
    private var btnSpeaker: ImageButton? = null
    private var btnVideo: ImageButton? = null
    private var btnSwitch: ImageButton? = null
    private var avatarIv: ImageView? = null
    private var vControl: View? = null
    var sendID: String? =
        null
    var receiveID:kotlin.String? = null
    var name:kotlin.String? = null
    var image:kotlin.String? = null


    private var mStringeeCall: StringeeCall? = null
    private var isMute = false
    private var isSpeaker = false
    private var isVideo = false

    private var mMediaState: StringeeCall.MediaState? = null
    private var mSignalingState: StringeeCall.SignalingState? = null

    val REQUEST_PERMISSION_CALL = 1
    var context=this@IncomingCallActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        Constrain.context=context

        Common.isInCall = true

        val callId = intent.getStringExtra("call_id")
        mStringeeCall = Common.callsMap.get(callId)

        mLocalViewContainer = findViewById<View>(R.id.v_local) as FrameLayout
        mRemoteViewContainer = findViewById<View>(R.id.v_remote) as FrameLayout

        tvFrom = findViewById<View>(R.id.tv_from) as TextView
        receiveID = mStringeeCall!!.getFrom()


        tvState = findViewById<View>(R.id.tv_state) as TextView

        avatarIv = findViewById<View>(R.id.avatarIv) as ImageView
        btnAnswer = findViewById<View>(R.id.btn_answer) as ImageButton
        btnAnswer!!.setOnClickListener(this)

        btnEnd = findViewById<View>(R.id.btn_end) as ImageButton
        btnEnd!!.setOnClickListener(this)

        btnMute = findViewById<View>(R.id.btn_mute) as ImageButton
        btnMute!!.setOnClickListener(this)
        btnSpeaker = findViewById<View>(R.id.btn_speaker) as ImageButton
        btnSpeaker!!.setOnClickListener(this)
        btnVideo = findViewById<View>(R.id.btn_video) as ImageButton
        btnVideo!!.setOnClickListener(this)
        btnSwitch = findViewById<View>(R.id.btn_switch) as ImageButton
        btnSwitch!!.setOnClickListener(this)

        UserController.getUserProfile(context,receiveID!!).observe(context,{
            name=it[0].name
            image=it[0].image
            receiveID=it[0]._id
            tvFrom!!.setText(name)
            var path = Constrain.baseUrl + "/profile/" + image!!.substring(image!!.lastIndexOf("/")+1)
            Constrain.checkShowImage(context,R.drawable.avatar_default,path,avatarIv!!)
        })
        avatarIv!!.visibility=View.VISIBLE



        isSpeaker = mStringeeCall!!.isVideoCall()
        btnSpeaker!!.setBackgroundResource(if (isSpeaker) R.drawable.btn_speaker_on else R.drawable.btn_speaker_off)

        vControl = findViewById(R.id.v_control)
        isVideo = mStringeeCall!!.isVideoCall()
        btnVideo!!.setImageResource(if (isVideo) R.drawable.btn_video else R.drawable.btn_video_off)

        btnVideo!!.setVisibility(if (isVideo) View.VISIBLE else View.GONE)
        btnSwitch!!.setVisibility(if (isVideo) View.VISIBLE else View.GONE)

        //create audio manager to control audio device

        //create audio manager to control audio device
        if (Common.audioManager == null) {
            Common.audioManager = StringeeAudioManager.create(this@IncomingCallActivity)
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

        }

        //play device ringtone

        //play device ringtone
        if (Common.ringtone == null) {
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            Common.ringtone = RingtoneManager.getRingtone(this@IncomingCallActivity, ringtoneUri)
            Common.ringtone!!.play()
        }

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
            if (mStringeeCall!!.isVideoCall()) {
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

        initAnswer()
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
                endCall(false, true)
            } else {
                initAnswer()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, ChatOneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("hisName", name)
        intent.putExtra("hisUid", receiveID)
        intent.putExtra("sendID", sendID)
        intent.putExtra("hisImage", image)
        startActivity(intent)
    }

    private fun initAnswer() {
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
                        StringeeCall.SignalingState.ANSWERED -> {
                            tvState!!.setText(R.string.connected)
                            if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                                tvState!!.setText(R.string.connected)

                            }
                        }
                        StringeeCall.SignalingState.ENDED -> {
                            tvState!!.setText(R.string.end_call)

                            endCall(true, false)
                            avatarIv!!.visibility=View.GONE

                        }
                    }
                }
            }

            override fun onError(stringeeCall: StringeeCall, i: Int, s: String) {
                runOnUiThread { Constrain.showToast(s)}
            }

            override fun onHandledOnAnotherDevice(
                stringeeCall: StringeeCall,
                signalingState: StringeeCall.SignalingState,
                s: String
            ) {
                runOnUiThread {
                    if (signalingState == StringeeCall.SignalingState.ANSWERED || signalingState == StringeeCall.SignalingState.BUSY) {
                        Constrain.showToast("This call is handled on another device.")
                        endCall(false, false)
                    }
                }
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
                            tvFrom!!.visibility = View.GONE
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
        mStringeeCall!!.ringing(object : StatusListener() {
            override fun onSuccess() {
                Log.d("Stringee", "send ringing success")
            }
        })
    }

  override  fun onClick(view: View) {
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
                    Common.audioManager!!.setSpeakerphoneOn(isSpeaker)
                }
            }
            R.id.btn_answer -> if (mStringeeCall != null) {
               avatarIv!!.visibility=View.GONE

                Common.audioManager!!.setSpeakerphoneOn(isVideo)
                if (Common.ringtone != null && Common.ringtone!!.isPlaying()) {
                    Common.ringtone!!.stop()
                    Common.ringtone = null
                }
                vControl!!.visibility = View.VISIBLE
                btnAnswer!!.visibility = View.GONE
                mStringeeCall!!.answer()
            }
            R.id.btn_end -> endCall(true, false)
            R.id.btn_video -> {
                isVideo = !isVideo
                btnVideo!!.setImageResource(if (isVideo) R.drawable.btn_video else R.drawable.btn_video_off)
                if (mStringeeCall!! != null) {
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

    private fun endCall(isHangup: Boolean, isReject: Boolean) {
        if (Common.audioManager != null) {
            Common.audioManager!!.stop()
            Common.audioManager = null
        }
        if (Common.ringtone != null && Common.ringtone!!.isPlaying()) {
            Common.ringtone!!.stop()
            Common.ringtone = null
        }
        if (isHangup) {
            if (mStringeeCall != null) {
                mStringeeCall!!.hangup()
            }
        }
        if (isReject) {
            if (mStringeeCall != null) {
                mStringeeCall!!.reject()
            }
        }
        Constrain.postDelay(Runnable {
            Common.isInCall = false
            finish()
        }, 1000)
    }
    }