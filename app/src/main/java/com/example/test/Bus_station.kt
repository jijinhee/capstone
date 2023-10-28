package com.example.test

import androidx.activity.ComponentActivity
import java.io.BufferedReader
import android.os.Bundle
import android.widget.TextView
import java.io.InputStreamReader

class Bus_station : ComponentActivity() {
    //private lateinit var editText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bus_station)

        //editText = findViewById(R.id.locationTextView)

        // 텍스트뷰 찾기
        val Bus_station = findViewById<TextView>(R.id.Bus_station)

        // CSV 파일을 읽어와 원하는 정보를 찾고 TextView에 설정
        try {
            val inputStream = assets.open("bus.csv")
            val br = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (br.readLine().also { line = it } != null) {
                val parts = line?.split(",")
                if (parts != null && parts.size >= 3) {
                    val location = parts[2] // 위치
                    val latitude = parts[3] // 위도
                    val longitude = parts[4] // 경도

                    if (location == "김삿갓교") {
                        val infoText = "장소: $location\n위도: $latitude\n경도: $longitude"
                        Bus_station.text = infoText
                        break
                    }
                }
            }
            br.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}