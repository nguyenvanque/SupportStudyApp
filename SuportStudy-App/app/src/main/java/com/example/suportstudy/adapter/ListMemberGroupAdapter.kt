package com.example.suportstudy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.activity.chat.ChatOneActivity
import com.example.suportstudy.activity.group.InfoGroupActivity
import com.example.suportstudy.model.Users
import com.example.suportstudy.until.Constrain

class ListMemberGroupAdapter(
    var context: Context,
    var listUsers: List<Users>
) : RecyclerView.Adapter<ListMemberGroupAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var avatarIv: ImageView? = null
        var nameTv: TextView? = null
        var statusTv: TextView? = null

        init {
            Constrain.context=context
            avatarIv = itemView.findViewById(R.id.avatarIv)
            nameTv = itemView.findViewById(R.id.nameTv)
            statusTv = itemView.findViewById(R.id.statusTv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_user_group, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var users = listUsers[position]
        var name = users.name
        var imageUrl = users.image

        var pathImageUser = Constrain.baseUrl + "/profile/" + imageUrl.substring(imageUrl.lastIndexOf("/")+1)
        Constrain.checkShowImage(context,R.drawable.avatar_default,pathImageUser,holder.avatarIv!!)

        holder.nameTv!!.text = name
        if (users.isTurtor == true && users._id.equals(InfoGroupActivity.groupCreateBy)) {
            if (users._id.equals(ActionActivity.uid)) {
                holder.statusTv!!.text = "Tôi"
            } else {
                holder.statusTv!!.text = "Quản trị viên"
            }
        } else {
            if (users._id.equals(ActionActivity.uid)) {
                holder.statusTv!!.text = "Tôi"
            } else {
                holder.statusTv!!.text = "Thành viên thường"
            }

        }

        holder.itemView.setOnClickListener {
            if (holder.statusTv!!.text.equals("Tôi")) {
                Constrain.showToast("Bạn không thể gửi tin nhắn cho bạn")
            } else {
                var intent = Intent(context, ChatOneActivity::class.java)
                intent.putExtra("hisUid", users._id)
                intent.putExtra("hisName", users.name)
                intent.putExtra("hisImage", users.image)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }
}