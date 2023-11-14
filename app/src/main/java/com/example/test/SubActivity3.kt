package com.example.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity


class SubActivity3:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub3)

        // 입력값 가져오기
        val xCoordinate = intent.getStringExtra("xCoordinate")
        val yCoordinate = intent.getStringExtra("yCoordinate")
        val stationName = intent.getStringExtra("stationName")
        // 위도와 경도를 받아온 후, Double로 변환
        val current_latitude = intent.getStringExtra("latitude")?.toDoubleOrNull()
        val current_longitude = intent.getStringExtra("longitude")?.toDoubleOrNull()

        Log.e("확인용1", xCoordinate.toString())
        Log.e("확인용1", yCoordinate.toString())
        Log.e("확인용1", stationName.toString())
        Log.e("확인용1", current_latitude.toString())
        Log.e("확인용1", current_longitude.toString())

        val current_latitude_view = findViewById<TextView>(R.id.current_latitude_view)
        current_latitude?.let { current_latitude_view.text = it.toString() }

        val current_longitude_view = findViewById<TextView>(R.id.current_longitude_view)
        current_longitude?.let { current_longitude_view.text = it.toString() }

        val stationName_view = findViewById<TextView>(R.id.stationName_view)
        stationName_view.text = stationName

        val xCoordinate_view = findViewById<TextView>(R.id.xCoordinate_view)
        xCoordinate_view.text = xCoordinate

        val yCoordinate_view = findViewById<TextView>(R.id.yCoordinate_view)
        yCoordinate_view.text = yCoordinate


    }
}