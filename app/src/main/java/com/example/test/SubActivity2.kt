package com.example.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SubActivity2:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub2)

        // 입력값 가져오기
        val responseData = intent.getStringExtra("responseData")
        val busNumber = intent.getStringExtra("busNumber")
        val currentLocation = intent.getStringExtra("currentLocation")
        val stationList = parseJsonResponse(responseData)

        Log.e("확인용", currentLocation.toString())
        val listView = findViewById<ListView>(R.id.listView)
        val adapter = ListAdapter(this, stationList)
        listView.adapter = adapter

        // 텍스트뷰에 입력값 설정
        val Bus_number_view = findViewById<TextView>(R.id.Bus_number_view)
        Bus_number_view.text = busNumber

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedStation = stationList[position]
            val xCoordinate = selectedStation.getString("x")
            val yCoordinate = selectedStation.getString("y")
            val stationName = selectedStation.getString("stationName")

            val intent = Intent(this@SubActivity2, SubActivity3::class.java)
            intent.putExtra("xCoordinate", xCoordinate)
            intent.putExtra("yCoordinate", yCoordinate)
            intent.putExtra("stationName", stationName)
            intent.putExtra("currentLocation", currentLocation)
            startActivity(intent)
        }
    }
    private fun parseJsonResponse(responseData: String?): ArrayList<JSONObject> {
        val stationList = ArrayList<JSONObject>()
        try {
            val jsonArray = JSONArray(responseData)
            for (i in 0 until jsonArray.length()) {
                val station = jsonArray.getJSONObject(i)
                val stationName = station.getString("stationName")
                val distance = station.getString("distance")
                val x = station.getString("x")
                val y = station.getString("y")

                val stationObject = JSONObject()
                stationObject.put("stationName", stationName)
                stationObject.put("distance", distance)
                stationObject.put("x", x)
                stationObject.put("y", y)

                stationList.add(stationObject)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return stationList
    }
}