package com.example.myloginapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


class MapsScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()           // Use full height
                    .padding(16.dp),         // Add padding for spacing
                contentAlignment = Alignment.BottomCenter  // Align button to bottom
            ) {
                OpenMapButton()
            }

        }
    }
    @Composable
   fun OpenMapButton() {
        val context = LocalContext.current

        Box(

        ){
            Button(onClick = {
                val gmmIntentUri = Uri.parse("geo:0,0?q=Scandinavia+Gym+Guatemala")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.resolveActivity(context.packageManager)?.let {
                    context.startActivity(mapIntent)
                } ?: run {
                    // fallback if Maps app not installed
                    context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                }


            }) {
                Text("Open Scandinavia Gym in Maps")
            }}

        }
}