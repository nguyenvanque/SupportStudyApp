package com.example.suportstudy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.suportstudy.fragment.GroupNoteFragment
import com.example.suportstudy.fragment.SelfNoteFragment

class NoteContainerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var numberTab = 2
    override fun getCount(): Int {
        return numberTab
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return SelfNoteFragment()
            }
            1 -> {
                return GroupNoteFragment()
            }
        }
        return null!!
    }
}