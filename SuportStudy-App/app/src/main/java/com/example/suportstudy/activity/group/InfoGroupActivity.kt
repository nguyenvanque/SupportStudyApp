package com.example.suportstudy.activity.group

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.example.suportstudy.R
import com.example.suportstudy.activity.course.CourseTypeActivity
import com.example.suportstudy.extensions.gone
import com.example.suportstudy.extensions.visible
import com.example.suportstudy.model.GroupCourse
import com.example.suportstudy.service.GroupCourseAPI
import com.example.suportstudy.until.Constrain
import com.example.suportstudy.until.Persmission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_confirm.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class InfoGroupActivity : AppCompatActivity() {
    var context = this@InfoGroupActivity
    var groupCourseAPI: GroupCourseAPI? = null

    companion object {
        var groupCreateBy: String? = null
    }

    var groupId: String? = null
    var groupName: String? = null
    var groupDescription: String? = null
    var groupImage: String? = null

    var groupIv: ImageView? = null
    var groupNameTv: TextView? = null
    var groupDescriptionTv: TextView? = null
    var leaveGroupTv: TextView? = null
    var changeGroupNameLayout: RelativeLayout? = null
    var viewMemberLayout: RelativeLayout? = null
    var leaveGroupLayout: RelativeLayout? = null
    var finishLayout: RelativeLayout? = null
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var dataLayout: RelativeLayout
    lateinit var lazyLoader: LazyLoader
    val groupCoursedata = MutableLiveData<List<GroupCourse>>()

    var image_uri: Uri? = null
    var path_imageStorage: String? = null

    var sd:SweetAlertDialog?=null

    var isTurtor:Boolean = false
    var uid:String ?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)

        initViewData()
        viewMemberLayout!!.setOnClickListener {
            var intent = Intent(context, ListMemberGroupActivity::class.java)
            intent.putExtra("groupId", groupId)
            context.startActivity(intent)
        }
        groupIv!!.setOnClickListener {
            if (isTurtor==true){
                if (!Persmission.checkStoragePermission(context)) {
                    Persmission.requestStoragetPermission(context)
                } else {
                    Persmission.pickFromGallery(context)
                }
            }
        }
        leaveGroupLayout!!.setOnClickListener {
            val dialog = Constrain.createDialog(context, R.layout.dialog_confirm)
            var txtXacNhan = dialog.findViewById<TextView>(R.id.messagCfTv)
            var btnHuy = dialog.findViewById<LinearLayout>(R.id.cancelBtn)
            var btnXacNhan = dialog.findViewById<LinearLayout>(R.id.dongyBtn)
            txtXacNhan.setText("Bạn có muốn đăng xuất !")
            if (leaveGroupTv!!.text.equals("Rời nhóm")) {
                txtXacNhan.text = "Bạn muốn rời nhóm ?"
            } else if (leaveGroupTv!!.text.equals("Xóa nhóm")) {
                txtXacNhan.text = "Bạn muốn xóa nhóm ?"
            }
            btnHuy.setOnClickListener { dialog.dismiss() }
            btnXacNhan.setOnClickListener {
                if (leaveGroupTv!!.text.equals("Rời nhóm")) {
                    leaveGroup()
                    dialog.dismiss()
                } else if (leaveGroupTv!!.text.equals("Xóa nhóm")) {
                    deleteGroup()
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
        changeGroupNameLayout!!.setOnClickListener {
            if (isTurtor==true){
                updateGroupName()
            }else{
                Constrain.showToast("Bạn không thể đổi tên nhóm")
            }
        }
    }

    private fun deleteGroup() {
        groupCoursedata.observe(context,{
            var listjoin=it[0].participant
            for (i in listjoin!!.indices){
                groupCourseAPI!!.deleteUserGroup(listjoin[i]._id).enqueue(object :Callback<GroupCourse>{
                    override fun onResponse(
                        call: Call<GroupCourse>,
                        response: Response<GroupCourse>
                    ) {
                        if(response.isSuccessful){
                            groupCourseAPI!!.deleteGroup(groupId).enqueue(object :Callback<GroupCourse>{
                                override fun onResponse(
                                    call: Call<GroupCourse>,
                                    response: Response<GroupCourse>
                                ) {
                                    Constrain.showToast("Đã xóa nhóm")
                                    Constrain.nextActivity(context,CourseTypeActivity::class.java)
                                    finish()
                                }
                                override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                                    Log.e("err1", t.message.toString())
                                }
                            })
                        }
                    }

                    override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                        Log.e("err2", t.message.toString())

                    }

                })
            }
        })
    }

    private fun leaveGroup() {
        groupCoursedata.observe(context,{
            var listJoin= it!![0].participant
            for (i in listJoin!!.indices){
                if(listJoin[i].uid!!.equals(uid)){
                    var idjoin=listJoin[i]._id
                    groupCourseAPI!!.deleteUserGroup(idjoin).enqueue(object :Callback<GroupCourse>{
                        override fun onResponse(
                            call: Call<GroupCourse>,
                            response: Response<GroupCourse>
                        ) {
                            Constrain.showToast("Đã rời nhóm")
                            Constrain.nextActivity(context,CourseTypeActivity::class.java)
                            finish()
                        }

                        override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                            Log.e("Error",t.message.toString())
                        }

                    })
                }
            }
        })
    }

    private fun updateGroupName() {
        val dialog = Constrain.createDialog(context, R.layout.dialog_edit_name)
        val edtName = dialog.findViewById<EditText>(R.id.edtName)
        val btnHuy = dialog.findViewById<AppCompatButton>(R.id.btnHuy)
        val btnDoi = dialog.findViewById<AppCompatButton>(R.id.btnDoi)
        edtName.setText(groupNameTv!!.text.toString())
        btnDoi.setOnClickListener {
            var groupName = edtName.text.toString()
            if (groupName.equals("")) {
                Constrain.showToast("Vui lòng nhập tên nhóm")
            } else {
                groupCourseAPI!!.updateGroupName(groupId!!, groupName)
                    .enqueue(object : Callback<GroupCourse> {
                        override fun onResponse(
                            call: Call<GroupCourse>,
                            response: Response<GroupCourse>
                        ) {
                            if (response.isSuccessful) {
                                getGroupById()
                                showUiProfile()
                                dialog.dismiss()
                            }
                        }
                        override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                            Log.e("err", t.message.toString())
                        }

                    })

            }

        }
        btnHuy.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun initViewData() {
        var  userSharedPreferences = getSharedPreferences(Constrain.SHARED_REF_USER, MODE_PRIVATE)
        isTurtor = userSharedPreferences!!.getBoolean(Constrain.KEY_ISTUTOR, false)!!
        uid = userSharedPreferences!!.getString(Constrain.KEY_ID, "")!!

        Constrain.context = context
        var intentGroupChat = intent
        groupId = intentGroupChat.getStringExtra("groupId")
        groupCreateBy = intentGroupChat.getStringExtra("groupCreateBy")
        groupIv = findViewById(R.id.groupImage)
        groupNameTv = findViewById(R.id.groupNameTv)
        groupDescriptionTv = findViewById(R.id.groupDescriptionTv)
        leaveGroupTv = findViewById(R.id.leaveGroupTv)
        changeGroupNameLayout = findViewById(R.id.changeGroupNameLayout)
        viewMemberLayout = findViewById(R.id.viewMemberLayout)
        leaveGroupLayout = findViewById(R.id.leaveGroupLayout)
        finishLayout = findViewById(R.id.finishLayout)
        refreshLayout = findViewById(R.id.refreshLayout)
        dataLayout = findViewById(R.id.dataLayout)
        lazyLoader = findViewById(R.id.myLoader)
        sd=Constrain.sweetdialog(context,"Đang xử lí...")

        groupCourseAPI = Constrain.createRetrofit(GroupCourseAPI::class.java)

        dataLayout.gone()
        refreshData()
        getGroupById()
        showUiProfile()
        if (!groupCreateBy.equals(uid)) {
            leaveGroupTv!!.text = "Rời nhóm"
        } else {
            leaveGroupTv!!.text = "Xóa nhóm"
        }
        finishLayout!!.setOnClickListener {
            finish()
        }
    }
    fun getGroupById() {
        val chatFetchJob = Job()
        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            Constrain.showToast("Data error")
        }
        val scope = CoroutineScope(chatFetchJob + Dispatchers.Main)
        scope.launch(errorHandler) {
            groupCourseAPI!!.getAllGroupByID(groupId!!)
                .enqueue(object : Callback<List<GroupCourse>> {
                    override fun onResponse(
                        call: Call<List<GroupCourse>>,
                        response: Response<List<GroupCourse>>
                    ) {
                        if (response.isSuccessful) {
                            groupCoursedata.postValue(response.body())
                        }
                    }
                    override fun onFailure(call: Call<List<GroupCourse>>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }
                })
        }
    }

    fun showUiProfile() {
        groupCoursedata.observe(context, { listGroupId ->
            groupName = listGroupId[0].groupName
            groupDescription = listGroupId[0].groupDescription
            groupImage = listGroupId[0].groupImage
            groupNameTv!!.text = groupName
            groupDescriptionTv!!.text = groupDescription
            var path = Constrain.subPathImage("group",groupImage!!)
            Constrain.checkShowImage(context, R.drawable.avatar_default, path!!, groupIv!!)
            lazyLoader.gone()
            dataLayout.visible()
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
                        Constrain.showToast( "Bật quyen thư viện")
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
                editGroupImage()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun editGroupImage() {
        sd!!.show()
        groupCourseAPI!!.getAllGroupByID(groupId!!)
            .enqueue(object : Callback<List<GroupCourse>> {
                override fun onResponse(
                    call: Call<List<GroupCourse>>,
                    response: Response<List<GroupCourse>>
                ) {
                    if (response.isSuccessful) {
                        var imageGroup=response.body()!![0].groupImage
                        var file = File(path_imageStorage)
                        var requestId =
                            RequestBody.create(MediaType.parse("multipart/form_data"), groupId)
                        var requestOldImage = RequestBody.create(MediaType.parse("multipart/form_data"), imageGroup)

                        val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                        val body = MultipartBody.Part.createFormData("group", file.getName(), reqFile)

                        groupCourseAPI!!.updateGroupImage(requestId,body,requestOldImage).enqueue(object :Callback<GroupCourse>{
                            override fun onResponse(call: Call<GroupCourse>, response: Response<GroupCourse>) {
                                if (response.isSuccessful) {
                                    Constrain.showToast("Đổi thành công")
                                    getGroupById()
                                    showUiProfile()
                                }
                                sd!!.dismiss()

                            }

                            override fun onFailure(call: Call<GroupCourse>, t: Throwable) {
                                Constrain.showToast( "Thất bại")
                                Log.e("ERROR", t.message.toString())
                                sd!!.dismiss()}

                        })                    }
                }

                override fun onFailure(call: Call<List<GroupCourse>>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }

            })

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