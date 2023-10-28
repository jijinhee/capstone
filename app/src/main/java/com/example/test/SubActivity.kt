package com.example.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class SubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub) // activity_sub.xml에 정의된 레이아웃을 설정

        val button = findViewById<Button>(R.id.sub_1)

        button.setOnClickListener {
            val intent = Intent(this, Bus_station::class.java)
            startActivity(intent)
        }
    }
}