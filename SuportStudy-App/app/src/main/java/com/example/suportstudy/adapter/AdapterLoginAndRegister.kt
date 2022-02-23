package com.example.italkapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.suportstudy.fragment.LoginFragment
import com.example.suportstudy.fragment.RegisterFragment


class AdapterLoginAndRegister(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {
    var numberTab = 2
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return LoginFragment()
            }
            1 -> {
                return RegisterFragment()
            }
        }
        return null!!
    }

    override fun getCount(): Int {
        return numberTab
    }
}