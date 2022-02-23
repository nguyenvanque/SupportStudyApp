package com.example.suportstudy.activity.chat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.activity.call.CallingActivity
import com.example.suportstudy.adapter.ChatOneAdapter
import com.example.suportstudy.model.Chat
import com.example.suportstudy.until.Constrain
import com.google.firebase.database.*
import com.stringee.StringeeClient
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatOneActivity : AppCompatActivity() {
    var avatarIv: ImageView? = null
    var btnCall: ImageView? = null

    var txtName: TextView? = null
    var ChatConnectionTV: TextView? = null
    var sendBtn: ImageView? = null
    var messageEt: EditText? = null


    var hisName :String?=null
    var senderUid :String?=null
    var senderName :String?=null
    var senderImage:String?=null
    var receiverUid:String?=null
    var hisImage :String?=null

    var chatRef: DatabaseReference? = null

    var context = this@ChatOneActivity

    val data = MutableLiveData<List<Chat>>()

    var chatList=ArrayList<Chat>()

    companion object{
        var recyclerView: RecyclerView? = null
        var chatAdapter:ChatOneAdapter?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_one)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initDataView()

        displayMessage()

        sendBtn!!.setOnClickListener {
            val message = messageEt!!.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(message)) {
                Constrain.showToast("Nhập tin nhắn")
            } else {

                sendMessage(message)
                Constrain.hideKeyBoard(context)

            }

            messageEt!!.setText("")
        }
        btnCall!!.setOnClickListener {
            val client: StringeeClient = ActionActivity.client!!
            if (client.isConnected) {
                val intent = Intent(context, CallingActivity::class.java)
                intent.putExtra("from", client.userId)
                intent.putExtra("to", receiverUid)
                intent.putExtra("is_video_call", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                //Put for chat
                intent.putExtra("hisName", hisName)
                intent.putExtra("hisUid", receiverUid)
                intent.putExtra("sendID", senderUid)
                intent.putExtra("hisImage", hisImage)
                startActivity(intent)
            } else {
                Constrain.showToast("Không thể kết nối video call. Vui lòng thử lại sau!")

            }
        }

    }
fun initDataView(){
     Constrain.context=context
    chatRef=Constrain.initFirebase("Chats")

     var  userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
    senderUid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
    senderName = userSharedPreferences!!.getString(Constrain.KEY_NAME, "")
    senderImage = userSharedPreferences!!.getString(Constrain.KEY_IMAGE, "noImage")

    var intentChat=intent
    receiverUid=intentChat.getStringExtra("hisUid")
    hisName=intentChat.getStringExtra("hisName")
    hisImage=intentChat.getStringExtra("hisImage")


    recyclerView = findViewById(R.id.chat_Recyclerview)
    avatarIv = findViewById(R.id.avatarIv)
    btnCall = findViewById(R.id.btnCall)
    txtName = findViewById(R.id.txtName)

    sendBtn = findViewById(R.id.senBtn)
    messageEt = findViewById(R.id.messageEt)
    ChatConnectionTV = findViewById(R.id.ChatConnectionTV)
    recyclerView!!.setHasFixedSize(true)

    txtName!!.text=hisName
    var pathImageUsers = Constrain.subPathImage("profile",hisImage!!)
    Constrain.checkShowImage(context,R.drawable.avatar_default,pathImageUsers!!,avatarIv!!)
}
    private fun displayMessage() {
        chatRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (ds in dataSnapshot.children) {

                    val chat: Chat? = ds.getValue(Chat::class.java)
                    if (chat!!.receiverUid.equals(senderUid) && chat.senderUid.equals(receiverUid) ||
                        chat.receiverUid.equals(receiverUid) && chat.senderUid.equals(senderUid)
                    ) {
                        chatList.add(chat!!)
                    }
                }
                chatAdapter = ChatOneAdapter(context, chatList)
                recyclerView!!.adapter = chatAdapter
                recyclerView!!.scrollToPosition(chatList.size - 1)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private fun sendMessage(message: String) {
        var  time=System.currentTimeMillis().toString()
        var  hashMap=HashMap<String, String>()
        hashMap.put("_id", time)
        hashMap.put("senderUid", senderUid!!)
        hashMap.put("senderName", senderName!!)
        hashMap.put("receiverUid", receiverUid!!)
        hashMap.put("timeSend", time)
        hashMap.put("messageType", "text")
        hashMap.put("message", message)
        chatRef!!.push().setValue(hashMap).addOnCompleteListener({
            if (it.isSuccessful) {
                Constrain.showToast("Gửi thành công")
                getToken(message, senderName!!, receiverUid!!, senderImage!!)
            }
        })
        var chatListRef = Constrain.initFirebase("ChatList")
            ?.child(senderUid!!)
            ?.child(receiverUid!!)
        chatListRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatListRef!!.child("id").setValue(receiverUid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        var chatRef2 = Constrain.initFirebase("ChatList")
            .child(receiverUid!!)
            .child(senderUid!!)
        chatRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(senderUid)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private fun getToken(message: String, senderName: String, hisID: String, myImage: String) {
        val database = FirebaseDatabase.getInstance().getReference("Tokens").child(hisID)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val token = snapshot.child("token").value.toString()
                val to = JSONObject()
                val data = JSONObject()
                try {
                    data.put("title", senderName)
                    data.put("message", message)
                    data.put("hisUid", senderUid)
                    data.put("hisName", senderName)
                    data.put("hisImage", myImage)
                    data.put("notificationType", "chatOne")
                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun sendNotification(to: JSONObject) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, Constrain.NOTIFICATION_URL, to,
            Response.Listener { response: JSONObject ->
                Log.d(
                    "notification",
                    "sendNotification: $response"
                )
            },
            Response.ErrorListener { error: VolleyError ->
                Log.d(
                    "notification",
                    "sendNotification: $error"
                )
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map: MutableMap<String, String> = java.util.HashMap()
                map["Authorization"] = "key=" + Constrain.SERVER_KEY
                map["Content-Type"] = "application/json"
                return map
            }
            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }

}





