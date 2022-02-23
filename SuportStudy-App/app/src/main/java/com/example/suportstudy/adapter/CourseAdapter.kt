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
import com.example.suportstudy.activity.course.CourseDetailActivity
import com.example.suportstudy.model.Course
import com.example.suportstudy.until.Constrain

class CourseAdapter(var context: Context,var courseList: List<Course>) :RecyclerView.Adapter<CourseAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var courseIv: ImageView? = null
        var txtCourseName: TextView? = null
        var nextIv: ImageView? = null
        init {
            courseIv = itemView.findViewById(R.id.courseIv)
            txtCourseName = itemView.findViewById(R.id.txtName)
            nextIv = itemView.findViewById(R.id.nextIv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_coursetype, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var course=courseList[position]
        var name=course.name
        var desciption=course.description
        var imageUrl=course.image
        holder.txtCourseName!!.text=name

        Constrain.checkShowImage(context,R.drawable.ic_gallery_grey,imageUrl,holder.courseIv!!)

        holder.itemView.setOnClickListener {
            var  intent= Intent(context,CourseDetailActivity::class.java)
            intent.putExtra("courseId",course._id)
            intent.putExtra("name",name)
            intent.putExtra("desciption",desciption)
            intent.putExtra("image",imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}