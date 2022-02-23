package com.example.suportstudy.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.activity.chat.ChatGroupActivity
import com.example.suportstudy.model.GroupChat
import com.example.suportstudy.until.Constrain
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*

class GroupChatAdapter(
    var context: Context,
    var chatList: List<GroupChat>,
    private val listener: IItemClickedListener
) : RecyclerView.Adapter<GroupChatAdapter.MyViewHolder>() {
    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var profileIv: ImageView? = null
        var messageIv: ImageView? = null
        var messageTv: TextView? = null
        var timeTv: TextView? = null
        var isSeenTv: TextView? = null
        var messageLayout: LinearLayout? = null

        init {
            profileIv = itemView.findViewById(R.id.profileIv)
            messageTv = itemView.findViewById(R.id.messageTv)
            timeTv = itemView.findViewById(R.id.timeTv)
            isSeenTv = itemView.findViewById(R.id.isSeendTv)
            messageLayout = itemView.findViewById(R.id.messageLayout)
            messageIv = itemView.findViewById(R.id.messageIv)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false)
            return MyViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false)
            return MyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Constrain.context = context
        val message: String = chatList.get(position).message!!
        val timeStamp: String = chatList.get(position).timeSend!!
        val type: String = chatList.get(position).typeMessage!!
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timeStamp.toLong()
        val dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()

        // gán dữ liệu
        if (type == "text") {
            holder.messageTv!!.visibility = View.VISIBLE
            holder.messageIv!!.visibility = View.GONE
            holder.messageTv!!.text = message
        } else {
            holder.messageTv!!.visibility = View.GONE
            holder.messageIv!!.visibility = View.VISIBLE
            Picasso.with(context).load(message).placeholder(R.drawable.ic_gallery_grey)
                .into(holder.messageIv)
        }
        // gán dữ liệu
        holder.messageTv!!.text = message
        holder.timeTv!!.text = dateTime

        holder.itemView.setOnClickListener {
            var dialog = Constrain.createDialog(context, R.layout.dialog_confirm)
            var confirmTv = dialog.findViewById<TextView>(R.id.messagCfTv)
            var huyBtn = dialog.findViewById<LinearLayout>(R.id.cancelBtn)
            var dongYBtn = dialog.findViewById<LinearLayout>(R.id.dongyBtn)
            confirmTv.setText("Bạn có muốn xóa tin nhắn ?")
            dongYBtn.setOnClickListener {
                deleteMessage(chatList[position].groupId!!, chatList[position]._id)
                dialog.dismiss()
            }
            huyBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        holder.itemView.setOnLongClickListener {
            listener.onItemLongClick(message)
            false
        }
    }

    private fun deleteMessage(groupId: String, _id: String?) {
        var userSharedPreferences = context.getSharedPreferences(
            Constrain.SHARED_REF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
        var uid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
        var chatRef = Constrain.initFirebase("GroupChats")
        val query: Query = chatRef.child(groupId).child("Message").orderByChild("_id")
            .equalTo(_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    if (ds.child("senderUid").value == uid) {
                        ds.ref.removeValue().addOnSuccessListener {
                            Constrain.showToast("Xóa thành công")
                            ChatGroupActivity.groupChatAdapter!!.notifyDataSetChanged()
                            ChatGroupActivity.chatGroup_Recyclerview!!.scrollToPosition(chatList.size - 1)
                        }
                    } else {
                        Constrain.showToast("Bạn chỉ xóa tin nhắn của bạn")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Constrain.showToast("Xóa thất bạn")
            }
        })


    }

    override fun getItemViewType(position: Int): Int {

        return if (chatList[position].senderUid.equals(ActionActivity.uid)) {
            return MSG_TYPE_RIGHT
        } else {
            return MSG_TYPE_LEFT
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    interface IItemClickedListener {
        fun onItemLongClick(content: String)
    }
}