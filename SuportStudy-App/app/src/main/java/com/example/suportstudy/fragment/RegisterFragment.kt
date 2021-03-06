package com.example.suportstudy.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.suportstudy.R
import com.example.suportstudy.activity.MainActivity
import com.example.suportstudy.activity.acount.LoginAndRegisterMainActivity
import com.example.suportstudy.activity.course.CourseTypeActivity
import com.example.suportstudy.model.Users
import com.example.suportstudy.service.UserAPI
import com.example.suportstudy.until.Constrain

import kotlinx.android.synthetic.main.fragment_register.*
import retrofit2.Response
import java.util.regex.Matcher

class RegisterFragment : Fragment() {
    var isTutor = false
    var sd: SweetAlertDialog? = null

    var isLogin = false
    var sharedPreferences: SharedPreferences? = null

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
        var view = inflater.inflate(R.layout.fragment_register, container, false)
        Constrain.context=activity!!
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val txtHome = view.findViewById<TextView>(R.id.txtHome)

        if (LoginAndRegisterMainActivity.isTutor == true) {
            isTutor = true
        } else {
            isTutor = false
        }
        val userAPI = Constrain.createRetrofit(UserAPI::class.java)
        sharedPreferences = context!!.getSharedPreferences(
            Constrain.SHARED_REF_USER,
            Context.MODE_PRIVATE
        )
        sd = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        sd!!.titleText = "??ang t???o t??i kho???n..."
        sd!!.setCancelable(false)


        txtHome.setOnClickListener {
            LoginAndRegisterMainActivity.isTutor = false
            isTutor = false
            Constrain.nextActivity(activity!!, MainActivity::class.java)
        }

        btnRegister.setOnClickListener {

            var email = edtEmail.text.toString()
            var name = edtName.text.toString()
            var password = edtPassword.text.toString()
            val matcher: Matcher = Constrain.VALID_EMAIL_ADDRESS_REGEX.matcher(email)
            if (name.equals("")) {
                edtName.error = "Vui l??ng nh???p t??n !"
                edtName.setFocusable(true)
            }
            else if (name.length < 6) {
                edtName.error = "T??n ph???i t??? 6 k?? t??? !"
                edtName.setFocusable(true)
            }else if (email.equals("")) {
                edtEmail.error = "Vui l??ng nh???p email !"
                edtEmail.setFocusable(true)
            } else if (!matcher.matches()) {
                edtEmail.error = "Nh???p ????ng ?????nh d???ng email !"
                edtEmail.setFocusable(true)
            }
            else if (password.equals("")) {
                edtPassword.error = "Vui l??ng nh???p m???t kh???u !"
                edtPassword.setFocusable(true)
            } else if (password.length < 6) {
                edtPassword.error = "M???t kh???u ph???i t??? 6 k?? t??? !"
                edtPassword.setFocusable(true)
            }  else {
                sd!!.show()

                var pass=Constrain.encryption(password)
                var call = userAPI.register(
                    name,
                    email,
                    pass!!,
                    "noImage",
                    isTutor
                )
                call.enqueue(object : retrofit2.Callback<Users> {
                    override fun onResponse(
                        call: retrofit2.Call<Users>,
                        response: Response<Users>
                    ) {
                        sd!!.titleText = "??ang ????ng nh???p v??o ???ng d???ng..."
                        if (response.isSuccessful) {
                            var   users = response.body()!!
                            var _id = users._id
                            var name = users.name
                            var email = users.name
                            var image = users.image

                            isLogin = true
                            val editor = sharedPreferences!!.edit()
                            editor.putString(Constrain.KEY_ID, _id)
                            editor.putString(Constrain.KEY_NAME, name)
                            editor.putString(Constrain.KEY_EMAIL, email)
                            editor.putString(Constrain.KEY_IMAGE, image)
                            editor.putBoolean(Constrain.KEY_LOGIN, isLogin)
                            editor.putBoolean(Constrain.KEY_ISTUTOR, isTutor)
                            editor.apply()
                            sd!!.dismiss()
                            Constrain.nextActivity(
                                activity!!,
                                CourseTypeActivity::class.java
                            )
                            activity!!.finish()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Users>, t: Throwable) {
                        sd!!.dismiss()
                        Log.v("Data", "Error: " + t.message.toString())
                    }
                })
            }


        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}