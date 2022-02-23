package com.example.suportstudy.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.suportstudy.R
import com.example.suportstudy.activity.acount.ProfileActivity
import com.example.suportstudy.activity.call.IncomingCallActivity
import com.example.suportstudy.activity.course.CourseTypeActivity
import com.example.suportstudy.call_api.Common
import com.example.suportstudy.call_api.GenAccessToken
import com.example.suportstudy.until.Constrain
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.stringee.StringeeClient
import com.stringee.call.StringeeCall
import com.stringee.call.StringeeCall2
import com.stringee.exception.StringeeError
import com.stringee.listener.StringeeConnectionListener
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject

class ActionActivity : AppCompatActivity(), android.view.View.OnClickListener, LifecycleObserver {
    var context = this@ActionActivity
    lateinit var btnDisscus: CardView
    lateinit var btnTopic: CardView
    lateinit var txtName: TextView
    lateinit var avatarIv: CircleImageView

    var userSharedPreferences: SharedPreferences? = null
    var database: DatabaseReference? = null

    var tokenBase = ""
    private var sharedPreferences: SharedPreferences? = null
    var token: String? = null
    private var editor: SharedPreferences.Editor? = null
    private val PREF_NAME = "com.example.suportstudy"
    private val IS_TOKEN_REGISTERED = "is_token_registered"
    private val TOKEN = "token"

    companion object {
        var uid: String? = null
        var name: String? = null
        var image: String? = null
        var istutor: Boolean = false
        var client: StringeeClient? = null

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("AppLifecycle", "App in background")
        Common.isAppInBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("AppLifecycle", "App in foreground")
        Common.isAppInBackground = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action)
        getReference()
        initViewData()
        btnDisscus.setOnClickListener {
            Constrain.nextActivity(context, HomeNavActivity::class.java)
        }

        btnTopic.setOnClickListener {
            Constrain.nextActivity(context, CourseTypeActivity::class.java)

        }
        avatarIv!!.setOnClickListener {
            Constrain.nextActivity(context, ProfileActivity::class.java)
        }
        txtName.text = name
    }

    private fun initViewData() {
        Constrain.context = context
        btnTopic = findViewById(R.id.btnTopic)
        btnDisscus = findViewById(R.id.btnDisscus)
        txtName = findViewById(R.id.txtName)
        avatarIv = findViewById(R.id.avatarIv)

        database = Constrain.initFirebase("Users")
        token = GenAccessToken.genAccessToken(uid)
        updateToken(FirebaseInstanceId.getInstance().getToken())
        setupNotification()
    }

    fun getReference() {
        userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        uid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")!!
        name = userSharedPreferences!!.getString(Constrain.KEY_NAME, "")!!
        image = userSharedPreferences!!.getString(Constrain.KEY_IMAGE, "noImage")!!
        istutor = userSharedPreferences!!.getBoolean(Constrain.KEY_ISTUTOR, false)!!
    }

    fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance(Constrain.firebaseUrl).getReference("Tokens")
        ref.child(uid!!).child("token").setValue(token)
    }

    fun initAndConnectStringee() {
        client = StringeeClient(this)
        client!!.setConnectionListener(object : StringeeConnectionListener {
            override fun onConnectionConnected(
                stringeeClient: StringeeClient,
                isReconnecting: Boolean
            ) {
                val isTokenRegistered = sharedPreferences!!.getBoolean(IS_TOKEN_REGISTERED, false)
                if (!isTokenRegistered) {
                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.d("Stringee", "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }
                            tokenBase = task.result!!.token
//                            client!!.registerPushToken(
//                                tokenBase,
//                                object : StatusListener() {
//                                    override fun onSuccess() {
//                                        database!!.child(uid!!).child("token")
//                                            .setValue(tokenBase)
//                                        editor!!.putBoolean(IS_TOKEN_REGISTERED, true)
//                                        editor!!.putString(TOKEN, token)
//                                        editor!!.commit()
//                                    }
//
//                                    override fun onError(error: StringeeError) {
//                                        Log.d(
//                                            "Stringee",
//                                            "Register push token unsuccessfully: " + error.getMessage()
//                                        )
//                                    }
//                                })
                        })
                }
            }

            override fun onConnectionDisconnected(
                stringeeClient: StringeeClient,
                isReconnecting: Boolean
            ) {
                runOnUiThread {
                    //                        Utils.reportMessage(MainActivity.this, "Bị mất kết nối");
                }
            }

            override fun onIncomingCall(stringeeCall: StringeeCall) {
                if (Common.isInCall) {
                    stringeeCall.hangup()
                } else {
                    Common.callsMap.put(stringeeCall.callId, stringeeCall)
                    val intent = Intent(context, IncomingCallActivity::class.java)
                    intent.putExtra("call_id", stringeeCall.callId)
                    startActivity(intent)
                }
            }

            override fun onIncomingCall2(stringeeCall2: StringeeCall2) {}
            override fun onConnectionError(
                stringeeClient: StringeeClient,
                stringeeError: StringeeError
            ) {
                Log.d("Stringee", "StringeeClient fails to connect: " + stringeeError.getMessage())
                runOnUiThread {
                    Constrain.showToast("Lỗi kết nối: " + stringeeError.getMessage())
                }
            }

            override fun onRequestNewToken(stringeeClient: StringeeClient) {
                // Get new token here and connect to Stringe server
            }

            override fun onCustomMessage(s: String, jsonObject: JSONObject) {}
            override fun onTopicMessage(s: String, jsonObject: JSONObject) {}
        })
        client!!.connect(token)
    }

    private fun setupNotification() {
        requiredPermissions()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm?.cancel(44448888)
        initAndConnectStringee()
    }


    private fun requiredPermissions() {
        ActivityCompat.requestPermissions(
            context, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ), 1
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(
                    this,
                    "Vui lòng cấp quyền để thực hiện Video Call!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //Block
    override fun onBackPressed() {}
    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        getReference()
        txtName.text = name
    }
}