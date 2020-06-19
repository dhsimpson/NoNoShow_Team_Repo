package com.example.nonoshow.ui.noShowManager

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.nonoshow.MyApplication.Companion.isLogined
import com.example.nonoshow.MyApplication.Companion.managerInfo
import com.example.nonoshow.MyApplication.Companion.managerMode
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.MyApplication.Companion.IMAGE_BUTTON
import com.example.nonoshow.MyApplication.Companion.LINE
import com.example.nonoshow.MyApplication.Companion.LINEAR_LAYOUT
import com.example.nonoshow.MyApplication.Companion.TEXT_VIEW
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.MyApplication.Companion.modifyShowNoShow
import com.example.nonoshow.MyApplication.Companion.tryLookShowNoShowList
import com.example.nonoshow.R
import com.example.nonoshow.ReservationRequest
import kotlinx.android.synthetic.main.fragment_noshow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

class NoShowManagerFragment : Fragment(){
    private lateinit var noShowManagerFragment : NoShowManagerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LL = LLNoShow
        instance = this
        if(isLogined) {
            if (managerMode)/*매니저 서비스*/
                tryLookShowNoShowList(managerInfo!!.name)
            else /*고객 서비스*/{
                //TODO("관리자가 아닙니다.")
            }

        }
        else {
            Log.i("isLogined","false")
            //TODO("로그인 해 주세요.같은 글을 보여주기")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        noShowManagerFragment =
            ViewModelProviders.of(this).get(NoShowManagerViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_noshow, container, false)

        return root
    }

    fun refresh(){
        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.detach(this).attach(this).commit()
    }

    companion object{
        enum class stateEnum{
            yet, //아직 수정 할 수 없음.
            show, //수정가능 현재는 쇼
            noShow, //수정가능 현재는 노쇼
            late //시간이 지나서 수정불가능
        }
        var state = stateEnum.yet

        var instance : NoShowManagerFragment? = null
        var LL : LinearLayout? = null

        fun createABlock(request : ReservationRequest) {
            var requestDateTime : LocalDateTime? = null
            var current : LocalDateTime? = null
            var permission : Boolean = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i("noShowManager","정상")
                val requestDateTime = getDateTime(request)
                val current = LocalDateTime.now()
                permission = permissionModifyNoshow(requestDateTime, current, 20)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            run{
                Log.i("noShowManager","run")
                var color : Int = R.color.colorWhite
                var text : String = "데이터 로드 중..."
                val randomValue = Random.nextInt(0,100)
                val buttonGroup: LinearLayout? = createView(
                    type = LINEAR_LAYOUT,
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    directionHorizontal = true, /*가로*/
                    backGroundColor = color
                )
                when(request.state){
                    "show"->{color = R.color.colorGreenWhite
                        text = if(state == stateEnum.yet){"아직 노쇼등록을 할 수 없음"}else if(state == stateEnum.late){"마감된 기록 : 변경불가능"} else{"show"}
                        if(permission){
                            buttonGroup!!.addView(createView<TextView>(
                                type = TEXT_VIEW,
                                text = "터치해 noShow로 등록",
                                textSize = 20f,
                                background = R.drawable.edit_text_customize_primary,
                                textAlignCenter = true,
                                textColor = R.color.colorPrimary,
                                width = ViewGroup.LayoutParams.MATCH_PARENT,
                                height = 300
                            ).apply{this!!.setOnClickListener{
                                request.state = "noShow"
                                modifyShowNoShow(request,instance!!)
                            }})
                            }
                    }/*show*/
                    "noShow"->{color = R.color.colorRedWhite
                        text = if(state == stateEnum.late){"마감된 기록 : 변경불가능"} else{"noShow"}
                        if(permission){
                            buttonGroup!!.addView(createView<TextView>(
                                type = TEXT_VIEW,
                                text = "터치해 Show로 변경",
                                textSize = 20f,
                                background = R.drawable.edit_text_customize_primary,
                                textAlignCenter = true,
                                textColor = R.color.colorPrimary,
                                width = ViewGroup.LayoutParams.MATCH_PARENT,
                                height = 300
                            ).apply{this!!.setOnClickListener{
                                request.state = "show"
                                modifyShowNoShow(request,instance!!)
                            }})
                        }
                    }/*noshow*/
                }
                val tableRow : LinearLayout? = createView(
                    type = LINEAR_LAYOUT,
                    directionHorizontal = true, /*가로*/
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    backGroundColor = color
                )
                val textView: TextView? = createView(
                    type = TEXT_VIEW,
                    text = "예약ID : "+ request.userID,
                    textSize = 24f,
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    backGroundColor = color
                )
                val textViewSub: TextView? = createView(
                    type = TEXT_VIEW,
                    text = "예약날짜 : "+ request.date + "\n예약시간 : " + request.time +"\n상태 : $text \n인원 : ${request.numberOfPerson}\n핸드폰번호 : ${request.phoneNum}", /* 날짜 등 */
                    textColor = R.color.colorGray140,
                    textSize = 16f,
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    marginLeft = 64,
                    marginTop = 32,
                    backGroundColor = color
                )
                val imageButton: ImageButton? = createView(
                    type = IMAGE_BUTTON,
                    width = 0,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    weight = .33f,
                    imageId = R.drawable.logo_transparent,
                    backGroundColor = color
                )

                val textGroup: LinearLayout? = createView(
                    type = LINEAR_LAYOUT,
                    width = 0,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    weight = .67f,
                    directionHorizontal = false, /*세로*/
                    marginHorizontal = 64,
                    marginTop = 32,
                    marginBottom = 64,
                    backGroundColor = color
                )
                val block: LinearLayout? = createView(
                    type = LINEAR_LAYOUT,
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    marginHorizontal = 24,
                    marginVertical = 10,
                    directionHorizontal = false /*세로*/,
                    backGroundColor = color
                )

                /*뷰와 레이아웃의 연결*/
                textGroup!!.addView(textView)
                textGroup.addView(textViewSub)
                tableRow!!.addView(textGroup)
                tableRow.addView(imageButton)
                block!!.addView(tableRow)
                block.addView(createView<View>(
                    type = LINE,
                    directionHorizontal = true,
                    backGroundColor = R.color.colorLightGray)) /*가로선*/
                block.addView(buttonGroup)
                LL!!.addView(block)
            }   /*관리자 유저*/
        }

        fun getDateTime(request : ReservationRequest) : LocalDateTime{
            val requestDate = request.date
            val requestTime = request.time
            var isPm = false

            var stringArray = requestDate.split("년 ")
            val year : Int = stringArray[0].toInt()

            stringArray = stringArray[1].split("월 ")
            val month : Int = stringArray[0].toInt()

            stringArray = stringArray[1].split("일")
            val day : Int = stringArray[0].toInt()

            stringArray = requestTime.split(" ")

            if(stringArray[0] == "오후")  isPm = true

            stringArray = stringArray[1].split("시")
            val hour = stringArray[0].toInt() + if(isPm){ 12} else{ 0}
            val minute = stringArray[1].split("분")[0].toInt()

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.of(LocalDate.of(year,month,day),LocalTime.of(hour,minute,0,0))
            } else {
                TODO("VERSION.SDK_INT < O")
            }

        }

