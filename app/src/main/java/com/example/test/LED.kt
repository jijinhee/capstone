package com.example.test

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity

class LED:  ComponentActivity() {
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.led)

        val button = findViewById<Button>(R.id.sub_2)
        editText = findViewById(R.id.editText)

        button.setOnClickListener {
            val intent = Intent(this, LED_Result::class.java)
            val inputText = editText.text.toString()
            intent.putExtra("userInput", inputText)
            startActivity(intent)
        }
    }
}