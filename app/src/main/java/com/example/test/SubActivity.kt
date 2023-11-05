package com.example.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapPolyLine
import com.skt.Tmap.TMapView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.net.URLEncoder
import kotlinx.coroutines.*

class SubActivity : ComponentActivity() {
    // 위치 권한 요청 코드
    private val LOCATION_PERMISSION_REQUEST = 1
    private val client = OkHttpClient()
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 안드로이드서 제공하는 위치 정보 서비스 api (ㄱㅈㅅ)
    private lateinit var tMapView: TMapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub) // activity_sub.xml에 정의된 레이아웃을 설정

        // 목적지 좌표 가져오는 코드 필요

        // TMap 뷰 설정
        tMapView = TMapView(this)
        tMapView.setSKTMapApiKey("UR12n8NxMX11hi3IhN9gR9vC4paPTvsn1NvuHL6M")

        val linearLayoutTmap = findViewById<LinearLayout>(R.id.LinearLayout)
        linearLayoutTmap.addView(tMapView)
        //val button = findViewById<Button>(R.id.startButton)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }else{
            // 임의로 설정(변경 필요)
            val end_X = 127.080649
            val end_Y = 37.799686
            val end_Name_String = "도착지"
            val end_Name = URLEncoder.encode(end_Name_String, "UTF-8")

            // 현재 위치 받아오기
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val start_X = 127.071336 //location.latitude
                    val start_Y = 37.810042 //location.longitude

                    Log.e("start_X", "${start_X}")
                    Log.e("start_Y", "${start_Y}")

                    val start_Name_String = "출발지"
                    val start_Name = URLEncoder.encode(start_Name_String, "UTF-8")

                    if(tMapView != null){
                        tMapView.setCenterPoint(start_X, start_Y)
                    }
                    updateRoute(start_X, start_Y, end_X, end_Y, start_Name, end_Name) //Tmap 대중교통 API를 호출하는 함수
                }
            }
        }
    }
    // 대중교통 경로를 업데이트하는 함수
    private fun updateRoute(
        start_X: Double, start_Y: Double,
        end_X: Double, end_Y: Double,
        start_Name: String, end_Name: String
    ) {
        // CoroutineScope 생성
        val scope = CoroutineScope(Dispatchers.Main)
        val startPoint = TMapPoint(start_Y, start_X)
        val endPoint = TMapPoint(end_Y, end_X)

        scope.launch {
            val response = withContext(Dispatchers.IO) {
                // api 호출에 필요한 데이터 설정
                val requestData = RequestData(
                    startX = start_X.toString(),
                    startY = start_Y.toString(),
                    endX = end_X.toString(),
                    endY = end_Y.toString(),
                    startName = start_Name,
                    endName = end_Name
                )
                val json = Gson().toJson(requestData)

                // api 호출을 위한 요청을 설정
                val mediaType = "application/json".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                val request = Request.Builder()
                    .url("https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=function")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("appKey", "UR12n8NxMX11hi3IhN9gR9vC4paPTvsn1NvuHL6M")
                    .build()

                // api 호출 결과를 처리
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body
                if (responseBody != null) {
                    val responseString = responseBody.string()
                    val jsonData = JSONObject(responseString)
                    val features = jsonData.getJSONArray("features")

                    val tMapPolyLine = TMapPolyLine()

                    for (i in 0 until features.length()) {
                        val feature = features.getJSONObject(i)
                        val geometry = feature.getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")

                        // LineString 타입인 경우에만 좌표를 추출
                        if (geometry.getString("type") == "LineString") {
                            for (j in 0 until coordinates.length()) {
                                val coordinate = coordinates.getJSONArray(j)
                                val tMapPoint = TMapPoint(coordinate.getDouble(1), coordinate.getDouble(0))
                                tMapPolyLine.addLinePoint(tMapPoint)
                            }
                        }
                    }
                    // Tmap에 경로를 그림
                    tMapView.addTMapPolyLine("tMapPolyLine", tMapPolyLine)

                    // 출발지와 도착지를 지도 화면에 꽉 차게 보여주도록 지도의 축척을 설정
                    tMapView.zoomToSpan(Math.abs(start_Y - end_Y)*1.5, Math.abs(start_X - end_X))

                    // 출발지와 도착지의 중간 지점을 계산
                    val middlePointX = (start_X + end_X) / 2
                    val middlePointY = (start_Y + end_Y) / 2

                    // 지도의 중심점을 출발지와 도착지의 중간 지점으로 설정
                    tMapView.setCenterPoint(middlePointX, middlePointY)

                    val startMarker = TMapMarkerItem().apply {
                        tMapPoint = startPoint
                        icon = (resources.getDrawable(R.drawable.poi_star, null) as BitmapDrawable).bitmap
                        setPosition(0.5f, 1.0f)
                        // 마커의 라벨 설정
                        calloutTitle = "출발지"
                        // 말풍선(Callout Bubble) 활성화 및 자동 표시 설정
                        canShowCallout = true
                        autoCalloutVisible = true
                    }
                    tMapView.addMarkerItem("startMarker", startMarker)

                    val endMarker = TMapMarkerItem().apply {
                        tMapPoint = endPoint
                        icon = (resources.getDrawable(R.drawable.end, null) as BitmapDrawable).bitmap
                        setPosition(0.5f, 1.0f)
                        // 마커의 라벨 설정
                        calloutTitle = "목적지"
                        // 말풍선(Callout Bubble) 활성화 및 자동 표시 설정
                        canShowCallout = true
                        autoCalloutVisible = true
                    }
                    tMapView.addMarkerItem("endMarker", endMarker)

                    val startNavigationButton = findViewById<Button>(R.id.startButton)
                    startNavigationButton.setOnClickListener {
                        val intent = Intent(this@SubActivity, SubActivity2::class.java)
                        // JSONArray를 ArrayList<String>으로 변환
                        val list = ArrayList<String>()
                        for (i in 0 until features.length()) {
                            list.add(features.getString(i))
                        }
                        intent.putStringArrayListExtra("features", list)
                        startActivity(intent)
                    }

                } else {
                    Log.e("실패", "${response.code}")
                }
            }
        }
    }
    data class RequestData(
        val startX: String,
        val startY: String,
        val endX: String,
        val endY: String,
        val startName: String,
        val endName: String
    )
}