package com.example.nonoshow.data

import android.util.Log
import com.example.nonoshow.ID_Token
import com.example.nonoshow.MyApplication
import com.example.nonoshow.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FcmPush() {
    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")//Post전송 JSON Type
        val url = "https://fcm.googleapis.com/fcm/send" //FCM HTTP를 호출하는 URL
        val serverKey = MyApplication.contextForList!!.resources.getString(R.string.firebaseKey)
        //Firebase에서 복사한 서버키
        var okHttpClient = OkHttpClient()
        var gson = Gson()

        fun sendMessage(title: String, message: String, ID_token: ID_Token) {
            val token = ID_token.token
            Log.i("토큰정보", token)
            val pushDTO = PushDTO()
            pushDTO.to = token                   //푸시토큰 세팅
            pushDTO.notification?.title = title  //푸시 타이틀 세팅
            pushDTO.notification?.body = message //푸시 메시지 세팅

            val body = RequestBody.create(JSON, gson.toJson(pushDTO)!!)
            val request = Request
                .Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + serverKey)
                .url(url)       //푸시 URL 세팅
                .post(body)     //pushDTO가 담긴 body 세팅
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                //푸시 전송
                override fun onFailure(call: Call?, e: IOException?) {
                }

                override fun onResponse(call: Call?, response: Response?) {
                    println(response?.body()?.string())  //요청이 성공했을 경우 결과값 출력
                }
            })
        }
    }

    data class PushDTO(
        var to: String? = null,                             //PushToken을 입력하는 부분 푸시를 받는 사용자
        var notification: Notification? = Notification()    //백그라운드 푸시 호출하는 변수
    ) {
        data class Notification(
            var body: String? = null,                       //백그라운드 푸시 메시지 내용
            var title: String? = null                       //백그라운드 푸시 타이틀
        )
    }
}
