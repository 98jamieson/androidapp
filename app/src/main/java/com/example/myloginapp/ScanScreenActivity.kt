package com.example.myloginapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myloginapp.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

class ScanScreenActivity : ComponentActivity() {

    private val imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = this
            var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
            var resultText by remember { mutableStateOf("Resultado") }
            var confidenceText by remember { mutableStateOf("Nivel de coincidencia") }

            // Camera launcher
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val image = data?.extras?.get("data") as? Bitmap
                    image?.let {
                        val resized = Bitmap.createScaledBitmap(it, imageSize, imageSize, false)
                        classifyImage(resized) { result, confidence ->
                            resultText = result
                            confidenceText = confidence
                        }
                        imageBitmap = it
                    }
                }
            }

            // Permission launcher
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(intent)
                }
            }

            // Back navigation to main screen
            BackHandler {
                val intent = Intent(context, MainScreenActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(intent)
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        imageBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Imagen capturada",
                                modifier = Modifier.size(300.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = resultText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = confidenceText, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(onClick = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                cameraLauncher.launch(intent)
                            } else {
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    arrayOf(Manifest.permission.CAMERA),
                                    100
                                )
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }) {
                            Text("Escanear")
                        }
                    }
                }
            )
        }
    }

    private fun classifyImage(image: Bitmap, onResult: (String, String) -> Unit) {
        try {
            val model = Model.newInstance(applicationContext)


            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )


            /*val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.UINT8
            )*/

            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            //val byteBuffer = ByteBuffer.allocateDirect(imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val value = intValues[pixel++]
                    //byteBuffer.put(((value shr 16) and 0xFF).toByte())
                    //byteBuffer.put(((value shr 8) and 0xFF).toByte())
                    //byteBuffer.put((value and 0xFF).toByte())
                    byteBuffer.putFloat(((value shr 16 and 0xFF) / 255.0f))
                    byteBuffer.putFloat(((value shr 8 and 0xFF) / 255.0f))
                    byteBuffer.putFloat(((value and 0xFF) / 255.0f))



                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val confidences = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            val classes = arrayOf("Banana", "Apple")
            val result = classes[maxPos]

            val confidenceStr = buildString {
                for (i in classes.indices) {
                    append("${classes[i]}: ${"%.1f".format(confidences[i] * 100)}%\n")
                }
            }

            onResult(result, confidenceStr)
            model.close()
        } catch (e: IOException) {
            e.printStackTrace()
            onResult("Error", "Model could not be loaded")
        }
    }






}