package com.example.suportstudy.activity.document

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.R
import com.example.suportstudy.activity.course.CourseDetailActivity
import com.example.suportstudy.adapter.DocumentAdapter
import com.example.suportstudy.model.Document

class ListDocumentActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var list: ArrayList<Document>? = null
    var documentAdapter: DocumentAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_document)
        recyclerView = findViewById(R.id.rcvDocument)
        list = ArrayList()
        for (i in 1..23) {
            list!!.add(Document("Bài $i", CourseDetailActivity.imageUrl))
        }
        documentAdapter = DocumentAdapter(applicationContext!!, list!!)
        recyclerView!!.adapter = documentAdapter
    }
}