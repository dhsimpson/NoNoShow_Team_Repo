package com.example.nonoshow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import com.example.nonoshow.MyApplication.Companion.bookingTextView
import kotlinx.android.synthetic.main.unlogin_popup.*


class unLoginBookingPopupActivity : Activity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.unlogin_popup)

        buttonConfirm.setOnClickListener{
            intent = Intent()
            intent.putExtra("result", "Close Popup")
            setResult(RESULT_OK, intent)
            bookingTextView!!.text = "예약하기"
            bookingTextView!!.setOnClickListener{/*비회원이지만 개인정보를 입력한자의 예약 실행*/
            }
            //액티비티(팝업) 닫기
            finish()
        }
        buttonCancel.setOnClickListener{
            intent = Intent()
            intent.putExtra("result", "Close Popup")
            setResult(RESULT_OK, intent)

            //액티비티(팝업) 닫기
            finish()
        }
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean{
        //바깥레이어 클릭시 안닫히게
        if(event.action == MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }
    override fun onBackPressed() {
        //안드로이드 백버튼 막기
        return
    }
}