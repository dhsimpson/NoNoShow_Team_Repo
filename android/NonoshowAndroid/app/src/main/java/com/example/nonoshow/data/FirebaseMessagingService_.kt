package com.example.nonoshow.data
/**
 * https://beomseok95.tistory.com/119?category=1031942 에서 복사함
 * **/
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nonoshow.MainActivity
import com.example.nonoshow.MyApplication.Companion.managerMode
import com.example.nonoshow.R
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService_ : FirebaseMessagingService() {
    override fun onNewToken(p0: String) { //토큰이 변경되었을때 호출
        super.onNewToken(p0)
        //v17.0.0 이후부터는 onTokenRefresh()-depriciated
        //var pushToken = FirebaseInstanceId.getInstance().token
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var map = mutableMapOf<String, Any>()
        map["pushtoken"] = p0
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)

        //서버로 바뀐토큰 전송
        sendRegistrationToServer(p0)
    }
    private fun sendRegistrationToServer(token: String?) {
        //서버로 바뀐 토큰 전송할 메소드 작성하는 부분
    }
    override fun onMessageReceived(p0: RemoteMessage) { //푸시알림을 받았을때 호출되는 메소드
        Log.d(TAG, "From: ${p0?.from}")
        p0?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + p0.data)
            if (true) {/* 장기 실행 작업으로 데이터를 처리해야하는지 확인*/
                //장기 실행 작업 (10 초 이상)의 경우 Firebase Job Dispatcher를 사용하십시오
                scheduleJob()
            } else {
                //10 초 이내에 메시지 처리
                handleNow()
            }
        }

        // 메시지에 알림 페이로드가 포함되어 있는지 확인하십시오.
        p0?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body!!)
        }
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_push)
            .setContentTitle(if(managerMode){"예약 요청"}else{"내 예약상태 변경됨"})
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // android Oreo 알림 채널이 필요합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /*알림ID */, notificationBuilder.build())
    }

    private fun scheduleJob() { //장기작업인지(10초이상)일때 처리하는 메소드
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val myJob = dispatcher.newJobBuilder()
            .setService(MyJobService::class.java)
            .setTag("my-job-tag")
            .build()
        dispatcher.schedule(myJob)
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    companion object {
        private val TAG = "FirebaseMessageService"
    }
}
