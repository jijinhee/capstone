import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
class LED_Result : ComponentActivity() {
    // 위치 권한 요청 코드
    private val LOCATION_PERMISSION_REQUEST = 1

    // 사용자가 선택한 버스 번호를 저장할 변수
    private var selectedBusNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 사용자가 버튼을 클릭하여 버스 번호를 선택할 때 호출
        val selectBusButton = findViewById<Button>(R.id.selectBusButton)
        selectBusButton.setOnClickListener {
            // 사용자가 버스 번호를 선택하고 버튼을 눌렀을 때 호출되는 메서드
            onBusNumberSelected("21") // 여기서 "21"은 사용자가 선택한 버스 번호로 대체
        }
    }

    // 사용자가 선택한 버스 번호를 저장하고 위치 정보를 가져오는 메서드
    private fun onBusNumberSelected(busNumber: String) {
        selectedBusNumber = busNumber

        // 위치 권한을 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            // 위치 권한이 이미 승인된 경우 위치 정보 가져오기
            getLocation()
        }
    }

    // 위치 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 위치 권한이 승인되었을 때 위치 정보 가져오기
                    getLocation()
                } else {
                    // 위치 권한이 거부된 경우 사용자에게 설명하거나 다른 조치를 취할 수 있음
                }
            }
        }
    }

    // 위치 정보 가져오는 메서드
    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            // 서버로 위치 정보와 선택된 버스 번호를 보내는 함수 호출
            sendLocationToServer(latitude, longitude, selectedBusNumber)
        } else {
            // 위치 정보를 가져올 수 없는 경우 처리
        }
    }

    // 서버로 위치 정보와 선택된 버스 번호를 보내는 메서드
    private fun sendLocationToServer(latitude: Double, longitude: Double, busNumber: String) {
        val client = OkHttpClient()

        // Express 앱의 URL 설정 (Express 앱이 실행 중인 서버 주소로 대체)
        val expressAppUrl = "http://서버주소:포트번호/getAPI" // 실제 서버 주소로 변경해야 합니다.

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val json = """
        {
            "busNumber": "$busNumber",
            "xCoordinate": $latitude,
            "yCoordinate": $longitude
        }
    """.trimIndent()

        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(expressAppUrl)
            .post(requestBody)
            .build()

        try {
            val response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                // 서버에서 받은 응답을 처리하거나 표시할 수 있습니다.
                if (responseData != null) {
                    println("서버 응답: $responseData")
                }
            } else {
                // 요청 실패 처리
                println("서버 요청 실패")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // 예외 처리
        }
    }
}
