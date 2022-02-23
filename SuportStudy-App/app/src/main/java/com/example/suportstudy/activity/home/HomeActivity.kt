package com.example.suportstudy.activity.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.suportstudy.R
import com.example.suportstudy.extensions.onClick
import com.example.suportstudy.fragment.ChatContainerFragment
import com.example.suportstudy.fragment.NoteFragment
import com.example.suportstudy.fragment.addNewsFeed.AddNewsFeedFragment
import com.example.suportstudy.fragment.newsfeed.NewsFeedFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {



    val fragmentNewsFeed: Fragment = NewsFeedFragment()
    val fragmentAddsNewsFeed: Fragment = AddNewsFeedFragment()
    val fragmentChat: Fragment = ChatContainerFragment()
    val fragmentNote: Fragment = NoteFragment()
    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = fragmentNewsFeed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        fm.beginTransaction().add(R.id.mainContainer, fragmentNewsFeed, "1").commit()
        fm.beginTransaction().add(R.id.mainContainer, fragmentChat, "2").hide(fragmentChat).commit()
        fm.beginTransaction().add(R.id.mainContainer, fragmentNote, "3").hide(fragmentNote).commit()
        btnDocument.onClick {
            fm.beginTransaction().hide(active).show(fragmentNote).commit()
            active = fragmentNote
        }
        btnChat.onClick {
            fm.beginTransaction().hide(active).show(fragmentChat).commit()
            active = fragmentChat
        }
        btnHome.onClick {
            fm.beginTransaction().hide(active).show(fragmentNewsFeed).commit()
            active = fragmentNewsFeed
//            val intent = Intent(this, HomeNavActivity::class.java)
//            startActivity(intent)
        }


    }


}