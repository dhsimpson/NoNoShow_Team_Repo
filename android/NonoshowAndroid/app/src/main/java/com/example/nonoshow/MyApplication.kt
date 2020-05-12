package com.example.nonoshow

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.*
import java.util.*

class MyApplication : Application() { /*하나의 인스턴스를 가지는 클래스*/
    companion object {
        const val LINEAR_LAYOUT = 1004
        const val TEXT_VIEW = 1015
        const val IMAGE_BUTTON = 1026
        const val LINE = 1037
        const val CALENDAR = 1048
        const val SPINNER = 1059
        const val DEFAULT = 8000
        const val LOGINED = 0
        @SuppressLint("StaticFieldLeak")
        var contextForList: Context? = null
        var state = DEFAULT /*내 상태 저장*/
        fun logout() {
            ID = "default"
            PW = "default"
            isLogined = false
            var loginToken = ""
        }

        /*static at Kotlin*/
        var ID = "default"
        var PW = "default"
        var isLogined = false
        var loginToken = "" /*서버에서 암호화해서 보내준 녀석을 저장<나중에 업데이트>*/
        var bookingTextView : TextView? = null

        fun <T> createView(
            type: Int,     /*0 = LL 1 = textView 2 = ImageButton */
            directionHorizontal: Boolean = false,    /*레이아웃 방향 true = 가로*/
            text: String = "",
            textSize: Float = 24f,
            width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginHorizontal: Int = 0, /*가로 마진*/
            marginVertical: Int = 0,
            marginLeft: Int = marginHorizontal,
            marginTop: Int = marginVertical,
            marginRight: Int = marginHorizontal,
            marginBottom: Int = marginVertical,
            backGroundColor: Int = R.color.colorWhite,
            weight: Float = 0f,
            imageId: Int = R.color.colorWhite,
            background: Int = backGroundColor,
            textColor: Int = android.R.color.black,
            textAlignCenter : Boolean = false,
            startNum : Int = 0,
            endNum : Int = 24,
            list: Array<String> = Array(endNum-startNum+1/*배열크기*/){""}.apply{   /*list를 직접 넣지않을경우 숫자로만 이루어진 아이템들을 설정*/
                var count = 0
                for (i in startNum..endNum) {
                    this[count++] = (i).toString()
                }
            }

        ): T? {
            val context = contextForList!! /*context 문제*/
            var result: T = View(context) as T
            when (type) {
                LINEAR_LAYOUT -> {   /*LL*/
                    result = LinearLayout(context).apply {
                        when (directionHorizontal) {
                            true -> orientation = LinearLayout.HORIZONTAL
                            false -> orientation = LinearLayout.VERTICAL
                        }
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        val param = layoutParams as ViewGroup.MarginLayoutParams    /*마진설정*/
                        param.setMargins(marginLeft, marginTop, marginRight, marginBottom)
                        setBackgroundColor(     /*배경 색 설정*/
                            ContextCompat.getColor(
                                context,
                                backGroundColor
                            )
                        )  /*backgroundColor 설정*/
                        weightSum = 1f
                    } as T
                }
                TEXT_VIEW -> {  /*textView*/
                    result = TextView(context).apply {
                        this.text = text /*  이곳에 매장의 이름이 들어와야 함  */
                        this.textSize = textSize
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                textColor
                            )
                        )
                        if(textAlignCenter){
                            textAlignment = View.TEXT_ALIGNMENT_CENTER
                            gravity = Gravity.CENTER
                        }
                        val param = layoutParams as ViewGroup.MarginLayoutParams    /*마진설정*/
                        param.setMargins(marginLeft, marginTop, marginRight, marginBottom)
                        this.background = ContextCompat.getDrawable(context, background)
                    } as T
                }
                IMAGE_BUTTON -> { /*ImageButton*/
                    result = ImageButton(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        /*gravity = layout_centerVertical 위치 정렬 - 이상하게 안됨 일단 미구현*/
                        this.setImageResource(imageId)    /*사진도 나중에 구현*/
                        this.background = ContextCompat.getDrawable(context, background)
                        adjustViewBounds = true
                    } as T
                }
                LINE -> {   /*line*/
                    result = View(context).apply {
                        when (directionHorizontal) {    /*true 면 가로선 (height 가 6)*/
                            true -> layoutParams =
                                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6)
                            false -> layoutParams =
                                LinearLayout.LayoutParams(6, ViewGroup.LayoutParams.MATCH_PARENT)
                        }
                        setBackgroundColor(     /*배경 색 설정*/
                            ContextCompat.getColor(
                                context,
                                backGroundColor
                            )
                        )
                    } as T
                }
                CALENDAR -> {
                    result = MaterialCalendarView(context).apply {
                        state().edit()
                            .setFirstDayOfWeek(Calendar.SUNDAY)
                            .setMinimumDate(CalendarDay.from(2017, 0, 1))
                            .setMaximumDate(CalendarDay.from(2030, 11, 31))
                            .setCalendarDisplayMode(CalendarMode.MONTHS)
                            .commit()
                        setOnDateChangedListener { widget, date, selected ->
                            run{
                                Log.i("calendar", date.toString())
                            }
                        }  /*날짜가 선택됐을 때*/
                    } as T
                }
                SPINNER -> {
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, list)
                    result = Spinner(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        ListView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                width,
                                height
                            )
                        }
                        setAdapter(adapter)
                    } as T
                }
            }
            return result
        }

        /******
         *이더리움 함수 이름 custSignIn
         *******/
        fun trySignIn(id : String ="",pw : String="") : String{ /*이더리움으로 부터 "client"->상태 고객 고유 ID와 true값을 받아 고유ID를 반환함*/

            MainActivity.changeState(ID, LOGINED)/*로그인 성공시 상태를 변경하며, 닉네임설정*/
            state = LOGINED
            var result = "err"


            return result
        }

        /******
         * 이더리움 함수 이름 custSignIn
         *******/
        fun trySignUp(phoneNumber : String, name : String, id : String, pw : String) : Boolean {    /*회원가입*/
            return false
        }
    }
}