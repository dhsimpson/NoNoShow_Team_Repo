package com.example.nonoshow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.nonoshow.ui.bookingMain.BookingMainFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory.newLatLng


/**
 * https://apis.map.kakao.com/android/guide/
 *
 * //bitsoul.tistory.com/145 [Happy Programmer~]
 * https://medium.com/@logishudson0218/%EC%A7%80%EB%8F%84-api-01-72510b25e4bd
 * **/
class map : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    var mMap : GoogleMap? = null
    var position : LatLng? = LatLng(37.555744, 126.970431)
    var options : GoogleMapOptions = GoogleMapOptions().liteMode(true)

    val mapView = MapView(context).apply{
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            1500
        )
    }
    fun map(){
        mMap!!.mapType = options.mapType
        mapView.getMapAsync(this)
    }



    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.i("map","좌표 : "+ p0!!.position)
        return true
    }
    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        //var coordinate : LatLng? = null
        var marker : MarkerOptions?
        mMap!!.uiSettings.isMyLocationButtonEnabled = false
        mMap!!.isMyLocationEnabled = true
        setMapLocation(mMap!!,position!!)

        mMap!!.moveCamera(
            newLatLng(
                LatLng(37.555744, 126.970431))  // 위도, 경도
        )

        val zoom : CameraUpdate = com.google.android.gms.maps.CameraUpdateFactory.zoomTo(15f)
        mMap!!.animateCamera(zoom)

        marker = MarkerOptions()
        marker.position(LatLng(37.555744, 126.970431))
            .title("서울역")
            .snippet("Seoul Station")
        mMap!!.addMarker(marker).showInfoWindow() // 마커추가,화면에출력

        /*marker.alpha(0.8f)
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))*/
        mMap!!.addMarker(marker)
    }
    private fun setMapLocation(map : GoogleMap,position : LatLng) {
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            addMarker(MarkerOptions().position(position))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }
    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }
}

