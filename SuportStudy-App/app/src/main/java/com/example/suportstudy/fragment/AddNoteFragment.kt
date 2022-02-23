package com.example.suportstudy.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.suportstudy.R
import com.example.suportstudy.apibodymodel.AddNoteBody
import com.example.suportstudy.databinding.FragmentAddNoteBinding
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.viewmodel.NoteViewModel

class AddNoteFragment : Fragment() {

    companion object {

    }

    private lateinit var viewModel: NoteViewModel
    private lateinit var binding: FragmentAddNoteBinding
    private var addNoteBody = AddNoteBody()
    private var userSharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(this).get(NoteViewModel::class.java)

        initView()
        setListener()
        subscribeUI()
        return binding.root

    }

    private fun initView() {
        userSharedPreferences = activity?.getSharedPreferences(
            Constrain.SHARED_REF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setListener() {
        /** Back to previous screen **/
        binding.menu.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvAdd.setOnClickListener {
            saveNote()
        }
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

            /** Add note response **/
            liveDataAddNoteResponse.observe(viewLifecycleOwner, Observer {
                it?.let {
                    liveDataNoteResponse.value = null
                    if (it != null) {
                        Constrain.showToast(requireContext(), "Thêm ghi chú thành công")
                        findNavController().navigate(R.id.action_addNoteFragment_to_noteFragment)
                    } else {
                        Constrain.showErrorMessage("Có lỗi xảy ra", requireContext())
                    }
                }
            })
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            return !(edtContent.text.isEmpty() || edtMessage.text.isEmpty())
        }
    }

    private fun saveNote() {
        if (validate()) {
            collectData()
            viewModel.addNote(addNoteBody)
        } else {
            Constrain.showErrorMessage("Nội dung không được để trống", requireContext())
        }
    }

    private fun collectData() {
        val currentUser = userSharedPreferences!!.getString(Constrain.KEY_ID, "")
        addNoteBody.apply {
            title =
                if (binding.edtMessage.text.isEmpty()) "" else binding.edtMessage.text.toString()
            content =
                if (binding.edtContent.text.isEmpty()) "" else binding.edtContent.text.toString()
            userId = currentUser
            isGroupNote = 0
        }
    }
}