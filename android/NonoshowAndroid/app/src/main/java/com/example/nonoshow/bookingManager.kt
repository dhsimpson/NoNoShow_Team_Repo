/*예약하기를 눌렀을 때 나오는 레이아웃을 컨트롤하는 클래스*/
package com.example.nonoshow

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nonoshow.MyApplication.Companion.contextForList
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.nonoshow.MyApplication.Companion.bookingTextView
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.MyApplication.Companion.isLogined
import kotlinx.android.synthetic.main.booking_manager.*
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log.*
import java.util.*
import android.widget.DatePicker
import com.example.nonoshow.MyApplication.Companion.ID
import com.example.nonoshow.MyApplication.Companion.ampm
import com.example.nonoshow.MyApplication.Companion.hour
import com.example.nonoshow.MyApplication.Companion.minute
import com.example.nonoshow.MyApplication.Companion.numberOfPerson
import com.example.nonoshow.MyApplication.Companion.tryBooking


class bookingManager : AppCompatActivity() {
  private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
  private val TAG: String = "bookingManager"
    var dateString : String = "default"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.booking_manager)
    contextForList = this

    initialSetting(bookingDetailLinearLayout)
    /**뒤로가기 버튼을 위한 툴바**/
    val mToolbar = findViewById<View>(R.id.toolbarBooking) as Toolbar
    setSupportActionBar(mToolbar)

    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    /**                         **/
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    /*툴바에 뒤로가기버튼기능을 핸드폰의 뒤로가기버튼의 동작과 같도록 함*/
    when (item.itemId) {
      android.R.id.home -> {
        finish()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }
    fun finishActivity(){
        this.finish()
    }
  fun initialSetting(LL: LinearLayout) {
    LL.addView(createView<View>(
            type = MyApplication.LINE,
            directionHorizontal = true,
            backGroundColor = R.color.colorLightGray)) /*가로선*/
    LL.addView(createView<TextView>(
            type = MyApplication.TEXT_VIEW,
            text = "예약날짜 선택",
            backGroundColor = R.color.colorPrimary,
            textSize = 16f,
            width = ViewGroup.LayoutParams.WRAP_CONTENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginLeft = 64,
            marginTop = 32,
            textColor = R.color.colorWhite
    ))
    LL.addView(createView<TextView>(    /*날짜 선택 TextView 클릭시 DataPickerDialog 팝업생성*/
            type = MyApplication.TEXT_VIEW,
            text = "예약 날짜 선택",
            textSize = 20f,
            marginHorizontal = 256,
            marginVertical = 64,
            background = R.drawable.edit_text_customize_primary,
            textAlignCenter = true,
            textColor = R.color.colorPrimary,
            height = 300
    ).apply {
      val cal: Calendar = Calendar.getInstance()    /*초기설정*/
      var y = cal.get(Calendar.YEAR)
      var m = cal.get(Calendar.MONTH)
      var d = cal.get(Calendar.DAY_OF_MONTH)
      var monthL = m + 1
      var date = "$y" + "년 " + "$monthL" + "월 " + "$d" + "일"

      mDateSetListener =
              DatePickerDialog.OnDateSetListener { dataPicker: DatePicker, year, month, day ->
                y = year
                m = month
                d = day
                monthL = m + 1
                d(TAG, "onDateSet: mm/dd/yyy: $monthL/$day/$year")
                date = "$year" + "년 " + "$monthL" + "월 " + "$day" + "일"
                  dateString = date
                this!!.text = date

              }

      this!!.setOnClickListener {
        this.text = date
        val dialog = DatePickerDialog(
                context,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                y, m, d)
        val window = dialog.window
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


      }
    })
    LL.addView(createView<View>(
            type = MyApplication.LINE,
            directionHorizontal = true,
            backGroundColor = R.color.colorLightGray)) /*가로선*/
    LL.addView(createView<TextView>(
            type = MyApplication.TEXT_VIEW,
            text = "예약시각 선택",
            backGroundColor = R.color.colorPrimary,
            textSize = 16f,
            width = ViewGroup.LayoutParams.WRAP_CONTENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginLeft = 64,
            marginTop = 32,
            textColor = R.color.colorWhite
    ))

    run {
      val textGroup0: LinearLayout? = createView(
              type = MyApplication.LINEAR_LAYOUT,
              directionHorizontal = true  /*가로*/,
              marginHorizontal = 80
      )
      textGroup0!!.addView(
              createView<Spinner>(
                      type = MyApplication.SPINNER,
                      width = 0,
                      height = ViewGroup.LayoutParams.WRAP_CONTENT,
                      weight = .3f,
                      list = contextForList!!.resources.getStringArray(R.array.time_array),    /*오전, 오후*/
              spinnerType = "ampm"
              )
      )
      textGroup0.addView(
              createView<Spinner>(
                      type = MyApplication.SPINNER,
                      width = 0,
                      height = ViewGroup.LayoutParams.WRAP_CONTENT,
                      weight = .3f,
                      startNum = 1,       /*시각 1시 ~ 12시*/
                      endNum = 12,
                  spinnerType = "hour"
              )
      )
      textGroup0.addView(
              createView<TextView>(
                      type = MyApplication.TEXT_VIEW,
                      text = "시",
                      textSize = 18f,
                      textColor = R.color.colorPrimary,
                      width = 0,
                      height = ViewGroup.LayoutParams.WRAP_CONTENT,
                      weight = .05f
              )
      )
      textGroup0.addView(
              createView<Spinner>(
                      type = MyApplication.SPINNER,
                      width = 0,
                      height = ViewGroup.LayoutParams.WRAP_CONTENT,
                      weight = .3f,
                      startNum = 0,
                      endNum = 59,
                  spinnerType = "minute"
              )
      )
      textGroup0.addView(
              createView<TextView>(
                      type = MyApplication.TEXT_VIEW,
                      text = "분",
                      textSize = 18f,
                      textColor = R.color.colorPrimary,
                      width = 0,
                      height = ViewGroup.LayoutParams.WRAP_CONTENT,
                      weight = .05f
              )
      )
      LL.addView(textGroup0)
    }/*예약시각 선택 가로LinearLayout*/

    LL.addView(createView<View>(
            type = MyApplication.LINE,
            directionHorizontal = true,
            backGroundColor = R.color.colorLightGray)) /*가로선*/
    LL.addView(createView<TextView>(
            type = MyApplication.TEXT_VIEW,
            text = "예약인원 선택",
            backGroundColor = R.color.colorPrimary,
            textSize = 16f,
            width = ViewGroup.LayoutParams.WRAP_CONTENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginLeft = 64,
            marginTop = 32,
            textColor = R.color.colorWhite
    ))

    run {
      val textGroup1: LinearLayout? = createView(
              type = MyApplication.LINEAR_LAYOUT,
              directionHorizontal = true  /*가로*/,
              marginHorizontal = 80
      )
      textGroup1!!.addView(
              createView<TextView>(
                      type = MyApplication.TEXT_VIEW,
                      text = "예약인원 : ",
                      textSize = 24f,
                      textColor = R.color.colorPrimary,
                      width = 0,
                      weight = .5f,
                      marginLeft = 40,
                      marginTop = 8
              )
      )
      textGroup1.addView(
              createView<Spinner>(
                      type = MyApplication.SPINNER,
                      startNum = 1,
                      endNum = 30,
                      width = 0,
                      height = ViewGroup.LayoutParams.WRAP_CONTENT,
                      weight = .5f,
                  spinnerType = "numberOfPerson"
              )
      )
      LL.addView(textGroup1)
    }/*예약인원 선택 가로LinearLayout*/

    LL.addView(createView<View>(
            type = MyApplication.LINE,
            directionHorizontal = true,
            backGroundColor = R.color.colorLightGray)) /*가로선*/
    LL.addView(createView<TextView>(
            type = MyApplication.TEXT_VIEW,
            text = when (isLogined) {
              true -> "예약하기"
              false -> "비회원 예약하기"
            },
            textSize = 20f,
            marginHorizontal = 256,
            marginVertical = 64,
            background = R.drawable.edit_text_customize_primary,
            textAlignCenter = true,
            textColor = R.color.colorPrimary,
            height = 300
    ).apply {
      this!!.setOnClickListener {
          var time = ""
          time = if(ampm == "am") {
              "오전 "+hour+"시"+minute+"분"
          } else{
              "오후 "+hour+"시"+minute+"분"
          }
        when (isLogined) {
          true -> {/*이미 로그인 되었을 경우*/
            //("핸드폰번호, userID, 날짜, 시간, 예약인원,매장이름 으로 데이타를 생성 -> DB에 삽입하기 [MyApplication의 함수를 call]")
              tryBooking(MyApplication.userPhoneNum!!,ID,dateString,time,numberOfPerson.toString())
              finishActivity()
          }
          false -> {    /*비로그인상태일경우 - 매커니즘상 실행 안됨*/
            bookingTextView = this
            val intent = Intent(context, unLoginBookingPopupActivity::class.java)
            startActivity(intent)
              //("핸드폰번호, userID=name(임시), 날짜, 시간, 예약인원 으로 데이타를 생성 -> DB에 삽입하기")
          }
        }

      }
    })
  }
}