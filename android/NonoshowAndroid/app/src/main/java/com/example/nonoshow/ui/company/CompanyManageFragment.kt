package com.example.nonoshow.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.MyApplication
import com.example.nonoshow.MyApplication.Companion.IMAGE_BUTTON
import com.example.nonoshow.MyApplication.Companion.LINEAR_LAYOUT
import com.example.nonoshow.MyApplication.Companion.TEXT_VIEW
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.R
import kotlinx.android.synthetic.main.fragment_company_manage.*

class CompanyManageFragment : Fragment() {
    private lateinit var companyManageFragment: CompanyManageViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(true/*isLogined && managerMode*/){
            true->{
                println("is manager")
                createBlocks()
            }
            false->{
                println("permission not allow")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        companyManageFragment =
            ViewModelProviders.of(this).get(CompanyManageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_company_manage, container, false)

        return root
    }

    private fun createBlocks(LL : LinearLayout = LinearLayoutComp){
        createABlock(LL)
        createLast(LL)
    }

    private fun createABlock(LL: LinearLayout = LinearLayoutComp): LinearLayout {    /*블록을 하나 생성하고 그 뷰를 리턴*/
        val tableRow: LinearLayout? = createView(
            type = LINEAR_LAYOUT,
            directionHorizontal = true, /*가로*/
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = 500,
            backGroundColor = R.color.colorWhite
        )
        val textView: TextView? = createView(
            type = TEXT_VIEW,
            text = "흥부네오리",
            textSize = 24f,
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val textViewSub: TextView? = createView(
            type = TEXT_VIEW,
            text = "오리요리전문점\n경기도 시흥시\n캐주얼, 어린이 환영, 단체석", /* 간단한 설명  */
            textColor = R.color.colorGray140,
            textSize = 16f,
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginLeft = 64,
            marginTop = 32
        )
        val imageButton: ImageButton? = createView(
            type = IMAGE_BUTTON,
            width = 0,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            weight = .33f,
            imageId = R.drawable.test_photo_1
        )
        val textGroup: LinearLayout? = createView(
            type = LINEAR_LAYOUT,
            width = 0,
            height = ViewGroup.LayoutParams.MATCH_PARENT,
            weight = .67f,
            directionHorizontal = false, /*세로*/
            marginHorizontal = 64,
            marginTop = 32,
            marginBottom = 64
        )
        val block: LinearLayout? = createView(
            type = LINEAR_LAYOUT,
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginHorizontal = 24,
            marginVertical = 10,
            directionHorizontal = false /*세로*/
        )

        /*뷰와 레이아웃의 연결*/
        textGroup!!.addView(textView)
        textGroup.addView(textViewSub)
        tableRow!!.addView(textGroup)
        tableRow.addView(imageButton)
        block!!.addView(tableRow)
        LL.addView(block)
        return block
    }

    private fun createLast(LL:LinearLayout = LinearLayoutComp){
        val tableRow: LinearLayout? = createView(
            type = LINEAR_LAYOUT,
            directionHorizontal = true, /*가로*/
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = 500,
            backGroundColor = R.color.colorWhite
        )
        val imageButton: ImageButton? = createView(
            type = IMAGE_BUTTON,
            width = 0,
            height = ViewGroup.LayoutParams.MATCH_PARENT,
            weight = .2f,
            imageId = R.drawable.plus_icon
        )
        val block: LinearLayout? = createView(
            type = LINEAR_LAYOUT,
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginHorizontal = 24,
            marginVertical = 10,
            directionHorizontal = false /*세로*/
        )
        tableRow!!.addView(createView(
            type = TEXT_VIEW,
            width = 0,
            height = ViewGroup.LayoutParams.MATCH_PARENT,
            weight = .4f,
            marginHorizontal = 64,
            marginTop = 32,
            marginBottom = 64
        ))
        tableRow.addView(imageButton)
        tableRow.addView(createView(
            type = TEXT_VIEW,
            width = 0,
            height = ViewGroup.LayoutParams.MATCH_PARENT,
            weight = .4f,
            marginHorizontal = 64,
            marginTop = 32,
            marginBottom = 64
        ))
        block!!.addView(tableRow)
        block.setOnClickListener{
/*activity 쌓기*/
        }
        LL.addView(block)
    }
}