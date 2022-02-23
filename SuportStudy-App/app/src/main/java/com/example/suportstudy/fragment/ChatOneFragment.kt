package com.example.suportstudy.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.example.suportstudy.R
import com.example.suportstudy.adapter.AdapterOneChatlist
import com.example.suportstudy.model.Chat
import com.example.suportstudy.model.Chatlist
import com.example.suportstudy.model.Users
import com.example.suportstudy.service.UserAPI
import com.example.suportstudy.until.Constrain
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatOneFragment : Fragment() {
   var  rcvChatOne:RecyclerView?=null


    var chatlistList: ArrayList<Chatlist>? = null
    var userList: ArrayList<Users>? = null
    var reference: DatabaseReference? = null

    var noMessageLayout: LinearLayout? = null
    var loader: LazyLoader? = null

    var myUid:String?=null

    var adapterOneChatlist:AdapterOneChatlist?=null

    var userAPI:UserAPI?=null
    var searchView:SearchView?=null

    var listSearch:ArrayList<Users>?=null

    var userSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_chat_one, container, false)
        userSharedPreferences = activity!!.getSharedPreferences(Constrain.SHARED_REF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
        myUid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
        initViewData(view)
        searchView!!.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.equals("")){
                   loadChats()
                }else{
                    searchloadChats(query.toString())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.equals("")){
                    loadChats()
                }else{
                    searchloadChats(newText.toString())
                }
                return false
            }
        })
        return view
    }
    fun initViewData(view: View){
        listSearch=ArrayList()
        searchView=view.findViewById(R.id.searchView)
        rcvChatOne=view.findViewById(R.id.rcvChatOne)
        loader=view.findViewById(R.id.myLoader)
        noMessageLayout=view.findViewById(R.id.noDataLayout)
        userAPI=Constrain.createRetrofit(UserAPI::class.java)
        chatlistList=ArrayList()
        userList=ArrayList()
        reference =Constrain.initFirebase("ChatList").child(myUid!!)
        loader!!.visibility=View.VISIBLE
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatlistList!!.clear()
                for (ds in dataSnapshot.children) {
                    val chatlist: Chatlist? = ds.getValue(Chatlist::class.java)
                    chatlistList!!.add(chatlist!!)
                }
                if (chatlistList!!.size==0){
                    noMessageLayout!!.visibility=View.VISIBLE
                    loader!!.visibility=View.GONE
                }else{
                    noMessageLayout!!.visibility=View.GONE
                    loadChats()
                    loader!!.visibility=View.GONE
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    @SuppressLint("UseRequireInsteadOfGet")
    private fun loadChats() {
        userAPI!!.getAllUsers()
            .enqueue(object :Callback<List<Users>>{
                override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                    if(response.isSuccessful){
                    var    listUser= response.body()!!
                        userList!!.clear()
                        for (i in listUser.indices){
                            var id=listUser[i]._id
                            for (j in chatlistList!!.indices){
                                if (id.equals(chatlistList!![j].id)) {
                                    userList!!.add(listUser[i])
                                    break
                                }
                            }
                        }
                        adapterOneChatlist =  AdapterOneChatlist( context!!, userList!!   )
                        rcvChatOne!!.adapter = adapterOneChatlist
                        adapterOneChatlist!!.notifyDataSetChanged()
                        for (i in userList!!.indices) {
                            lastMessage(userList!![i]._id) // id  dzAZNw7EBJchnky8eJnrFepjBU73
                        }


                    }
                }
                override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                }
            })
    }
    private fun searchloadChats(query: String) {
        userAPI!!.getAllUsers()
            .enqueue(object :Callback<List<Users>>{
                override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                    if(response.isSuccessful){
                    var    listUser= response.body()!!
                        userList!!.clear()

                        for (i in listUser.indices){
                            var id=listUser[i]._id
                            for (j in chatlistList!!.indices){
                                if (id.equals(chatlistList!![j].id)) {
                                    if (listUser!![i].name.toLowerCase().contains(query.toLowerCase())) {
                                        userList!!.add(listUser[i])
                                    }
                                    break
                                }
                            }
                            adapterOneChatlist =  AdapterOneChatlist( context!!, userList!!   )
                            rcvChatOne!!.adapter = adapterOneChatlist
                            adapterOneChatlist!!.notifyDataSetChanged()
                        }

                        for (i in userList!!.indices) {
                            lastMessage(userList!![i]._id) // id  dzAZNw7EBJchnky8eJnrFepjBU73
                        }
                        if (userList!!.size==0){
                            noMessageLayout!!.visibility=View.VISIBLE
                        }else{
                            noMessageLayout!!.visibility=View.GONE
                        }
                        loader!!.visibility=View.GONE

                    }
                }
                override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                }
            })
    }
    private fun lastMessage(userId: String) { // id  dzAZNw7EBJchnky8eJnrFepjBU73
        val reference = Constrain.initFirebase("Chats")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var theLastMessage = "default"
                for (ds in dataSnapshot.children) {
                    val chat = ds.getValue(Chat::class.java) ?: continue
                    val sender: String = chat.senderUid!!
                    val receiver: String = chat.receiverUid!!
                    if (sender == null || receiver == null) {
                        continue
                    }
                    if (chat.receiverUid.equals(myUid) &&
                        chat.senderUid.equals(userId) ||
                        chat.receiverUid.equals(userId) &&
                        chat.senderUid.equals(myUid)
                    ) {
                        if (chat.messageType.equals("image")) {
                            theLastMessage = "Gửi một ảnh"
                        }
                        if (chat.messageType.equals("text")) {
                            theLastMessage = chat.message!!
                        }
                    }
                }
                adapterOneChatlist!!.setLastMessageMap(
                    userId,
                    theLastMessage
                )

                adapterOneChatlist!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatOneFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}