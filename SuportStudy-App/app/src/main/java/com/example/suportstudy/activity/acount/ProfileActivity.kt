package com.example.suportstudy.activity.acount

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.example.suportstudy.R
import com.example.suportstudy.activity.ActionActivity
import com.example.suportstudy.activity.MainActivity
import com.example.suportstudy.activity.group.ListGroupActivity
import com.example.suportstudy.controller.UserController
import com.example.suportstudy.extensions.gone
import com.example.suportstudy.extensions.visible
import com.example.suportstudy.model.Users
import com.example.suportstudy.service.UserAPI
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.until.Persmission
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {
    val context = this@ProfileActivity
    var listGroupLayout: RelativeLayout? = null
    var changeNameLayout: RelativeLayout? = null
    var changepasswordLayout: RelativeLayout? = null
    var deletedUserLayout: RelativeLayout? = null
    var logoutLayout: RelativeLayout? = null
    var avatarIv: CircleImageView? = null
    var nameTv: TextView? = null
    var finishTv: TextView? = null
    lateinit var myLoader: LazyLoader
    lateinit var dataLayout: RelativeLayout
    lateinit var refreshLayout: SwipeRefreshLayout

    var sharedPreferences: SharedPreferences? = null
    var userAPI: UserAPI? = null

    var image_uri: Uri? = null
    var path_imageStorage: String? = null
    lateinit var uid:String
    var image = ""
    var password = ""
    var name = ""


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViewData()

        finishTv!!.setOnClickListener {
            Constrain.nextActivity(context,ActionActivity::class.java)
            finish()
        }
        avatarIv!!.setOnClickListener {
            if (!Persmission.checkStoragePermission(context)) {
                Persmission.requestStoragetPermission(context)
            } else {
                Persmission.pickFromGallery(context)
            }
        }
        listGroupLayout!!.setOnClickListener {
            var intent = Intent(context, ListGroupActivity::class.java)
            intent.putExtra("group", "groupMyJoin")
            startActivity(intent)
        }
        changeNameLayout!!.setOnClickListener {
            editName()
        }
        changepasswordLayout!!.setOnClickListener {
            editPassword()
        }
        deletedUserLayout!!.setOnClickListener {
            deleteUser()
        }
        logoutLayout!!.setOnClickListener {
            val dialog = Constrain.createDialog(context,R.layout.dialog_confirm)
            var txtXacNhan = dialog.findViewById<TextView>(R.id.messagCfTv)
            var btnHuy = dialog.findViewById<LinearLayout>(R.id.cancelBtn)
            var btnXacNhan = dialog.findViewById<LinearLayout>(R.id.dongyBtn)
            txtXacNhan.setText("Bạn có muốn đăng xuất !")
            btnXacNhan.setOnClickListener {
                dialog.dismiss()
                val editor = sharedPreferences!!.edit()
                editor.clear()
                editor.commit()
                startActivity(Intent(context, MainActivity::class.java))
                finish()
            }
            btnHuy.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }



    fun initViewData() {
        Constrain.context=context
        sharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        uid= sharedPreferences!!.getString(Constrain.KEY_ID,"")!!
        listGroupLayout = findViewById(R.id.listGroupLayout)
        changeNameLayout = findViewById(R.id.changeNameLayout)
        changepasswordLayout = findViewById(R.id.changepasswordLayout)
        deletedUserLayout = findViewById(R.id.deletedUserLayout)
        logoutLayout = findViewById(R.id.logoutLayout)
        avatarIv = findViewById(R.id.avatarIv)
        nameTv = findViewById(R.id.nameTv)
        finishTv = findViewById(R.id.finishTv)
        refreshLayout = findViewById(R.id.refreshLayout)
        myLoader=findViewById(R.id.myLoader)
        dataLayout=findViewById(R.id.dataLayout)
        userAPI = Constrain.createRetrofit(UserAPI::class.java)

        refreshData()
        dataLayout.gone()
        getDataProfile(uid)
    }

    fun editImage() {
        var file = File(path_imageStorage)
        var requestId =
            RequestBody.create(MediaType.parse("multipart/form_data"), uid)
        var requestOldImage = RequestBody.create(MediaType.parse("multipart/form_data"), image)
        val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("user", file.getName(), reqFile)
        userAPI!!.editImage(requestId, body, requestOldImage)
            .enqueue(object : Callback<Users> {
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    if (response.isSuccessful) {
                        Constrain.showToast("Đổi thành công")
                        getDataProfile(uid)
                    }
                }
                override fun onFailure(call: Call<Users>, t: Throwable) {
                    Constrain.showToast( "Thất bại")
                    t.printStackTrace()
                    Log.e("ERROR", t.toString())
                }
            })
    }

    private fun getDataProfile(uid: String?) {
        var data=UserController.getUserProfile(context,uid!!)
        data.observe(context,{listUser->
            var imgUrl = ""
            for (i in listUser!!.indices) {
                imgUrl = listUser!![i].image
                name = listUser!![i].name
                password = Constrain.decryption(listUser!![i].password)!!
            }
            sharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
            val editor = sharedPreferences!!.edit()
            editor.putString(Constrain.KEY_IMAGE, imgUrl)
            editor.putString(Constrain.KEY_NAME, name)
            editor.apply()
            image = sharedPreferences!!.getString(Constrain.KEY_IMAGE, "")!!
            nameTv!!.text = name
            if (imgUrl != "") {
                var path = Constrain.baseUrl + "/post/" + imgUrl.substring(27)
                Constrain.checkShowImage(context, R.drawable.avatar_default, path, avatarIv!!)
            }
            myLoader.gone()
            dataLayout.visible()
        })

    }

    fun editName() {
        val dialog = Constrain.createDialog(context,R.layout.dialog_edit_name)
        val edtName = dialog!!.findViewById<EditText>(R.id.edtName)
        val btnDoi = dialog!!.findViewById<Button>(R.id.btnDoi)
        val btnHuy = dialog!!.findViewById<Button>(R.id.btnHuy)
        edtName.setText(name)
        btnDoi.setOnClickListener {
            var name = edtName.text.toString()
            if (name.equals("")) {
                Constrain.showToast("Vui lòng nhập tên")
            } else {
                userAPI!!.editName(uid, name)
                    .enqueue(object : Callback<Users> {
                        override fun onResponse(call: Call<Users>, response: Response<Users>) {
                            if (response.isSuccessful) {
                                Constrain.showToast( "Đổi thành công")
                                sharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
                                val editor = sharedPreferences!!.edit()
                                editor.putString(Constrain.KEY_NAME, name)
                                editor.commit()
                                getDataProfile(uid)
                                dialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<Users>, t: Throwable) {
                            Constrain.showToast( "Đổi thất bại")
                            dialog.dismiss()
                            Log.e("err", t.message.toString())
                        }

                    })
            }


        }
        btnHuy.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun editPassword() {
        val dialog = Constrain.createDialog(context,R.layout.dialog_edit_password)

        var edtNowPassword = dialog.findViewById<EditText>(R.id.edtNowPassword)
        var edtNewPassword = dialog.findViewById<EditText>(R.id.edtNewPassword)
        var edtCfNewPassword = dialog.findViewById<EditText>(R.id.edtCfNewPassword)
        var btnHuy = dialog.findViewById<AppCompatButton>(R.id.btnHuy)
        var btnXacNhan = dialog.findViewById<AppCompatButton>(R.id.btnXacNhan)
        btnXacNhan.setOnClickListener {
            var oldpassword = edtNowPassword.text.toString()
            var newpassword = edtNewPassword.text.toString()
            var newcfpassword = edtCfNewPassword.text.toString()
            if (oldpassword.equals("")) {
                Constrain.showToast( "Nhập mật khẩu cũ")
            } else if (newpassword.equals("")) {
                Constrain.showToast( "Nhập mật khẩu mới")
            } else if (newcfpassword.equals("")) {
                Constrain.showToast( "Nhập mật khẩu xác nhận")
            } else if (!oldpassword.equals(password)) {
                Constrain.showToast( "Mật khẩu cũ không đúng")
            } else if (!newpassword.equals(newcfpassword)) {
                Constrain.showToast( "Mật khẩu xác nhận không đúng")
            } else {
                userAPI!!.editPassword(uid, Constrain.encryption(newpassword))
                    .enqueue(object : Callback<Users> {
                        override fun onResponse(call: Call<Users>, response: Response<Users>) {
                            if (response.isSuccessful) {
                                Constrain.showToast( "Đổi thành công")
                                getDataProfile(uid)
                                dialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<Users>, t: Throwable) {
                            Constrain.showToast("Đổi thất bại")
                            dialog.dismiss()
                            Log.e("err", t.message.toString())
                        }

                    })
            }
        }
        btnHuy.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
    private fun deleteUser() {
        val dialog = Constrain.createDialog(context,R.layout.dialog_confirm)
        var txtXacNhan = dialog.findViewById<TextView>(R.id.confirmTv)
        var btnHuy = dialog.findViewById<LinearLayout>(R.id.cancelBtn)
        var btnXacNhan = dialog.findViewById<LinearLayout>(R.id.dongyBtn)
        txtXacNhan.setText("Bạn có muốn đăng xuất !")
        txtXacNhan.setText("Bạn có muốn xóa tài khoản của bạn !")
        btnXacNhan.setOnClickListener {
        }
        btnHuy.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Persmission.STORAGE_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    val writeStorageAccpted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (writeStorageAccpted) {
                        Persmission.pickFromGallery(context)
                    } else {
                        Constrain.showToast( "Bật quyền thư viện")
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Persmission.IMAGE_PICK_GALLERY_CODE) {
                image_uri = data!!.data!!
                path_imageStorage = Constrain.getRealPathFromURI(context,image_uri)
                editImage()

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun refreshData(){
        refreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                refreshData() // your code
                refreshLayout.setRefreshing(false)
            }

            private fun refreshData() {
                finish()
                overridePendingTransition(0, 0)
                startActivity(getIntent())
                overridePendingTransition(0, 0)
            }
        })
    }
}