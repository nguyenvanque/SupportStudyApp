package com.example.suportstudy.activity.group

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.example.suportstudy.R
import com.example.suportstudy.adapter.ListMemberGroupAdapter
import com.example.suportstudy.extensions.gone
import com.example.suportstudy.extensions.visible
import com.example.suportstudy.model.GroupCourse
import com.example.suportstudy.model.Users
import com.example.suportstudy.service.GroupCourseAPI
import com.example.suportstudy.service.UserAPI
import com.example.suportstudy.until.Constrain
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListMemberGroupActivity : AppCompatActivity() {
    var context=this@ListMemberGroupActivity
    lateinit var recyclerViewUser:RecyclerView
    var listUsers:ArrayList<Users> = ArrayList<Users>()

    lateinit var groupCourseAPI:GroupCourseAPI
    lateinit var userAPI:UserAPI

     var groupId:String?=null
    lateinit var listMemberGroupAdapter:ListMemberGroupAdapter
    lateinit  var lazyLoader:LazyLoader
    lateinit var refreshLayout:SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_member_group)
        initDataView()
        getAllParticipantByGroupId()
    }

    fun initDataView(){
        lazyLoader=findViewById(R.id.myLoader)
        groupCourseAPI=Constrain.createRetrofit(GroupCourseAPI::class.java)
        userAPI=Constrain.createRetrofit(UserAPI::class.java)
        recyclerViewUser=findViewById(R.id.recyclerViewUser);
        refreshLayout=findViewById(R.id.refreshLayout);
        var intentGroupChat=intent
        groupId=intentGroupChat.getStringExtra("groupId")
        refreshData()
    }
    fun getAllParticipantByGroupId(){
        var count=0
        lazyLoader!!.visible()
        val chatFetchJob = Job()
        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            Constrain.showToast("Data error")
        }
        val scope = CoroutineScope(chatFetchJob + Dispatchers.Main)
        scope.launch(errorHandler) {
            groupCourseAPI!!.getAllGroupByID(groupId!!).enqueue(object :Callback<List<GroupCourse>>{
                override fun onResponse(
                    call: Call<List<GroupCourse>>,
                    response: Response<List<GroupCourse>>
                ) {
                    var listJoin=response.body()!![0].participant
                    for (i in listJoin!!.indices){
                        var uid=listJoin[i].uid
                        count++
                        getUserById(uid!!)

                    }
                }
                override fun onFailure(call: Call<List<GroupCourse>>, t: Throwable) {
                    Log.e("error",t.message.toString())
                }

            })

        }
    }
    fun getUserById(uid:String){
        userAPI!!.getAllUsersByID(uid)
            .enqueue(object :Callback<List<Users>>{
                override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                   if (response.isSuccessful){
                       listUsers.addAll(response.body()!!)
                   }
                    listMemberGroupAdapter= ListMemberGroupAdapter(context,listUsers)
                    recyclerViewUser!!.adapter=listMemberGroupAdapter
                    lazyLoader!!.gone()

                }
                override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                    Log.e("error",t.message.toString())
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