package com.example.suportstudy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suportstudy.R
import com.example.suportstudy.adapter.NoteAdapter
import com.example.suportstudy.databinding.FragmentSelfNoteBinding
import com.example.suportstudy.extensions.gone
import com.example.suportstudy.extensions.visible
import com.example.suportstudy.model.Note
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.viewmodel.NoteViewModel


class SelfNoteFragment : Fragment(), NoteAdapter.OnItemNoteListener {
    private lateinit var viewModel: NoteViewModel
    private lateinit var binding: FragmentSelfNoteBinding
    private var noteAdapter = NoteAdapter(this)
    private lateinit var myUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel =
            ViewModelProvider(this).get(NoteViewModel::class.java)
        binding = FragmentSelfNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        subscribeUI()
        getNote()
        setDataToNoteRecyclerView()
        addNote()
        binding.swRefresh.setOnRefreshListener {
            binding.swRefresh.isRefreshing = false
            getNote()
        }
    }

    private fun init() {
        var userSharedPreferences = activity?.getSharedPreferences(
            Constrain.SHARED_REF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
        myUid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")!!


    }

    private fun subscribeUI() {
        viewModel.apply {
            /** Handle message fail **/
            messageFail.observe(viewLifecycleOwner, Observer {
                messageFail.value = null
                if (!it.isNullOrEmpty()) {
                    Constrain.showErrorMessage(it, requireContext())
                }
            })

            /** List note response **/
            liveDataNoteResponse.observe(viewLifecycleOwner, Observer {
                it?.let {
                    liveDataNoteResponse.value = null
                    if (!it.data.isNullOrEmpty()) {
                        viewModel.insertNote(it.data)
                        binding.myLoader.gone()
                    } else {
                        binding.layoutNoData.visible()
                        binding.myLoader.gone()
                    }
                }
            })
            /** Delete Note response **/
            liveDataDeleteNoteResponse.observe(viewLifecycleOwner, {
                it?.let {
                    liveDataDeleteNoteResponse.value = null
                    if (it != null) {
                        Constrain.showToast("Xoá ghi chú thàng công")
                    } else {
                        Constrain.showErrorMessage("Có lỗi xảy ra", requireContext())
                    }
                }
            })
            /** Observe data from DB **/
            liveDataNote.observe(viewLifecycleOwner,  {
                it?.let {
                    liveDataNote.value = null
                    if (!it.isNullOrEmpty()) {
                        binding.layoutNoData.visibility = View.GONE
                        noteAdapter.notes = it.toMutableList()
                    } else {
                        binding.layoutNoData.visibility = View.VISIBLE
                    }
                }
            })

        }
    }

    private fun getNote() {
        if (Constrain.isConnectedInternet(requireContext())) {
            viewModel.getListNote(isGroupNote = 0, myUid)
        } else {
            viewModel.getSelfNoteFromDB()
        }
    }

    private fun setDataToNoteRecyclerView() {
        binding.rcvDocument.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun addNote() {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_addNoteFragment2)
        }
    }

    override fun onItemLongClickListener(note: Note) {
        var dialog = Constrain.createDialog(requireContext(), R.layout.dialog_confirm)
        var confirmTv = dialog.findViewById<TextView>(R.id.messagCfTv)
        var huyBtn = dialog.findViewById<LinearLayout>(R.id.cancelBtn)
        var dongYBtn = dialog.findViewById<LinearLayout>(R.id.dongyBtn)
        confirmTv.text = "Bạn có muốn xoá ghi chú?"
        dongYBtn.setOnClickListener {
            deleteNote(note.id)
            dialog.dismiss()
        }
        huyBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteNote(id: String) {
        viewModel.deleteNote(id)
        viewModel.deleteSelfNoteFromDB(id)
    }
}