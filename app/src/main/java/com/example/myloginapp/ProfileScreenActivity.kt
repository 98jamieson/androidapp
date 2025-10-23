package com.example.myloginapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myloginapp.databinding.ActivityProfileScreenBinding

class ProfileScreenActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityProfileScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            ProfileScreen()
        }




    }


    @Composable
    fun ProfileScreen() {

        val context = LocalContext.current

        // Intercept system back button
        BackHandler {
            val intent = Intent(context, MainScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }



        // Variables for text fields
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }



        // Main layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000)), // Bright green background
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title
                Text(
                    text = "Perfil",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 24.dp)
                )

                // Profile image circle with icon
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.user_solid_full),
                        contentDescription = "Calendar",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }

                // Text fields (gray background)
                val fieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = fieldModifier,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.LightGray,
                        focusedContainerColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    modifier = fieldModifier,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.LightGray,
                        focusedContainerColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = fieldModifier,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.LightGray,
                        focusedContainerColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = fieldModifier,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.LightGray,
                        focusedContainerColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = fieldModifier,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.LightGray,
                        focusedContainerColor = Color.LightGray
                    )
                )

                // Save Button
                Button(
                    onClick = { /* TODO: Save profile */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(45.dp)
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Guardar",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }




    @Preview(showBackground = true)
    @Composable
    fun ProfileScreenPreview() {
        ProfileScreen()
    }







}
