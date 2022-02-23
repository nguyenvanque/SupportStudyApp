package com.example.suportstudy.activity.document

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.suportstudy.R
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import kotlinx.android.synthetic.main.activity_view_document_detail.*

class ViewDocumentDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_document_detail)

        val intent = intent
        val position = intent.getIntExtra("position", 1)
        pdfview.fromAsset("tai_lieu_android/huong_dan_lap_trinh_voi_android_$position.pdf")
            .defaultPage(0)
            .enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(this))
            .load()
    }
}