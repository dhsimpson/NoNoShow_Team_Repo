package com.example.nonoshow.ui.company

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.CompanyInfo
import com.example.nonoshow.MyApplication.Companion.IMAGE_BUTTON
import com.example.nonoshow.MyApplication.Companion.LINEAR_LAYOUT
import com.example.nonoshow.MyApplication.Companion.TEXT_VIEW
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.MyApplication.Companion.getImage
import com.example.nonoshow.MyApplication.Companion.managerInfo
import com.example.nonoshow.MyApplication.Companion.tryLookComp
import com.example.nonoshow.R
import com.example.nonoshow.modify_comp
import kotlinx.android.synthetic.main.fragment_company_manage.*

class CompanyManageFragment : Fragment() {
    private lateinit var companyManageFragment: CompanyManageViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayout = LinearLayoutComp
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
    companion object{
        fun DBlistener(companyInfo : CompanyInfo){
            createABlock(linearLayout!!,companyInfo)
        }
        var linearLayout : LinearLayout? = null

        fun createABlock(LL: LinearLayout,companyInfo : CompanyInfo): LinearLayout {    /*블록을 하나 생성하고 그 뷰를 리턴*/
            val tableRow: LinearLayout? = createView(
                type = LINEAR_LAYOUT,
                directionHorizontal = true, /*가로*/
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = 500,
                backGroundColor = R.color.colorWhite
            )
            val textView: TextView? = createView(
                type = TEXT_VIEW,
                text = companyInfo.name,
                textSize = 24f,
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val textViewSub: TextView? = createView(
                type = TEXT_VIEW,
                text = companyInfo.info, /* 간단한 설명  */
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
                imageId = R.drawable.load_image
            )
            val image = imageButton as ImageView
            Log.i("sendRequest", companyInfo.imageSrc)
            getImage(companyInfo.imageSrc,imageButton)
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
            LL.bringChildToFront(block)
            return block
        }
    }


    private fun createBlocks(LL : LinearLayout = LinearLayoutComp){
        createLast(LL)
        if(managerInfo != null) {
            tryLookComp(managerInfo!!.name,true,managerInfo!!.id)
        }
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
            directionHorizontal = false /*세로*/,
            backGroundColor = R.color.colorLightGray
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
        imageButton!!.setOnClickListener{
/*activity 쌓기*/
            val intent = Intent(context,modify_comp::class.java)
            startActivity(intent)
        }
        LL.addView(block)
    }
}