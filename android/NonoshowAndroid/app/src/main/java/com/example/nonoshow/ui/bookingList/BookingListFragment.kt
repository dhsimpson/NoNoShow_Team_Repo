package com.example.nonoshow.ui.bookingList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.MyApplication.Companion.LINE
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.R
import android.widget.LinearLayout
import android.widget.ImageButton
import androidx.fragment.app.FragmentTransaction
import com.example.nonoshow.MyApplication.Companion.ID
import com.example.nonoshow.MyApplication.Companion.IMAGE_BUTTON
import com.example.nonoshow.MyApplication.Companion.LINEAR_LAYOUT
import com.example.nonoshow.MyApplication.Companion.TEXT_VIEW
import com.example.nonoshow.MyApplication.Companion.isLogined
import com.example.nonoshow.MyApplication.Companion.managerInfo
import com.example.nonoshow.MyApplication.Companion.managerMode
import com.example.nonoshow.MyApplication.Companion.modifyBooking
import com.example.nonoshow.MyApplication.Companion.tryLookReservation
import com.example.nonoshow.ReservationRequest
import kotlin.random.Random

class BookingListFragment : Fragment() {

    private lateinit var bookingListViewModel: BookingListViewModel

    override fun onResume() {
        super.onResume()
        if(requestlist!=null)
            requestlist!!.clear()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instance = this
        LL = requireView().findViewById(R.id.LinearLayoutList) as LinearLayout
        if(isLogined) {
            if (managerMode)/*매니저 서비스*/
                tryLookReservation(managerInfo!!.name)
            else /*고객 서비스*/
                tryLookReservation(ID)
        }
        else {
            Log.i("isLogined","false")
            //TODO("로그인 해 주세요.같은 글을 보여주기")
        }
    }

    fun refresh(){
        requestlist!!.clear()
        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.detach(this).attach(this).commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bookingListViewModel =
            ViewModelProviders.of(this).get(BookingListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_booking_list, container, false)

        return root
    }

