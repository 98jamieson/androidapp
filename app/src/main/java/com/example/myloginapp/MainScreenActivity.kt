package com.example.myloginapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WelcomeScreen()

        }
    }


    @Composable
    fun WelcomeScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF81D709)) // bright green background
                .navigationBarsPadding(),       // 👈 avoids navigation bar area
                contentAlignment = Alignment.Center
        ) {
            // Main Text
            Text(
                text = "Bienvenido",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            // Bottom Navigation Row
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.bars_solid_full),
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                        .clickable{
                            val intent = Intent(this@MainScreenActivity, ProfileScreenActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.clock_rotate_left_solid_full),
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.camera_solid_full),
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)

                        .clickable{
                            val intent = Intent(this@MainScreenActivity, ScanScreenActivity::class.java)
                            startActivity(intent)
                            finish()
                        }


                )
                Icon(
                    painter = painterResource(id = R.drawable.bowl_food_solid_full),
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.calendar_solid_full),
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )

            }
        }
    }









}