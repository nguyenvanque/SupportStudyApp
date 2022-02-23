package com.example.suportstudy.activity.course

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.suportstudy.R
import com.example.suportstudy.activity.document.ListDocumentActivity
import com.example.suportstudy.activity.group.ListGroupActivity
import com.example.suportstudy.model.GroupCourse
import com.example.suportstudy.model.Participant
import com.example.suportstudy.service.GroupCourseAPI
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.until.Persmission
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CourseDetailActivity : AppCompatActivity() {

    var txtCourseName: TextView? = null
    var txtDescription: TextView? = null
    var courseIv: ImageView? = null
    var backIv: ImageView? = null
    var btnDocument: Button? = null
    var btnCreateGroup: Button? = null
    var ivGroup: CircleImageView? = null
    var context = this@CourseDetailActivity
    var groupCourseAPI: GroupCourseAPI? = null
    lateinit var myUid: String
    var sd: SweetAlertDialog? = null
    var image_uri: Uri? = null
    var part_image: String? = null
    var isTurtor: Boolean = false

    companion object {
        var imageUrl = ""
        var courseId: String? = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)
        initViewData()
        loadDetail()

        btnCreateGroup!!.setOnClickListener {
            if (btnCreateGroup!!.text.equals("Tạo Nhóm")) {
                createGroup()
            } else if (btnCreateGroup!!.text.equals("Nhóm thảo luận")) {
                var intent = Intent(context, ListGroupActivity::class.java)
                intent.putExtra("group", "allgroup")
                intent.putExtra("courseId", courseId)
                startActivity(intent)
            }
        }

        btnDocument!!.setOnClickListener {
            var intent = Intent(context, ListDocumentActivity::class.java)
            startActivity(intent)
        }
    }


    fun initViewData() {
        Constrain.context = context
        sd = Constrain.sweetdialog(context, "Đang tạo nhóm...")
        var userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        myUid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")!!
        isTurtor = userSharedPreferences!!.getBoolean(Constrain.KEY_ISTUTOR, false)!!

        groupCourseAPI = Constrain.createRetrofit(GroupCourseAPI::class.java)

        txtCourseName = findViewById(R.id.txtCourseName)
        txtDescription = findViewById(R.id.txtDescription)
        courseIv = findViewById(R.id.courseIv)
        backIv = findViewById(R.id.backIv)
        btnDocument = findViewById(R.id.btnJoin)
        btnCreateGroup = findViewById(R.id.btnCrearteClass)
        if (isTurtor == true) {
            btnCreateGroup!!.text = "Tạo Nhóm"
        } else {
            btnCreateGroup!!.text = "Nhóm thảo luận"
        }

        backIv!!.setOnClickListener {
            finish()
        }
    }

    fun loadDetail() {
        var intent: Intent = getIntent()
        courseId = intent.getStringExtra("courseId")
        var name = intent.getStringExtra("name")
        var desciption = intent.getStringExtra("desciption")
        imageUrl = intent.getStringExtra("image").toString()

        txtCourseName!!.text = name
        txtDescription!!.text = desciption

        Constrain.checkShowImage(context, R.drawable.ic_gallery_grey, imageUrl, courseIv!!)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun createGroup() {
        val dialog = Constrain.createDialog(context, R.layout.dialog_create_group)
        val btnHuy = dialog!!.findViewById<Button>(R.id.btnHuy)
        val btnTao = dialog!!.findViewById<Button>(R.id.btnTao)
        val edtName = dialog!!.findViewById<EditText>(R.id.edtName)
        val edtDecription = dialog!!.findViewById<EditText>(R.id.edtDecription)
        ivGroup = dialog!!.findViewById<CircleImageView>(R.id.IVGroup)

        ivGroup!!.setOnClickListener {
            if (!Persmission.checkStoragePermission(context)) {
                Persmission.requestStoragetPermission(context)
            } else {
                Persmission.pickFromGallery(context)
            }
        }

        btnTao.setOnClickListener {
            sd!!.show()
            var groupName = edtName.text.toString()
            var groupDescription = edtDecription.text.toString()
            var time = System.currentTimeMillis().toString()
            if (groupName.isEmpty() && groupDescription.isEmpty()) {
                sd!!.hide()
                Constrain.showErrorMessage("Vui lòng nhập thông tin", context)
            } else if (groupName == "") {
                sd!!.hide()
                Constrain.showErrorMessage("Tên group không được để trống", context)
            } else if (groupDescription == "") {
                sd!!.hide()
                Constrain.showErrorMessage("Mô tả không được để trống", context)

            } else {
                if (image_uri == null) {
                    createGroupNoImage(
                        myUid,
                        groupName,
                        groupDescription,
                        "noImage",
                        courseId,
                        time
                    )
                    dialog.dismiss()
                } else {
                    createGroupWithImage(myUid!!, groupName, groupDescription, time)
                    dialog.dismiss()
                }
            }

        }
        btnHuy.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun createGroupWithImage(
        myUid: String,
        groupName: String,
        groupDescription: String,
        time: String
    ) {
        sd!!.show()
        var file = File(part_image)
        var createBy = RequestBody.create(MediaType.parse("multipart/form_data"), myUid)
        var groupName = RequestBody.create(MediaType.parse("multipart/form_data"), groupName)
        var groupDescription =
            RequestBody.create(MediaType.parse("multipart/form_data"), groupDescription)
        var courseID = RequestBody.create(MediaType.parse("multipart/form_data"), courseId)
        val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("group", file.getName(), reqFile)

        groupCourseAPI!!.createGroupWithImage(
            createBy,
            groupName,
            groupDescription,
            image,
            courseID
        )
            .enqueue(object : Callback<GroupCourse> {
                override fun onResponse(call: Call<GroupCourse>, response: Response<GroupCourse>) {
                    if (response.isSuccessful) {
                        var groupId = response.body()!!._id
                        joinGroup(groupId, time)
                    }
                }

                override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })
    }

    private fun createGroupNoImage(
        myUid: String?,
        groupName: String,
        groupDescription: String,
        s: String,
        courseId: String?,
        time: String?
    ) {
        sd!!.show()
        groupCourseAPI!!.createGroupNoImage(myUid, groupName, groupDescription, s, courseId!!)
            .enqueue(object : Callback<GroupCourse> {
                override fun onResponse(
                    call: Call<GroupCourse>,
                    response: Response<GroupCourse>
                ) {
                    if (response.isSuccessful) {
                        var groupId = response.body()!!._id
                        joinGroup(groupId, time)
                    }
                }

                override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                    Constrain.showToast("Thất bại")
                }

            })
    }

    private fun joinGroup(groupId: String?, timeJoin: String?) {
        groupCourseAPI!!.joinGroup(groupId, myUid!!, timeJoin!!)
            .enqueue(object : Callback<Participant> {
                override fun onResponse(call: Call<Participant>, response: Response<Participant>) {
                    if (response.isSuccessful) {
                        Constrain.showToast("Thành công")
                        sd!!.dismiss()

                    }
                }

                override fun onFailure(call: Call<Participant>, t: Throwable) {
                    Log.e("Err", t.message.toString())

                }

            })
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
                        Constrain.showToast("Bật quyền thư viện")
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
                ivGroup!!.setImageURI(image_uri)
                part_image = Constrain.getRealPathFromURI(context, image_uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        if (isTurtor == true) {
            btnCreateGroup!!.text = "Tạo Nhóm"
        } else {
            btnCreateGroup!!.text = "Nhóm thảo luận"

        }
    }
}