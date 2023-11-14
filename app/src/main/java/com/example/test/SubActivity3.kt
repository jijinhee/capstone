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
        val currentLocation = intent.getStringExtra("currentLocation")

//        Log.e("확인용", xCoordinate.toString())
//        Log.e("확인용", yCoordinate.toString())
//        Log.e("확인용", stationName.toString())
//        Log.e("확인용", currentLocation.toString())
    }
}