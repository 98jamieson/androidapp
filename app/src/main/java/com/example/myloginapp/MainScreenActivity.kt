package com.example.myloginapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            // 🖼️ Background Image
            Image(
                painter = painterResource(id = R.drawable.mainimage), // your drawable image
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // keeps proportions while filling screen
            )

            // 📝 Main Text
            Text(
                text = "Bienvenido",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

            // 🔽 Bottom Navigation Row
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.user_solid_full),
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            val intent = Intent(context, ProfileScreenActivity::class.java)
                            context.startActivity(intent)
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.house_solid_full),
                    contentDescription = "History",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.camera_solid_full),
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            val intent = Intent(context, ScanScreenActivity::class.java)
                            context.startActivity(intent)
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.bowl_food_solid_full),
                    contentDescription = "Meals",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            val intent = Intent(context, MealScreenActivity::class.java)
                            context.startActivity(intent)
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.location_dot_solid_full),
                    contentDescription = "Location",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=Scandinavia+Gym+Guatemala")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.resolveActivity(context.packageManager)?.let {
                                context.startActivity(mapIntent)
                            } ?: run {
                                context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                            }
                        }
                )
            }
        }
    }








}