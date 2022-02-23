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
import com.example.suportstudy.activity.course.ListCourseActivity
import com.example.suportstudy.model.CourseType
import com.example.suportstudy.until.Constrain
import kotlinx.android.synthetic.main.item_coursetype.view.*

class CourseTypeAdapter (var context: Context, var courseList: List<CourseType>) :RecyclerView.Adapter<CourseTypeAdapter.MyViewHolder>(){

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var courseIv: ImageView? = null
        var txtCourseName: TextView? = null
        var nextIv: ImageView? = null
        init {
            courseIv = itemView.courseIv
            txtCourseName = itemView.txtName
            nextIv = itemView.nextIv
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseTypeAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_coursetype, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseTypeAdapter.MyViewHolder, position: Int) {
        var courseType=courseList.get(position)
        holder.txtCourseName!!.setText(courseType.name)
        Constrain.checkShowImage(context,R.drawable.ic_gallery_grey,courseType.image, holder.courseIv!!)
        holder.itemView.setOnClickListener {
            var intent=Intent(context,ListCourseActivity::class.java)
            intent.putExtra("_id",courseType._id)
            intent.putExtra("name",courseType.name)
            intent.putExtra("image",courseType.image)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}