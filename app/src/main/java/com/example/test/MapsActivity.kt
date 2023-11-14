package com.example.test

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
class MapsActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationTextView = findViewById(R.id.locationTextView)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // 위치 권한을 체크하고 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        // 위치 업데이트를 받을 리스너 등록
        val locationListener = MyLocationListener()
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000, 10f, locationListener
        )
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude

            locationTextView.text = "위도: $latitude, 경도: $longitude"
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // 위치 제공자 상태 변경 처리
        }

//        override fun onProviderEnabled(provider: String?) {
//            // 위치 제공자 활성화 시 처리
//        }
//
//        override fun onProviderDisabled(provider: String?) {
//            // 위치 제공자 비활성화 시 처리
//        }
    }
}