package com.example.test

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class SubActivity2:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub2)

        // 입력값 가져오기
        val userInput = intent.getStringExtra("userInput")
        val stationId = intent.getStringExtra("stationId")

        // 텍스트뷰에 입력값 설정
        val Bus_station_view = findViewById<TextView>(R.id.Bus_station_view)
        Bus_station_view.text = stationId
    }
}