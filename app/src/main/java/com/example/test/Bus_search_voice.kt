package com.example.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList
import java.util.Locale

class Bus_search_voice : ComponentActivity() {

    private lateinit var resultText: TextView
    private lateinit var textToSpeech: TextToSpeech
    private val RECORD_AUDIO_PERMISSION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bus_search_voice)

        val speakButton: ImageButton = findViewById(R.id.speak_image)
        resultText = findViewById(R.id.result_text)
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale.getDefault()
                textToSpeech.language = locale
            } else {
                // TTS 초기화 실패
                // 필요에 따라 처리
            }
        }

        speakButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST)
            } else {
                // 권한이 이미 부여되었으면 음성 인식 시작
                startListeningWithDelay()
            }
        }
    }

    private fun startListeningWithDelay() {
        // 0.5초 딜레이 후 음성 인식 시작
        val delayMilliseconds: Long = 500
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            textToSpeech.speak("시작", TextToSpeech.QUEUE_FLUSH, null, null)
            startListening()
        }, delayMilliseconds)
    }

    private fun startListening() {
        val intent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)

        if (intent.resolveActivity(packageManager) != null) {
            activityResult.launch(intent)
        } else {
            Toast.makeText(this, "음성을 텍스트로 변환할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            val result: ArrayList<String> = it.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
            resultText.text = result[0] // 음성을 텍스트로 표시
            speakText(result[0]) // TTS로 읽어줌
        }
    }

    private fun speakText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}