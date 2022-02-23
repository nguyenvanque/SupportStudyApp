package com.example.suportstudy.activity.acount

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.italkapp.adapter.AdapterLoginAndRegister
import com.example.suportstudy.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

class LoginAndRegisterMainActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    var po=0
    companion object{
        var isTutor=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_register_main)
        var intent:Intent=intent
        po=intent.getIntExtra("positionRegister",1);
        isTutor=intent.getBooleanExtra("isTutor",false);

        viewPager = findViewById(R.id.viewpager_Login_Register)
        tabLayout = findViewById(R.id.tablayout_Login__Register)
        tabLayout!!.addTab(tabLayout!!.newTab().setText("LOGIN"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("REGISTER"))
        val adapter = AdapterLoginAndRegister(supportFragmentManager)
        viewPager!!.setAdapter(adapter)
        viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        if(po==1){
            viewPager!!.setCurrentItem(1)
        }
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.setCurrentItem(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}