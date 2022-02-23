package com.example.suportstudy.notification

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.suportstudy.activity.ActionActivity.Companion.uid
import com.example.suportstudy.activity.chat.ChatGroupActivity
import com.example.suportstudy.activity.chat.ChatOneActivity
import com.example.suportstudy.until.Constrain
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class FirebaseNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage!!.data.size > 0) {
            val notificationType = remoteMessage!!.data["notificationType"]

            if (notificationType.equals("chatOne")) {
                val map: Map<String, String> = remoteMessage.getData()
                val title = map["title"]
                val message = map["message"]
                val hisID = map["hisUid"]
                val hisName = map["hisName"]
                val hisImage = map["hisImage"]
                //            String chatID = map.get("chatID");
                Log.d(
                    "TAG",
                    "DataMap: \n title: $title" + "\nmessage: $message" + "\n notificationType: $notificationType" + "\nhisUid: $hisID" + "\nhisName: $hisName" + "\nhisImage: $hisImage"
                )
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    createOreoNotification(
                        title,
                        message,
                        hisID,
                        hisName,
                        hisImage
                    )
                } else {
                    createNormalNotification(title, message, hisID, hisName, hisImage)
                }
            } else if (notificationType.equals("chatGroup")) {
                val map: Map<String, String> = remoteMessage.getData()
                val title = map["title"]
                val message = map["message"]
                val groupId = map["groupId"]
                val groupCreateBy = map["groupCreateBy"]
                val groupName = map["groupName"]
                val groupDescription = map["groupDescription"]
                val groupImage = map["groupImage"]
                val hisID = map["hisUid"]
                val hisName = map["hisName"]
                val hisImage = map["hisImage"]
                //            String chatID = map.get("chatID");
                Log.d(
                    "TAG",
                    "DataMap: \n title: $title" + "\nmessage: $message" + "\n notificationType: $notificationType" + "\nhisUid: $hisID" + "\nhisName: $hisName" +
                            "hisImage: $hisImage" + "\ngroupImage: $groupImage"
                )
                if (!hisID.equals(uid)) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                        createGroupOreoNotification(
                            title,
                            message,
                            groupId,
                            groupCreateBy,
                            groupName,
                            groupDescription,
                            groupImage,
                            hisImage,

                        )
                    } else {
                        createGroupNormalNotification(
                            title,
                            message,
                            groupId,
                            groupCreateBy,
                            groupName,
                            groupDescription,
                            groupImage,
                            hisImage,

                        )
                    }
                }

            }


        } else Log.d("TAG", "onMessageReceived: no data ")
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String?,
        message: String?,
        hisID: String?,
        hisName: String?,
        hisImage: String?
    ) {
        val channel = NotificationChannel(
            Constrain.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)

        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, ChatOneActivity::class.java)
        intent.putExtra("hisUid", hisID)
        intent.putExtra("hisImage", hisImage)
        intent.putExtra("hisName", hisName)
        intent.putExtra("hisImage", hisImage)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var pathImage =Constrain.baseUrl + "/profile/" + hisImage!!.substring(hisImage.lastIndexOf("/") + 1)

        var bitmap = Constrain.getBitmapFromURL(pathImage)



        val notification: Notification = Notification.Builder(this, Constrain.CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ResourcesCompat.getColor(resources, R.color.holo_green_dark, null))
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setLargeIcon(bitmap)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

            .setSound(uri, attributes)
            .build()
        manager.notify(Constrain.NOTIFICATION_ID, notification)
    }

    private fun createNormalNotification(
        title: String?,
        message: String?,
        hisID: String?,
        hisName: String?,
        hisImage: String?
    ) {
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            Constrain.CHANNEL_ID
        )


        var path =
            Constrain.baseUrl + "/profile/" + hisImage!!.substring(hisImage.lastIndexOf("/") + 1)

        var bitmap = Constrain.getBitmapFromURL(path)



        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.sym_def_app_icon)

            .setColor(ResourcesCompat.getColor(resources, R.color.holo_green_dark, null))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(bitmap)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setSound(uri)

        val intent = Intent(this, ChatOneActivity::class.java)
        intent.putExtra("hisUid", hisID)
        intent.putExtra("hisImage", hisImage)
        intent.putExtra("hisName", hisName)
        intent.putExtra("hisImage", hisImage)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(85 - 65), builder.build())
    }


    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGroupOreoNotification(
        title: String?,
        message: String?,
        groupId: String?,
        groupCreateBy: String?,
        groupName: String?,
        groupDescription: String?,
        groupImage: String?,

        hisImage: String?
    ) {
        val channel = NotificationChannel(
            Constrain.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)

        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, ChatGroupActivity::class.java)
        intent.putExtra("groupId",groupId)
        intent.putExtra("groupCreateBy",groupCreateBy)
        intent.putExtra("groupImage",groupImage)
        intent.putExtra("groupName",groupName)
        intent.putExtra("groupDescription",groupDescription)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_btn_speak_now)
        var remoteViews =
            RemoteViews(packageName, com.example.suportstudy.R.layout.notification_layout)

        remoteViews.setTextViewText(com.example.suportstudy.R.id.txtName, title)
        remoteViews.setTextViewText(com.example.suportstudy.R.id.txtMessage, message)
        var path =
            Constrain.baseUrl + "/profile/" + hisImage!!.substring(hisImage.lastIndexOf("/") + 1)

        var bitmap2 = Constrain.getBitmapFromURL(path)

        remoteViews.setImageViewBitmap(
            com.example.suportstudy.R.id.avatarIv,
            bitmap2
        )

        val notification: Notification = Notification.Builder(this, Constrain.CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(ResourcesCompat.getColor(resources, R.color.holo_green_dark, null))
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(bitmap2)
//            .setCustomBigContentView(remoteViews)
            .setDefaults(Notification.DEFAULT_ALL)

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

            .setSound(uri, attributes)
            .build()
        manager.notify(Constrain.NOTIFICATION_ID, notification)
    }

    private fun createGroupNormalNotification(
        title: String?,
        message: String?,
        groupId: String?,
        groupCreateBy: String?,
        groupName: String?,
        groupDescription: String?,
        groupImage: String?,
        hisImage: String?
    )

    {
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            Constrain.CHANNEL_ID
        )



        var path =
            Constrain.baseUrl + "/profile/" + hisImage!!.substring(hisImage.lastIndexOf("/") + 1)

        var bitmap = Constrain.getBitmapFromURL(path)


        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setColor(ResourcesCompat.getColor(resources, R.color.holo_green_dark, null))
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(bitmap)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setSound(uri)

        val intent = Intent(this,ChatGroupActivity::class.java)


        intent.putExtra("groupId",groupId)
        intent.putExtra("groupCreateBy",groupCreateBy)
        intent.putExtra("groupImage",groupImage)
        intent.putExtra("groupName",groupName)
        intent.putExtra("groupDescription",groupDescription)


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(85 - 65), builder.build())
    }


    override fun onNewToken(s: String) {
        updateToken(s)
//        if (ActionActivity.client != null && ActionActivity.client!!.isConnected()) {
//            ActionActivity.client!!.registerPushToken(s, object : StatusListener() {
//                override fun onSuccess() {
//                    FirebaseDatabase.getInstance().getReference("users")
//                        .child(uid!!).child("token").setValue(s)
//                }
//            })
//        }
        super.onNewToken(s)
    }

    private fun updateToken(token: String) {
        if (uid != null) {
            val databaseReference =
                Constrain.initFirebase("Tokens").child(
                    uid!!
                )
            val map: HashMap<String, Any> = HashMap()
            map.put("token", token);
            databaseReference.updateChildren(map)
        }
    }
}