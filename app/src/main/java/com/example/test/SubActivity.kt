package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity

class SubActivity:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
    }
}