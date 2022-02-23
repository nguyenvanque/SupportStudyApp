package com.example.suportstudy.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.suportstudy.R
import com.example.suportstudy.activity.acount.LoginAndRegisterMainActivity
import com.example.suportstudy.activity.quizz.QuizzActivity
import com.example.suportstudy.until.Constrain
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val context=this@MainActivity
    var isTutor=false
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        val isLogin = sharedPreferences!!.getBoolean(Constrain.KEY_LOGIN, false)
        if (isLogin == true) {
            val intent = Intent(context, ActionActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnTutor.setOnClickListener {
            var intent=Intent(context, QuizzActivity::class.java)
            startActivity(intent)
        }
        btnMember.setOnClickListener {
            isTutor=false
            var intent=Intent(context, LoginAndRegisterMainActivity::class.java)
            intent.putExtra("is" +
                    "Tutor", isTutor)
            startActivity(intent)
        }
        btnLoginMain.setOnClickListener {
            var intent=Intent(context, LoginAndRegisterMainActivity::class.java)
            intent.putExtra("positionRegister", 0)
            startActivity(intent)
        }
    }
}