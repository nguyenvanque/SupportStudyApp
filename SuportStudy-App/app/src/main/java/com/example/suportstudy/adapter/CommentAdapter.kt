package com.example.suportstudy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.model.NewsFeedById
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.until.TimeConverter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_comments.view.*


class CommentAdapter(var context: Context, var list: NewsFeedById, var layout: Int) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return CommentAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!list.comment?.get(position)?.userId?.image.equals("noImage")) {
            var pathImageUrlUser = Constrain.baseUrl + "/post/" + list.comment?.get(position)?.userId?.image?.substring(27)
            Picasso.with(context).load(pathImageUrlUser).into(holder.avatarIv)
        }
        holder.nameTv!!.text = list.comment?.get(position)?.userId?.name
        holder.commentTv!!.text = list?.comment?.get(position)?.content
        holder.timeTv!!.text = list?.comment?.get(position)?.createdAt?.let {
            TimeConverter.convertToDateTime(it)
        }
    }

    override fun getItemCount(): Int {
        return list.comment!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatarIv: ImageView? = null
        var nameTv: TextView? = null
        var commentTv: TextView? = null
        var timeTv: TextView? = null

        init {
            avatarIv = itemView.avatarIv
            nameTv = itemView.nameTv
            commentTv = itemView.commentTv
            timeTv = itemView.timeTv
        }
    }
}