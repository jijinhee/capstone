package com.example.test

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.test.ui.theme.TestTheme
import android.content.Intent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }

        val button2 = findViewById<Button>(R.id.button2)

        button2.setOnClickListener {
            val intent = Intent(this, Hacha::class.java)
            startActivity(intent)
        }

//        val button3 = findViewById<Button>(R.id.button3)
//
//        button3.setOnClickListener {
//            val intent = Intent(this, LED::class.java)
//            startActivity(intent)
//        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestTheme {
        Greeting("Android")
    }
}