    companion object{
        var instance : BookingListFragment? = null
        var LL : LinearLayout? = null
        var requestlist : ArrayList<ReservationRequest>? = ArrayList()
        fun createABlock(request : ReservationRequest) {
            var err = false
            for (req in requestlist!!){
                if(req.phoneNum == request.phoneNum && req.date == request.date && req.time == request.time){
                    err = true
                }
            }
            if(err){
                err = false
            }
            else {
                requestlist!!.add(request)
                if (!managerMode) {
                    val tableRow: LinearLayout? = createView(
                        type = LINEAR_LAYOUT,
                        directionHorizontal = true, /*가로*/
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = 500,
                        backGroundColor = R.color.colorGreenWhite
                    )
                    val textView: TextView? = createView(
                        type = TEXT_VIEW,
                        text = request.compName,
                        textSize = 24f,
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        backGroundColor = R.color.colorGreenWhite
                    )
                    val textViewSub: TextView? = createView(
                        type = TEXT_VIEW,
                        text = "예약날짜 : " + request.date + "\n예약시간 : " + request.time + "\n 상태 : " + request.state, /* 날짜 등 */
                        textColor = R.color.colorGray140,
                        textSize = 16f,
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        marginLeft = 64,
                        marginTop = 32,
                        backGroundColor = R.color.colorGreenWhite
                    )
                    val imageButton: ImageButton? = createView(
                        type = IMAGE_BUTTON,
                        width = 0,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight = .33f,
                        imageId = R.drawable.load_image,
                        backGroundColor = R.color.colorGreenWhite
                    )

                    val textGroup: LinearLayout? = createView(
                        type = LINEAR_LAYOUT,
                        width = 0,
                        height = ViewGroup.LayoutParams.MATCH_PARENT,
                        weight = .67f,
                        directionHorizontal = false, /*세로*/
                        marginHorizontal = 64,
                        marginTop = 32,
                        marginBottom = 64,
                        backGroundColor = R.color.colorGreenWhite
                    )
                    val block: LinearLayout? = createView(
                        type = LINEAR_LAYOUT,
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        marginHorizontal = 24,
                        marginVertical = 10,
                        directionHorizontal = false /*세로*/,
                        backGroundColor = R.color.colorGreenWhite
                    )


                    /*뷰와 레이아웃의 연결*/
                    textGroup!!.addView(textView)
                    textGroup.addView(textViewSub)
                    tableRow!!.addView(textGroup)
                    tableRow.addView(imageButton)
                    block!!.addView(tableRow)
                    LL!!.addView(block)
                }   /*고객 유저*/
                else {
                    var color: Int = R.color.colorWhite
                    var text: String = "대기중"
                    val randomValue = Random.nextInt(0, 100)
                    val buttonGroup: LinearLayout? = createView(
                        type = LINEAR_LAYOUT,
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        directionHorizontal = true, /*가로*/
                        backGroundColor = color
                    )
                    when (request.state) {
                        "waiting" -> {
                            color = R.color.colorLightGray
                            text = "예약 허용 대기중"
                            buttonGroup!!.addView(createView<TextView>(
                                type = TEXT_VIEW,
                                text = "허용",
                                textSize = 20f,
                                background = R.drawable.edit_text_customize_primary,
                                textAlignCenter = true,
                                textColor = R.color.colorPrimary,
                                width = 0,
                                height = 180,
                                weight = .5f
                            ).apply {
                                this!!.setOnClickListener {
                                    requestlist!!.clear()
                                    request.state = "allowed"
                                    modifyBooking(request, instance!!)
                                }
                            }
                            )
                            buttonGroup.addView(createView<TextView>(
                                type = TEXT_VIEW,
                                text = "거부",
                                textSize = 20f,
                                background = R.drawable.edit_text_customize_primary,
                                textAlignCenter = true,
                                textColor = R.color.colorPrimary,
                                width = 0,
                                height = 180,
                                weight = .5f
                            ).apply {
                                this!!.setOnClickListener {
                                    requestlist!!.clear()
                                    request.state = "rejected"
                                    modifyBooking(request, instance!!)
                                }
                            })

                        }/*대기중*/
                        "allowed" -> {
                            color = R.color.colorGreenWhite
                            text = "예약됨"
                            buttonGroup!!.addView(createView<TextView>(
                                type = TEXT_VIEW,
                                text = "취소",
                                textSize = 20f,
                                background = R.drawable.edit_text_customize_primary,
                                textAlignCenter = true,
                                textColor = R.color.colorPrimary,
                                width = 0,
                                height = 180,
                                weight = 1f
                            ).apply {
                                this!!.setOnClickListener {
                                    requestlist!!.clear()
                                    request.state = "waiting"
                                    modifyBooking(request, instance!!)
                                }
                            })
                        }/*허가됨*/
                        "rejected" -> {
                            color = R.color.colorRedWhite
                            text = "거부됨"
                            buttonGroup!!.addView(createView<TextView>(
                                type = TEXT_VIEW,
                                text = "취소",
                                textSize = 20f,
                                background = R.drawable.edit_text_customize_primary,
                                textAlignCenter = true,
                                textColor = R.color.colorPrimary,
                                width = 0,
                                height = 180,
                                weight = 1f
                            ).apply {
                                this!!.setOnClickListener {
                                    requestlist!!.clear()
                                    request.state = "waiting"
                                    modifyBooking(request, instance!!)
                                }
                            })
                        }/*거부됨*/
                    }
                    val tableRow: LinearLayout? = createView(
                        type = LINEAR_LAYOUT,
                        directionHorizontal = true, /*가로*/
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = 600,
                        backGroundColor = color
                    )
                    val textView: TextView? = createView(
                        type = TEXT_VIEW,
                        text = text,
                        textSize = 24f,
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        backGroundColor = color
                    )
                    val textViewSub: TextView? = createView(
                        type = TEXT_VIEW,
                        text = "예약날짜 : " + request.date + "\n예약시간 : " + request.time + "\n 상태 : $text \n 신뢰도 : $randomValue%", /* 날짜 등 */
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
                        height = ViewGroup.LayoutParams.MATCH_PARENT,
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
                    block.addView(
                        createView<View>(
                            type = LINE,
                            directionHorizontal = true,
                            backGroundColor = R.color.colorLightGray
                        )
                    ) /*가로선*/
                    block.addView(buttonGroup)
                    LL!!.addView(block)
                }   /*관리자 유저*/
            }
        }
    }

}

