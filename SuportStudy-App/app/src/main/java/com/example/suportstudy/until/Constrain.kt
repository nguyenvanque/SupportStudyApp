package com.example.suportstudy.until

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.suportstudy.R
import com.example.suportstudy.model.Question
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


@SuppressLint("StaticFieldLeak")
object Constrain {

    //    var baseUrl="http://192.168.3.107:10000"
    var baseUrl = "https://learning-support-application.herokuapp.com/"
    var firebaseUrl="https://suportstudy-72e5e-default-rtdb.firebaseio.com/"
//    var baseUrl="http://172.20.10.3:10000"

    var SHARED_REF_USER: String? = "savestatuslogin"
    var KEY_ID = "_id"
    var KEY_NAME = "name"
    var KEY_IMAGE = "image"
    var KEY_EMAIL = "email"
    var KEY_LOGIN = "islogin"
    var KEY_ISTUTOR = "isTutor"

    var context:Context?=null


    val VALID_EMAIL_ADDRESS_REGEX =Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    )

    var CHANNEL_ID = "1000"
    var NOTIFICATION_ID = 100

    var NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send"
    var SERVER_KEY ="AAAAT1UYtF0:APA91bELQ_x37OR3dXL_ZlUk3a3AE6qj6Xe7_JwaDfzpNP6S5TOk2CahSW_NPCRkZu2LC-TQReQl5gw0Ji_tlpB7-xmOKXQ8ZKmMhJTuToL3CQO13ihh-ilUypMVL4OwnaynaW6A9u6A"


    fun postDelay(runnable: Runnable?, delayMillis: Long) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable!!, delayMillis)
    }

    fun <T> nextActivity(context: Context, clazz: Class<T>) {
        var intent = Intent(context, clazz);
        context.startActivity(intent)
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    fun hideKeyBoard(context: Activity) {
        @SuppressLint("ServiceCast") val inputManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(context.currentFocus!!.windowToken, 0)
    }

    fun sweetdialog(context: Context, title: String):SweetAlertDialog{
        var sd = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        sd!!.titleText = title
        sd!!.setCancelable(false)
        return sd
    }

    fun formatDate(date: String): String {
        return date.substring(11, 19) + " " + date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4)
    }


    var retrofit: Retrofit? = null
    fun <T> createRetrofit(clazz: Class<T>): T {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(clazz);
    }

    fun showQuestion(
        op1: RadioButton,
        op2: RadioButton,
        op3: RadioButton,
        op4: RadioButton,
        list: List<Question>,
        index: Int
    ) {
        op1.text = list.get(index).option1
        op2.text = list.get(index).option2
        op3.text = list.get(index).option3
        op4.text = list.get(index).option4
    }

    fun checkShowImage(context: Context, defautImage: Int, imageUrl: String, imageView: ImageView){
        try {
            if(imageUrl.equals("noImage")){
                imageView!!.setImageResource(defautImage)
            }else if(imageUrl.equals("")){
                imageView!!.setImageResource(defautImage)
            }else{
                Picasso.with(context).load(imageUrl).placeholder(defautImage).into(
                    imageView
                )
            }
        }catch (e: Exception){
            imageView!!.setImageResource(R.drawable.ic_gallery_grey)
        }
    }
    fun createDialog(context: Context, layout: Int):Dialog{
        val dialog = Dialog(context)
        dialog.setContentView(layout)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogTheme
        val window = dialog!!.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (dialog != null && dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog!!.setCancelable(false)

        return dialog
    }
    fun getRealPathFromURI(context: Activity, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.managedQuery(
            contentUri,
            proj,  // Which columns to return
            null,  // WHERE clause; which rows to return (all rows)
            null,  // WHERE clause selection arguments (none)
            null
        ) // Order-by clause (ascending by name)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun initFirebase(path: String):DatabaseReference{
        var ref=FirebaseDatabase.getInstance(firebaseUrl).getReference(path)
        return ref
    }
    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun subPathImage(typePath: String, imageUrl: String):String{
        var path = baseUrl + "/${typePath}/" + imageUrl!!.substring(imageUrl.lastIndexOf("/") + 1)
        return path
    }
    val HASHKEY="com.example.suportstudy"

    fun encryption(strNormalText: String?): String? {
        var normalTextEnc = ""
        try {
            normalTextEnc = AESHelper.encrypt(HASHKEY, strNormalText!!)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return normalTextEnc
    }

    fun decryption(strEncryptedText: String?): String? {
        var strDecryptedText = ""
        try {
            strDecryptedText = AESHelper.decrypt(HASHKEY, strEncryptedText!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strDecryptedText
    }

    fun showErrorMessage(error:String,context: Context){
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setMessage(error)
        alertDialog.setNegativeButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show().setCanceledOnTouchOutside(false)
    }

    fun isConnectedInternet(context: Context): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = cm.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}