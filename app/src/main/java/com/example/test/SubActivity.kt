package com.example.test

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.ArrayList
import java.util.Locale

class SubActivity : ComponentActivity() {

    private val LOCATION_PERMISSION_REQUEST = 1
    private val RECORD_AUDIO_PERMISSION_REQUEST = 1
    private lateinit var locationManager: LocationManager
    private lateinit var resultText: TextView
    private lateinit var textToSpeech: TextToSpeech
    private var selectedBusNumber: String = ""
    private var currentLocation: Location? = null
    private var isFirstRun = true

    // 위치 정보를 업데이트할 때 사용되는 리스너
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            currentLocation = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        val editText = findViewById<EditText>(R.id.sub_2)
        val button = findViewById<Button>(R.id.sub_1)
        val speakImage = findViewById<ImageButton>(R.id.speak_image)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        resultText = findViewById<EditText>(R.id.sub_2)
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale.getDefault()
                textToSpeech.language = locale
            } else {
                // TTS 초기화 실패
                // 필요에 따라 처리
            }
        }

        // 음성 인식 버튼 클릭 시 처리
        speakImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST)
            } else {
                //textToSpeech.speak("음성 인식을 시작하겠습니다.", TextToSpeech.QUEUE_FLUSH, null, null)
                startListeningWithDelay()
            }
        }

        // 정류소 검색 버튼 클릭 시 처리
        button.setOnClickListener {
            val inputText = editText.text.toString()
            selectedBusNumber = inputText

            requestLocationAndSendData()
        }

        // 위치 권한이 있는지 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없다면 위치 접근 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            // 권한이 있다면 위치 업데이트 시작
            startLocationUpdates()
        }
    }

    // 음성 인식 시작 전 딜레이를 주는 메소드
    private fun startListeningWithDelay() {
        val handler = Handler(Looper.getMainLooper())

        val initialDelay: Long = 500 // 0.5초
        val secondDelay: Long = 2500 // 2.5초
        val finalDelay: Long = 800 // 0.8초
        // 처음 시작시 isFirstRun == true (위에 'private var isFirstRun = true' 이렇게 선언되어 있음)
        if (isFirstRun) {
            handler.postDelayed({
                textToSpeech.speak("음성 인식을 시작하겠습니다.", TextToSpeech.QUEUE_FLUSH, null, null)
            }, initialDelay) // 0.5초 후 실행
            isFirstRun = false
            handler.postDelayed({
                textToSpeech.speak("시작", TextToSpeech.QUEUE_FLUSH, null, null)
            }, initialDelay + secondDelay) // 3초 후 실행

            handler.postDelayed({
                startListening()
            }, initialDelay + secondDelay + finalDelay) // 3.8초 후 실행
        }
        else{
            handler.postDelayed({
                textToSpeech.speak("시작", TextToSpeech.QUEUE_FLUSH, null, null)
            }, initialDelay) // 0.5초 후 실행

            handler.postDelayed({
                startListening()
            }, initialDelay + finalDelay) // 1.3초 후 실행
        }
    }

    // 음성 인식 시작 메소드
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

    // 음성 인식 결과 처리를 위한 ActivityResultLauncher
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            val result: ArrayList<String> = it.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
            resultText.text = result[0]
            speakText(result[0])
        }
    }

    // TTS로 텍스트 읽어주는 메소드
    private fun speakText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // 위치 정보 요청 및 서버에 데이터 전송 메소드
    private fun requestLocationAndSendData() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한이 있는 경우
            val locationListener = object : LocationListener {

                override fun onLocationChanged(location: Location) {
                    // 위치가 변경되면 서버에 데이터 전송
                    val xCoordinate = location.latitude
                    val yCoordinate = location.longitude
                    sendDataToServer(xCoordinate, yCoordinate)
                    locationManager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            // 위치 업데이트 요청
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else {
            // 위치 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    // 서버에 위치 데이터 전송 메소드
    private fun sendDataToServer(xCoordinate: Double, yCoordinate: Double) {
        val client = OkHttpClient()
        var serverUrl = "http://3.37.242.54:3000/getAPI"
        serverUrl += "?busNumber=$selectedBusNumber&xCoordinate=$yCoordinate&yCoordinate=$xCoordinate" //좌표 설정 잘못됨 그냥 x좌표랑 y좌표 바꿈

        val request = Request.Builder()
            .url(serverUrl)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 네트워크 오류 처리
                val errorMessage = e.message

                if (errorMessage != null) {
                    if (errorMessage.contains("Timeout", ignoreCase = true)) {
                        Log.e("SubActivity", "네트워크 타임아웃 오류")
                    } else if (errorMessage.contains("Connection refused", ignoreCase = true)) {
                        Log.e("SubActivity", "서버 연결 거부 오류")
                    } else {
                        Log.e("SubActivity", "알 수 없는 네트워크 오류: $errorMessage")
                    }
                } else {
                    Log.e("SubActivity", "알 수 없는 네트워크 오류 발생")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 서버 응답이 성공인 경우
                    val responseData = response.body?.string()
                    Log.d("responseData", "$responseData")

                    // JSON 응답 처리
                    processJsonResponse(responseData)
                    // SubActivity2로 이동
                    val intent = Intent(this@SubActivity, SubActivity2::class.java)
                    intent.putExtra("responseData", responseData)
                    intent.putExtra("busNumber", selectedBusNumber)
                    intent.putExtra("currentLocation", currentLocation)
                    startActivity(intent)
                } else {
                    // 서버 응답이 실패인 경우
                    Log.e("SubActivity", "실패")
                }
            }
        })
    }

    // JSON 응답 처리 메소드
    private fun processJsonResponse(responseData: String?) {
        val stationArray = parseJson(responseData)

        for (i in 0 until stationArray.length()) {
            val station = stationArray.getJSONObject(i)
            val stationId = station.getString("stationId")
            val stationName = station.getString("stationName")
            val distance = station.getString("distance")
            val x = station.getString("x")
            val y = station.getString("y")

            Log.d("Station", "정류소 ID: $stationId")
            Log.d("Station", "정류소 이름: $stationName")
            Log.d("Station", "거리: $distance")
            Log.d("Station", "X 좌표: $x")
            Log.d("Station", "Y 좌표: $y")
        }
    }

    // JSON 파싱 메소드
    private fun parseJson(jsonString: String?): JSONArray {
        try {
            return JSONArray(jsonString)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return JSONArray()
    }

    // 위치 권한 요청 결과 처리 메소드
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 위치 업데이트 시작
                startLocationUpdates()
            } else {
                // 권한이 거부된 경우
                Log.e("Permission", "Denied")
            }
        }
    }

    // 위치 업데이트 시작 메소드
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 없다면 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }
        // 위치 업데이트 요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, locationListener)
    }
}