        fun permissionModifyNoshow(requestDateTime : LocalDateTime, currentDateTime : LocalDateTime, delay : Int) : Boolean{
            var result = false
            val rDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateToInt(requestDateTime.toLocalDate())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val cDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateToInt(currentDateTime.toLocalDate())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val rTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                timeToInt(requestDateTime.toLocalTime())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val cTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                timeToInt(currentDateTime.toLocalTime())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            if(rDate <= cDate){
                if(rDate + 14 < cDate){
                    state = stateEnum.late //show noshow 수정하기에 너무 늦었음.
                    result = false
                }
                else if(rDate == cDate){
                    Log.i("rDate - cDate", "" + (rDate - cDate))
                    if(rTime+delay < cTime){
                        state = stateEnum.show  //수정가능하며 default는 show.
                        result = true
                    }
                    else{
                        state = stateEnum.yet //수정하기에 너무 이름.
                        result = false
                    }
                }
                else{
                    state = stateEnum.show  //수정가능하며 default는 show.
                    result = true
                }
            }
            else{
                state = stateEnum.yet //수정불가능 너무 이릅니다.
                result = false
            }

            return result
        }

        fun dateToInt(requestDate : LocalDate) : Int{
            val year = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDate.year
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            Log.i("year",year.toString())
            val month = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDate.month.value
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            Log.i("month",month.toString())
            val day = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDate.dayOfMonth
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            Log.i("day",day.toString())
            val yearM = year-1
            var result = yearM * 365 + (yearM/4) - (yearM / 100) + (yearM / 400)
            val c = if((year%4 == 0) &&((year%100 !=0) || (year%400 == 0) )){1} else{0}

            when(month) {
                1 -> {
                    result += day
                }
                2 -> {
                    result += 31 + day
                }
                3 -> {
                    result += 59 + day + c
                }
                4 -> {
                    result += 90 + day + c
                }
                5 -> {
                    result += 120 + day + c
                }
                6 -> {
                    result += 151 + day + c
                }
                7 -> {
                    result += 181 + day + c
                }
                8 -> {
                    result += 212 + day + c
                }
                9 -> {
                    result += 243 + day + c
                }
                10 -> {
                    result += 273 + day + c
                }
                11 -> {
                    result += 304 + day + c
                }
                12 -> {
                    result += 334 + day + c
                }
            }
            Log.i("dateToInt",result.toString())
            return result
        }

        fun timeToInt(requestTime : LocalTime) : Int{
            val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestTime.hour
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestTime.minute
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            Log.i("timeToInt",(hour * 60 + minute).toString())
            return hour * 60 + minute
        }

    }
}