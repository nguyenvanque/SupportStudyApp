package com.example.suportstudy.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.activity.chat.ChatGroupActivity
import com.example.suportstudy.model.GroupCourse
import com.example.suportstudy.model.Participant
import com.example.suportstudy.service.GroupCourseAPI
import com.example.suportstudy.until.Constrain
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupAdapter(
    var context: Context,
    var list: List<GroupCourse>, var groupAPI: GroupCourseAPI) : RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var IVGroup: CircleImageView? = null
        var txtJoin: TextView? = null
        var txtGroupName: TextView? = null
        init {
            Constrain.context=context
            IVGroup = itemView.findViewById(R.id.IVGroup)
            txtGroupName = itemView.findViewById(R.id.txtGroupName)
            txtJoin = itemView.findViewById(R.id.txtJoin)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var  group:GroupCourse=list[position]
        showJoinUi(group,holder.txtJoin)
        var groupImage= group.groupImage!!
        holder.txtGroupName!!.text=group.groupName
        var pathImageUrl=""
        if(groupImage.equals("noImage")){
           pathImageUrl=""
        }else{
            pathImageUrl = Constrain.baseUrl + "/group/" + group.groupImage!!.substring(27)
        }

        Constrain.checkShowImage(context,R.drawable.ic_gallery_grey,pathImageUrl, holder.IVGroup!!)

         holder.itemView.setOnClickListener {
             if(holder.txtJoin!!.text.equals("Đã tham gia")){
                 var intent=Intent(context,ChatGroupActivity::class.java)
                 intent.putExtra("groupId",group._id)
                 intent.putExtra("groupCreateBy",group.createBy)
                 intent.putExtra("groupImage",group.groupImage)
                 intent.putExtra("groupName",group.groupName)
                 intent.putExtra("groupDescription",group.groupDescription)
                 context.startActivity(intent)
             }
              else if(holder.txtJoin!!.text.equals("Tham gia")){
                 Constrain.showToast("Bạn chưa tham gia nhóm này")
             }

         }
        holder.txtJoin!!.setOnClickListener {
            if(holder.txtJoin!!.text.equals("Tham gia")){
                var time=System.currentTimeMillis().toString()
                groupAPI.joinGroup(group._id,ActionActivity.uid!!,time)
                    .enqueue(object : Callback<Participant> {
                        override fun onResponse(
                            call:Call<Participant>,
                            response: Response<Participant>
                        ) {
                            if (response.isSuccessful) {
                                holder.txtJoin!!.text="Đã tham gia"
                            }
                        }
                        override fun onFailure(call: Call<Participant>, t: Throwable) {
                            Log.v("Data", "Error: " + t.message.toString())
                        }
                    })
            }
        }

    }

    private fun showJoinUi(group: GroupCourse, txtJoin: TextView?) {
         var joinList=group.participant
         txtJoin!!.text="Tham gia"
        for (i in joinList!!.indices){
            if(joinList[i].uid.equals(ActionActivity.uid))
            {
                txtJoin!!.text="Đã tham gia"
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}