package com.example.nonoshow.ui.bookingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.MyApplication
import com.example.nonoshow.MyApplication.Companion.LINE
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.R
import android.widget.LinearLayout
import android.widget.ImageButton
import com.example.nonoshow.MyApplication.Companion.IMAGE_BUTTON
import com.example.nonoshow.MyApplication.Companion.LINEAR_LAYOUT
import com.example.nonoshow.MyApplication.Companion.TEXT_VIEW
import com.example.nonoshow.MyApplication.Companion.managerMode
import kotlinx.android.synthetic.main.fragment_booking_list.*
import kotlin.random.Random

class BookingListFragment : Fragment() {

    private lateinit var bookingListViewModel: BookingListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createBlocks()

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

    private fun createBlocks(LL: LinearLayout = LinearLayoutList){
        createABlock(LL,0)
        createABlock(LL,1)
        createABlock(LL,2)
    }
    private fun createABlock(LL: LinearLayout = LinearLayoutList, status : Int = 0) {
        if(!managerMode) {
            val tableRow : LinearLayout? = createView(
                type = LINEAR_LAYOUT,
                directionHorizontal = true, /*가로*/
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = 500,
                backGroundColor = R.color.colorGreenWhite
            )
            val textView: TextView? = createView(
                type = TEXT_VIEW,
                text = "흥부네오리",
                textSize = 24f,
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT,
                backGroundColor = R.color.colorGreenWhite
            )
            val textViewSub: TextView? = createView(
                type = TEXT_VIEW,
                text = "예약날짜 : 2020년 5월 13일 \n예약시간 : 오후 7시 30분\n 상태 : 예약 허용 대기중", /* 날짜 등 */
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
                imageId = R.drawable.test_photo_1,
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
            LL.addView(block)
        }
        else{
            var color : Int = R.color.colorWhite
            var text : String = "대기중"
            val randomValue = Random.nextInt(0,100)
            val buttonGroup: LinearLayout? = createView(
                type = LINEAR_LAYOUT,
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT,
                directionHorizontal = true, /*가로*/
                backGroundColor = color
            )
            when(status){
                0->{color = R.color.colorLightGray
                    text = "예약 허용 대기중"
                    buttonGroup!!.addView(createView<TextView>(
                        type = TEXT_VIEW,
                        text = "허용",
                        textSize = 20f,
                        background = R.drawable.edit_text_customize_primary,
                        textAlignCenter = true,
                        textColor = R.color.colorPrimary,
                        width = 0,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight = .5f
                    ))
                    buttonGroup.addView(createView<TextView>(
                        type = TEXT_VIEW,
                        text = "거부",
                        textSize = 20f,
                        background = R.drawable.edit_text_customize_primary,
                        textAlignCenter = true,
                        textColor = R.color.colorPrimary,
                        width = 0,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight = .5f
                        ))

                }/*대기중*/
                1->{color = R.color.colorGreenWhite
                    text = "예약됨"
                    buttonGroup!!.addView(createView<TextView>(
                        type = TEXT_VIEW,
                        text = "취소",
                        textSize = 20f,
                        background = R.drawable.edit_text_customize_primary,
                        textAlignCenter = true,
                        textColor = R.color.colorPrimary,
                        width = 0,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight = 1f
                    ))
                }/*허가됨*/
                2->{color = R.color.colorRedWhite
                    text = "거부됨"
                    buttonGroup!!.addView(createView<TextView>(
                        type = TEXT_VIEW,
                        text = "취소",
                        textSize = 20f,
                        background = R.drawable.edit_text_customize_primary,
                        textAlignCenter = true,
                        textColor = R.color.colorPrimary,
                        width = 0,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight = 1f
                    ))
                }/*거부됨*/
            }
            val tableRow : LinearLayout? = createView(
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
                text = "예약날짜 : 2020년 5월 13일 \n예약시간 : 오후 7시 30분\n 상태 : $text \n 신뢰도 : $randomValue%", /* 날짜 등 */
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
            block.addView(createView<View>(
                type = LINE,
                directionHorizontal = true,
                backGroundColor = R.color.colorLightGray)) /*가로선*/
            block.addView(buttonGroup)
            LL.addView(block)
        }

    }
}

