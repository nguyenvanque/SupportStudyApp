package com.example.suportstudy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.suportstudy.R
import com.example.suportstudy.activity.document.ViewDocumentDetailActivity
import com.example.suportstudy.model.Document
import com.example.suportstudy.until.Constrain
import com.makeramen.roundedimageview.RoundedImageView

class DocumentAdapter(var context: Context, var list: ArrayList<Document>) :
    Adapter<DocumentAdapter.ViewHolder>() {

     inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: TextView
        var imageView: RoundedImageView
        init {
            txtTitle = itemView.findViewById(R.id.txt)
            imageView = itemView.findViewById(R.id.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = list[position].title
        var imageUrl=list[position].image

        Constrain.checkShowImage(context,R.drawable.ic_gallery_grey,imageUrl,holder.imageView)
        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, ViewDocumentDetailActivity::class.java)
            intent.putExtra("position", position + 1)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int{
        return list.size
    }


}
