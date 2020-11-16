package com.example.nonoshow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.widget.TextView
import android.widget.Button
import com.example.nonoshow.data.GeocodeUtil
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory.newLatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_set_address_with_map.*

class setAddressWithMap : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    private var mMap : GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {    /*지도 */
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_set_address_with_map)

        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
        textViewConfirm.setOnClickListener{
            confirmMap()
        }
        val button = findViewById<Button>(R.id.geocoderButton)
        button.setOnClickListener{
            val address = findViewById<TextView>(R.id.adressGeocoder).text.toString()
            val geocodeUtil = GeocodeUtil(MyApplication.contextForList)
            val results = geocodeUtil.getGeoLocationListUsingAddress(address)
            if(results.size < 1){
                //검색어를 찾을 수 없음.
            }
            else {
                val first: GeocodeUtil.GeoLocation = results[0]
                val position = LatLng(first.lat, first.lng)
                this.mMap!!.moveCamera(newLatLng(position))
                val mOptions: MarkerOptions = MarkerOptions()
                mOptions.position(position)
                modify_comp.position = position
                // 마커(핀) 추가
                selected = mMap!!.addMarker(mOptions) // 마커추가,화면에출력
            }
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
    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.i(TAG,"좌표 : "+ p0!!.position)
        return true
    }

    override fun onMapReady(p0: GoogleMap?) {
        var mMap = p0
        this.mMap = mMap
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true

        mMap.moveCamera(
            newLatLng(
                LatLng(37.555744, 126.970431))  // 위도, 경도
        )

        val zoom : CameraUpdate = com.google.android.gms.maps.CameraUpdateFactory.zoomTo(15f)
        mMap.animateCamera(zoom)

        mMap.setOnMapClickListener(GoogleMap.OnMapClickListener{
            if(selected != null){
                selected!!.remove()
            }
            val mOptions: MarkerOptions = MarkerOptions()
            // 마커 타이틀
            mOptions.title("마커 좌표")
            val latitude = it.latitude // 위도
            val longitude = it.longitude // 경도
            // 마커의 스니펫(간단한 텍스트) 설정
            mOptions.snippet("$latitude, $longitude")
            // LatLng: 위도 경도 쌍을 나타냄
            val position = LatLng(latitude,longitude)
            mOptions.position(position)
            modify_comp.position = position
            // 마커(핀) 추가
            selected = mMap.addMarker(mOptions) // 마커추가,화면에출력
        })

        /*marker.alpha(0.8f)
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))*/
    }
    companion object{
        private final val TAG = "mapActivity"
        var selected : Marker? = null
    }

    fun confirmMap(){
        //TODO(저장해둔 터치좌표를 가져와서 어딘가로 삽입)
        //uploadImageS3(filePath)
        this.finish()
    }
}
