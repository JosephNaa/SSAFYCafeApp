package com.ssafy.smartstoredb.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.util.MyAppWidgetProvider
import java.util.*

class MyFirebaseMessagingService: FirebaseMessagingService() {

    var fcmList = ArrayList<String>()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // 앱이 foreground 상태에 있을 때 FCM 알림을 받았다면 onMessageReceived() 콜백 메소드가 호출됨으로써 FCM 알림이 대신된다.
        Log.d(TAG, "From: ${remoteMessage.from}")

        var notificationInfo: Map<String, String> = mapOf()

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(this, MyAppWidgetProvider::class.java)
        )

        // 메시지 유형이 데이터 메시지일 경우
        // Check if message contains a data payload.
        var fcmData: String = ""
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            fcmData = remoteMessage.data.get("data").toString()

            notificationInfo = mapOf(
                "title" to remoteMessage.data["title"].toString(),
                "body" to remoteMessage.data["body"].toString()
            )

            fcmList = readSharedPreference("fcm")
            fcmList.add(0, remoteMessage.data["body"].toString())
            writeSharedPreference("fcm", fcmList)

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView_notice)

            sendNotification(notificationInfo)
        }

        // 메시지 유형이 알림 메시지일 경우
        // Check if message contains a notification payload.
        // Set FCM title, body to android notification
        remoteMessage.notification?.let {
            notificationInfo = mapOf(
                "title" to it.title.toString(),
                "body" to it.body.toString()
            )
            fcmList = readSharedPreference("fcm")
            fcmList.add(it.body.toString())
            writeSharedPreference("fcm", fcmList)
            sendNotification(notificationInfo)
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        // 이 메소드가 호출되었을 때 실행해야 할 작업이 존재하면 여기에 작성하기
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        /** 변경된 토큰 가져오기 및 확인하기 */
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("newFCMToken", token.toString())

            MainActivity.uploadToken(token)
        })
    }

    /**
     * 푸시 메시지의 세부 설정을 하고, 안드로이드 앱에 푸시 메시지를 보내는 메소드
     *
     * onMessagedReceived() 콜백 메소드에서 FCM이 보낸 메시지의 title, body 등을 알아와 sendNotification()의 매개변수로 넘기면 됨
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

//        val channelId = getString(R.string.default_notification_channel_id)
        val channelId = "sssss"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // icon, color는 메타 데이터에서 설정한 것으로 설정해주면 된다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(messageBody["title"])
            .setContentText(messageBody["body"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(pendingIntent, true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    // SP 저장
    private fun writeSharedPreference(key:String, value:ArrayList<String>){
        val sp = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        val editor = sp.edit()
        val gson = Gson()
        val json: String = gson.toJson(value)
        editor.putString(key, json)
        editor.apply()
    }

    // SP 읽기
    private fun readSharedPreference(key:String): ArrayList<String>{
        val sp = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString(key, "") ?: ""
        val type = object : TypeToken<ArrayList<String>>() {}.type
        val obj: ArrayList<String> = gson.fromJson(json, type) ?: ArrayList()
        return obj
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService_싸피"
        private const val SP_NAME = "fcm_message"
    }
}