package com.example.nonoshow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
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
class map : OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    var mMap : GoogleMap? = null

    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.i("map","좌표 : "+ p0!!.position)
        return true
    }
    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        var coordinate : LatLng? = null
        var marker : MarkerOptions? = null

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
}

