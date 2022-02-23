package com.example.italkapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.suportstudy.fragment.ChatGroupFragment
import com.example.suportstudy.fragment.ChatOneFragment


class ChatContainerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {
    var numberTab = 2
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return ChatOneFragment()
            }
            1 -> {
                return ChatGroupFragment()
            }
        }
        return null!!
    }

    override fun getCount(): Int {
        return numberTab
    }
}