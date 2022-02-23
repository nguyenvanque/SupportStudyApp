package com.example.suportstudy.activity.chat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.suportstudy.R
import com.example.suportstudy.activity.group.InfoGroupActivity
import com.example.suportstudy.adapter.GroupChatAdapter
import com.example.suportstudy.apibodymodel.AddNoteBody
import com.example.suportstudy.extensions.gone
import com.example.suportstudy.extensions.visible
import com.example.suportstudy.model.GroupChat
import com.example.suportstudy.model.GroupCourse
import com.example.suportstudy.service.GroupCourseAPI
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.viewmodel.NoteViewModel
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ChatGroupActivity : AppCompatActivity(), GroupChatAdapter.IItemClickedListener {
    var context = this@ChatGroupActivity

    var groupId: String? = null
    var groupCreateBy: String? = null
    var groupName: String? = null
    var groupDescription: String? = null
    var groupImage: String? = null

    var senderUid: String? = null

    var groupChatImage: CircleImageView? = null
    var txtGroupName: TextView? = null
    var btnInfoGroup: ImageView? = null
    var edtMessage: EditText? = null
    var btnSend: ImageView? = null
    lateinit var myLoader: LazyLoader
    lateinit var dataLayout: RelativeLayout
    var groupChatList = ArrayList<GroupChat>()
    var groupCourseAPI: GroupCourseAPI? = null
    var chatGroupRef: DatabaseReference? = null
    var userSharedPreferences: SharedPreferences? = null

    var name: String? = null
    var image: String? = null
    private var addNoteBody = AddNoteBody()
    private lateinit var viewModel: NoteViewModel

    companion object {
        var groupChatAdapter: GroupChatAdapter? = null
        var chatGroup_Recyclerview: RecyclerView? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)
        viewModel =
            ViewModelProvider(this).get(NoteViewModel::class.java)
        userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        senderUid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
        name = userSharedPreferences!!.getString(Constrain.KEY_NAME, "")
        image = userSharedPreferences!!.getString(Constrain.KEY_IMAGE, "")
        initDataView()
        displayMessage()

        btnSend!!.setOnClickListener {
            var message = edtMessage!!.text.toString()
            if (message.equals("")) {
                Constrain.showToast("Hãy nhập tin nhắn...")
            } else {
                sendMessage(message)
                Constrain.hideKeyBoard(context)
            }
            edtMessage!!.setText("")
        }
        btnInfoGroup!!.setOnClickListener {
            var intent = Intent(context, InfoGroupActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupCreateBy", groupCreateBy)
            intent.putExtra("groupImage", groupImage)
            intent.putExtra("groupName", groupName)
            intent.putExtra("groupDescription", groupDescription)
            context.startActivity(intent)
        }
    }

    fun initDataView() {
        Constrain.context = context
        var intentGroupChat = intent
        groupId = intentGroupChat.getStringExtra("groupId")
        groupCreateBy = intentGroupChat.getStringExtra("groupCreateBy")
        groupName = intentGroupChat.getStringExtra("groupName")
        groupDescription = intentGroupChat.getStringExtra("groupDescription")
        groupImage = intentGroupChat.getStringExtra("groupImage")

        groupChatImage = findViewById(R.id.groupChatImage)
        txtGroupName = findViewById(R.id.txtGroupName)
        btnInfoGroup = findViewById(R.id.btnInfoGroup)
        edtMessage = findViewById(R.id.edtMessage)
        btnSend = findViewById(R.id.btnSend)
        myLoader = findViewById(R.id.myLoader)
        dataLayout = findViewById(R.id.dataLayout)
        chatGroup_Recyclerview = findViewById(R.id.chatGroup_Recyclerview)
        chatGroupRef = Constrain.initFirebase("GroupChats")
        groupCourseAPI = Constrain.createRetrofit(GroupCourseAPI::class.java)


        getGroupInfo()


    }

    private fun sendMessage(message: String) {
        var time = System.currentTimeMillis().toString()
        var hashMap = HashMap<String, String>()
        hashMap.put("_id", time)
        hashMap.put("senderUid", senderUid!!)
        hashMap.put("senderName", name!!)
        hashMap.put("timeSend", time)
        hashMap.put("typeMessage", "text")
        hashMap.put("message", message)
        hashMap.put("groupId", groupId!!)

        chatGroupRef!!.child(groupId!!).child("Message").push().setValue(hashMap)
            .addOnCompleteListener(
                {
                    if (it.isSuccessful) {
                        Constrain.showToast("Gửi thành công")
                        getAllUserId(message)
                    }
                })

    }

    private fun displayMessage() {
        chatGroupRef!!.child(groupId!!).child("Message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    groupChatList.clear()
                    for (ds in dataSnapshot.children) {
                        val chat: GroupChat? = ds.getValue(GroupChat::class.java)
                        groupChatList.add(chat!!)
                    }
                    groupChatAdapter =
                        GroupChatAdapter(context, groupChatList, this@ChatGroupActivity)
                    chatGroup_Recyclerview!!.adapter = groupChatAdapter
                    chatGroup_Recyclerview!!.scrollToPosition(groupChatList.size - 1)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun getAllUserId(message: String) {
        groupCourseAPI!!.getAllGroupByID(groupId!!).enqueue(object : Callback<List<GroupCourse>> {
            override fun onResponse(
                call: Call<List<GroupCourse>>,
                response: retrofit2.Response<List<GroupCourse>>
            ) {
                var listJoin = response.body()!![0].participant

                for (i in listJoin!!.indices) {
                    var uid = listJoin[i].uid
                    getToken(
                        message,
                        name!!!!,
                        uid!!,
                        image!!
                    )

                }
            }

            override fun onFailure(call: Call<List<GroupCourse>>, t: Throwable) {
                TODO("Not yet implemented")
            }

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
                    data.put("groupId", groupId)
                    data.put("groupCreateBy", groupCreateBy)
                    data.put("groupName", groupName)
                    data.put("groupDescription", groupDescription)
                    data.put("groupImage", groupImage)
                    data.put("notificationType", "chatGroup")
                    data.put("hisUid", senderUid)
                    data.put("hisName", senderName)
                    data.put("hisImage", myImage)
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

    fun getGroupInfo() {
        myLoader.visible()
        dataLayout.gone()
        groupCourseAPI!!.getAllGroupByID(groupId!!).enqueue(object : Callback<List<GroupCourse>> {
            override fun onResponse(
                call: Call<List<GroupCourse>>,
                response: retrofit2.Response<List<GroupCourse>>
            ) {
                if (response.isSuccessful) {
                    groupName = response.body()!![0].groupName
                    groupCreateBy = response.body()!![0].createBy
                    groupDescription = response.body()!![0].groupDescription
                    groupImage = response.body()!![0].groupImage
                }
                var path = Constrain.subPathImage("group", groupImage!!)
                Constrain.checkShowImage(
                    context,
                    R.drawable.avatar_default,
                    path!!,
                    groupChatImage!!
                )
                txtGroupName!!.text = groupName
                myLoader.gone()
                dataLayout.visible()
            }

            override fun onFailure(call: Call<List<GroupCourse>>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

        })

    }

    override fun onStart() {
        super.onStart()
        getGroupInfo()
    }

    override fun onItemLongClick(content: String) {
        var dialog = Constrain.createDialog(context, R.layout.dialog_confirm)
        var confirmTv = dialog.findViewById<TextView>(R.id.messagCfTv)
        var huyBtn = dialog.findViewById<LinearLayout>(R.id.cancelBtn)
        var dongYBtn = dialog.findViewById<LinearLayout>(R.id.dongyBtn)
        confirmTv.setText("Bạn có muốn ghi chú tin nhắn?")
        dongYBtn.setOnClickListener {
            saveNote(content)
            dialog.dismiss()
        }
        huyBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun saveNote(message: String) {
        userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        val currentUser = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
        addNoteBody.apply {
            title = groupName
            content = message
            userId = currentUser
            isGroupNote = 1
        }
        viewModel.addNote(addNoteBody)
    }

    private fun subscribeUI() {
        viewModel.liveDataAddNoteResponse.observe(this, {
            it?.let {
                viewModel.liveDataAddNoteResponse.value = null
                Toast.makeText(context, "Ghi chú tin nhắn thành công", Toast.LENGTH_SHORT).show()
            }
        })
    }
}