package com.example.suportstudy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.R
import com.example.suportstudy.activity.chat.ChatOneActivity
import com.example.suportstudy.model.Users
import com.example.suportstudy.until.Constrain
import de.hdodenhof.circleimageview.CircleImageView

class AdapterOneChatlist(
    var context: Context,
    var list: List<Users>,

) :RecyclerView.Adapter<AdapterOneChatlist.MyViewHolder>(){
    private val lastMessageMap: HashMap<String, String>? = HashMap()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var chatIv: CircleImageView? = null
        var nameTv: TextView? = null
        var senderName: TextView? = null
        var lastMessageTv: TextView? = null
        init {
            chatIv = itemView.findViewById(R.id.groupChatIv)
            nameTv = itemView.findViewById(R.id.groupNameTv)
            senderName = itemView.findViewById(R.id.senderName)
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_group_chatlist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var user=list[position]

        val userName: String = user.name
        val userId: String = user._id
        val userImage: String = user.image

        holder.senderName!!.visibility=View.GONE
        val lastMessage = lastMessageMap!![userId]

        holder.nameTv!!.text = userName
        if (lastMessage == null || lastMessage == "default") {
            holder.lastMessageTv!!.visibility = View.GONE
        } else {
            holder.lastMessageTv!!.visibility = View.VISIBLE
            holder.lastMessageTv!!.text = lastMessage
        }
        var pathImageUser=Constrain.subPathImage("profile",userImage)
        Constrain.checkShowImage(context,R.drawable.avatar_default,pathImageUser, holder.chatIv!!)

        holder.itemView.setOnClickListener {
            var intent = Intent(context, ChatOneActivity::class.java)
            intent.putExtra("hisUid", userId)
            intent.putExtra("hisName", userName)
            intent.putExtra("hisImage", userImage)
            context.startActivity(intent)
        }
    }
    fun setLastMessageMap(userId: String?, lastMessage: String?) {
        lastMessageMap!![userId!!] = lastMessage!!
    }
    override fun getItemCount(): Int {
       return list.size
    }
}