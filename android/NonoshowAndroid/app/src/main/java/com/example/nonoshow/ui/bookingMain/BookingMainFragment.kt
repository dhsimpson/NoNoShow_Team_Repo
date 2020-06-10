package com.example.nonoshow.ui.bookingMain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.*
import com.example.nonoshow.MyApplication.Companion.CALENDAR
import com.example.nonoshow.MyApplication.Companion.IMAGE_BUTTON
import com.example.nonoshow.MyApplication.Companion.LINE
import com.example.nonoshow.MyApplication.Companion.LINEAR_LAYOUT
import com.example.nonoshow.MyApplication.Companion.MAPVIEW
import com.example.nonoshow.MyApplication.Companion.SPINNER
import com.example.nonoshow.MyApplication.Companion.TEXT_VIEW
import com.example.nonoshow.MyApplication.Companion.contextForList
import com.example.nonoshow.MyApplication.Companion.createView
import com.example.nonoshow.MyApplication.Companion.getImage
import com.example.nonoshow.MyApplication.Companion.tryLookComp
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.fragment_booking_main.*
import android.view.View.inflate
import androidx.core.app.ActivityCompat
import com.example.nonoshow.MyApplication.Companion.reservationCompName
import com.example.nonoshow.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.CameraUpdateFactory.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class BookingMainFragment : Fragment() ,OnMapReadyCallback{
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map!!.mapType = options.mapType
        map!!.uiSettings.isMyLocationButtonEnabled = true
        map!!.isMyLocationEnabled = true
        setMapLocation(map!!,position!!)
    }

    // val materialCalendarView : MaterialCalendarView? = createView()
    private lateinit var bookingMainViewModel: BookingMainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createBlocks()
        LL = LinearLayoutBookingManager
        root = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bookingMainViewModel =
            ViewModelProviders.of(this).get(BookingMainViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_booking_main, container, false)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)

        return root
    }
    companion object{
        @SuppressLint("StaticFieldLeak")
        var root : BookingMainFragment? = null
        @SuppressLint("StaticFieldLeak")
        var LL : LinearLayout? = null
        @SuppressLint("StaticFieldLeak")
        var selected : LinearLayout? = null
        @SuppressLint("StaticFieldLeak")
        var selectedInfo : LinearLayout? = null
        fun DBListenerClient(companyInfo : CompanyInfo){
            createABlock(LL!!,companyInfo)
        }
        fun createABlock(LL: LinearLayout,companyInfo : CompanyInfo): LinearLayout {    /*블록을 하나 생성하고 그 뷰를 리턴*/
            val tableRow : LinearLayout? = createView(
                type = LINEAR_LAYOUT,
                directionHorizontal = true, /*가로*/
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = 500,
                backGroundColor = R.color.colorWhite
            )
            val textView : TextView? = createView(
                type = TEXT_VIEW,
                text = companyInfo.name,
                textSize = 24f,
                width = ViewGroup.LayoutParams.MATCH_PARENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val textViewSub : TextView? = createView(
                type = TEXT_VIEW,
                text = companyInfo.info, /* 간단한 설명  */
                textColor = R.color.colorGray140,
                textSize = 16f,
                width =  ViewGroup.LayoutParams.MATCH_PARENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT,
                marginLeft = 64,
                marginTop = 32
            )
            val imageButton : ImageButton? = createView(
                type = IMAGE_BUTTON,
                width = 0,
                height = ViewGroup.LayoutParams.WRAP_CONTENT,
                weight = .33f,
                imageId = R.drawable.test_photo_1
            )
            getImage(companyInfo.imageSrc,imageButton!!)
            val textGroup : LinearLayout? = createView(
                type = LINEAR_LAYOUT,
                width = 0,
                height = ViewGroup.LayoutParams.MATCH_PARENT,
                weight = .67f,
                directionHorizontal = false, /*세로*/
                marginHorizontal = 64,
                marginTop = 32,
                marginBottom = 64
            )
            val block : LinearLayout? = createView(
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

            settingTableRowClickListener(block, tableRow,companyInfo) /*클릭시 정보생성*/

            return block
        }
        fun settingTableRowClickListener(block : LinearLayout, tableRow : LinearLayout,companyInfo : CompanyInfo){
            tableRow.setOnClickListener{/*선택됨 - 선택된 녀석의 정보 보여주기! + 선택상태를 저장*/
                setMapLocation(map!!,LatLng(companyInfo.lat,companyInfo.lng))
                if(selectedInfo != null) {
                    selectedInfo!!.removeView(mapView)
                    selected!!.removeView(selectedInfo) /* 다른녀석이 선택되면 이전 선택된 info 제거*/
                }
                val info : LinearLayout? = createView(
                    type = LINEAR_LAYOUT
                )
                info!!.addView(createView<View>(
                    type = LINE,
                    directionHorizontal = true,
                    backGroundColor = R.color.colorGray207)) /*가로선*/
                info.addView(mapView)
                info.addView(createView<View>(
                    type = LINE,
                    directionHorizontal = true,
                    backGroundColor = R.color.colorGray207)) /*가로선*/
                info.addView(createView<TextView>(
                    type = TEXT_VIEW,
                    text = "\n회사 : "+companyInfo.name+"\n" + "주소 : "+ companyInfo.address,
                    textColor = R.color.colorGray140,
                    textSize = 16f,
                    width =  ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT,
                    marginLeft = 64,
                    marginVertical = 32
                ))
                info.addView(createView<View>(
                    type = LINE,
                    directionHorizontal = true,
                    backGroundColor = R.color.colorLightGray)) /*가로선*/
                info.addView(createView<TextView>(
                    type = TEXT_VIEW,
                    text = "예약하기",
                    textSize = 20f,
                    marginHorizontal = 256,
                    marginVertical = 64,
                    background = R.drawable.edit_text_customize_primary,
                    textAlignCenter = true,
                    textColor = R.color.colorPrimary,
                    height = 300
                ).apply{
                    this!!.setOnClickListener{
                        //매장이름을 저장하도록 하여 다른액티비티에서 꺼내쓰자
                        reservationCompName = companyInfo.name
                        val intent = Intent(contextForList,bookingManager::class.java)
                        root!!.startActivity(intent)
                    }
                })

                block.addView(info) /*블록에 정보를 붙임*/
                tableRow.setOnClickListener{/*위쪽 테이블을 눌렀을경우 INFO 창 제거를 위해*/
                    info.removeView(mapView)
                    settingTableRowClickListener(block, tableRow,companyInfo)/*INFO 가 제거된 테이블의 setOnClickListener 초기화를 위해*/
                    block.removeView(info)
                    selected = null
                    selectedInfo = null
                }
                selected = block /* 선택 */
                selectedInfo = info
            }
        }
        fun setMapLocation(map : GoogleMap,position : LatLng) {
            with(map) {
                moveCamera(newLatLngZoom(position, 15f))
                addMarker(MarkerOptions().position(position))
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
        var map : GoogleMap? =null
        val mapView = MapView(contextForList).apply{
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1500
            )
        }
        var position : LatLng? = LatLng(37.555744, 126.970431)
        var options : GoogleMapOptions = GoogleMapOptions().liteMode(true)
    }
    private fun createBlocks() {
        tryLookComp(null,false)
    }
    private fun setMapLocation(map : GoogleMap,position : LatLng) {
        with(map) {
            moveCamera(newLatLngZoom(position, 15f))
            addMarker(MarkerOptions().position(position))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }
    override fun onResume() {
        if(mapView != null)
            mapView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

}