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
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.model.Users
import com.example.suportstudy.service.UserAPI
import com.example.suportstudy.until.Constrain
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Response
import java.util.regex.Matcher

class LoginFragment : Fragment() {
    @SuppressLint("UseRequireInsteadOfGet")
    var sd:SweetAlertDialog?=null
    var listUser: List<Users>?=null
    var  checkLogin=false

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
        Constrain.context=activity!!
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)
        sharedPreferences = context!!.getSharedPreferences(
            Constrain.SHARED_REF_USER,
            Context.MODE_PRIVATE
        )

        sd=Constrain.sweetdialog(activity!!,"Đang đăng nhập")
        btnLogin.setOnClickListener {
            var email = edtEmail.text.toString()
            var password = edtPassword.text.toString()
            val matcher: Matcher = Constrain.VALID_EMAIL_ADDRESS_REGEX.matcher(email)

            if(email.equals("")){
                Constrain.showToast("Vui lòng nhập email")
                edtEmail.setFocusable(true)
            }else if (!matcher.matches()) {
                edtEmail.error = "Nhập đúng định dạng email !"
                edtEmail.setFocusable(true)
            }else if(password.equals("")){
                Constrain.showToast("Vui lòng nhập mật khẩu")
                edtPassword.setFocusable(true)
            }else{
                sd!!.show()
               loginFuntion(email,password)
            }
        }

        return view
    }

    fun loginFuntion(email:String,password:String){

        val userAPI = Constrain.createRetrofit(UserAPI::class.java)
        var call = userAPI.getAllUsers()
        call.enqueue(object : retrofit2.Callback<List<Users>> {
            override fun onResponse(
                call: retrofit2.Call<List<Users>>,
                response: Response<List<Users>>
            ) {
                if (response.isSuccessful) {
                    listUser = response.body()
                    for (i in listUser!!.indices) {
                        var   _id=listUser!![i]._id
                        var   name=listUser!![i].name
                        var   image=listUser!![i].name
                        var   userEmail=listUser!![i].email
                        var   userPassword=Constrain.decryption(listUser!![i].password)
                        var   istutor=listUser!![i].isTurtor
                        if (userEmail.equals(email) && userPassword.equals(password)
                        ) {
                            checkLogin=true
                            isLogin=true
                            val editor = sharedPreferences!!.edit()
                            editor.putString(Constrain.KEY_ID, _id)
                            editor.putString(Constrain.KEY_NAME, name)
                            editor.putString(Constrain.KEY_IMAGE, image)
                            editor.putString(Constrain.KEY_EMAIL, userEmail)
                            editor.putBoolean(Constrain.KEY_LOGIN, isLogin)
                            if (istutor != null) {
                                editor.putBoolean(Constrain.KEY_ISTUTOR, istutor)
                            }
                            editor.apply()
                            break
                        }
                    }
                    if(checkLogin==true){
                        sd!!.dismiss()
                        Constrain.nextActivity(activity!!, ActionActivity::class.java)
                        activity!!.finish()
                    }else{
                        Constrain.showToast("Email hoặc mật khẩu không đúng")
                        sd!!.dismiss()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Users>>, t: Throwable) {
                sd!!.dismiss()
                Log.v("Data", "Error:" + t.message.toString())
            }
        })
        sd!!.dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}



