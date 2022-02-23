package com.example.suportstudy.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.suportstudy.R
import com.example.suportstudy.adapter.NoteContainerAdapter
import com.example.suportstudy.databinding.FragmentNoteBinding
import com.example.suportstudy.extensions.onClick
import com.example.suportstudy.extensions.push
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.bottom_navigation.*


class   NoteFragment : Fragment() {
    companion object {

    }

    private lateinit var binding: FragmentNoteBinding

    private var viewPagerChat: ViewPager? = null
    private var tabLayout: TabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        binding.menu.setOnClickListener {
            activity?.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        init()
//        subscribeUI()
//        subscribeUserAva()
//        getNote()
//        setDataToNoteRecyclerView()
//        Constrain.hideKeyBoard(activity as Activity)
        txtNote.setTextColor(resources.getColor(R.color.blueIcon))
        imageNote.setImageResource(R.drawable.ic_book_blue)
        btnChat.onClick {
            push(R.id.action_noteFragment_to_chatFragment)
        }
        btnHome.onClick {
            push(R.id.action_noteFragment_to_newsFeedFragment)
        }
        setUpUI()
    }


    private fun setUpUI() {
        viewPagerChat = binding.viewpagerChat
        tabLayout = binding.tablayoutChat
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Cá nhân"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Nhóm"))


        val adapter = NoteContainerAdapter(childFragmentManager)
        viewPagerChat!!.adapter = adapter
        viewPagerChat!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPagerChat!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}