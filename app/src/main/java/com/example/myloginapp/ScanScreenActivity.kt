package com.example.myloginapp

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScanScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // setContentView(R.layout.activity_scan_screen)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
            setContent {

                Text("Main Screen after Log in",
                    Modifier.padding(80.dp),
                    fontSize = 35.sp,   // Medium size (you can adjust to 16.sp, 18.sp, etc.)
                    color= Color.Black,
                    style = MaterialTheme.typography.bodyMedium, // predefined medium text style

                )




                // ---- States for composable parameters ----
                var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
                var classifiedText by remember { mutableStateOf("") }
                var resultText by remember { mutableStateOf("") }
                var confidenceText by remember { mutableStateOf("") }

                // ---- Call your composable ----
                MainScreen(
                    onTakePictureClick = {
                        // 👇 handle button action (e.g. launch camera or update values)
                        resultText = "Cat"
                        classifiedText = "Classified as: Cat"
                        confidenceText = "Confidence: 95%"
                        // Example: load local image (replace with actual photo result)
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.snoopy)
                        imageBitmap = bitmap.asImageBitmap()
                    },
                    imageBitmap = imageBitmap,
                    classifiedText = classifiedText,
                    resultText = resultText,
                    confidenceText = confidenceText
                )
            }


















    }


    @Composable
    fun MainScreen(
        onTakePictureClick: () -> Unit,
        imageBitmap: ImageBitmap?, // or rememberImagePainter if using Coil
        classifiedText: String,
        resultText: String,
        confidenceText: String
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // ImageView equivalent
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .size(370.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(370.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No image", fontSize = 18.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // "Classified as:"
                Text(
                    text = "Classified as:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Result text
                Text(
                    text = resultText,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC30000)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // "Confidences:"
                Text(
                    text = "Confidences:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Confidence details
                Text(
                    text = confidenceText,
                    fontSize = 22.sp,
                    color = Color.Black
                )
            }

            // Button aligned at bottom
            Button(
                onClick = onTakePictureClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "Take Picture",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }



}