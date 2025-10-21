package com.example.myloginapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.myloginapp.ui.theme.MyLoginAppTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.google.android.gms.common.api.ApiException

import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import com.google.firebase.FirebaseApp




import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
//import com.google.firebase.firestore.ktx.firestore


//import com.google.firebase.ktx.Firebase






class GoogleSigInActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var request: GetCredentialRequest
    private val showErrorDialog = mutableStateOf(false)
    private var errorMessage by mutableStateOf("")

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("GoogleSignIn", "Google sign in failed", e)
                    updateUI(null)
                }
            } else {
                Log.w("GoogleSignIn", "Google sign in canceled or failed")
                updateUI(null)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this) // ✅ Make sure Firebase is initialized


        // Firebase auth
        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)

        // Configure sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
           // .setAutoSelectEnabled(false)            // force showing bottom sheet
            .build()

        request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        setContent {
            LoginScreen { code, password ->
                loginUser(code, password) { success ->
                    if (success) {
                        println("Login success!")
                    } else {
                        println("Invalid credentials")
                    }
                }
            }


            //GoogleSignInButton(onClick = { signInWithGoogle() })

            //modal error
            if (showErrorDialog.value) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog.value = false },
                    title = { Text("Sign-in Error") },
                    text = { Text("Credential is not a valid Google ID.") },
                    confirmButton = {
                        Button(onClick = { showErrorDialog.value = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }

    }

    private fun signInWithGoogle() {

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@GoogleSigInActivity,
                    request = request
                )
                //existing account
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "Couldn't get credentials: ${e.localizedMessage}")
                showErrorDialog.value = true

                // 👇 Detect "no credentials" case and fall back
                if (e is androidx.credentials.exceptions.NoCredentialException) {
                    //not an existing account
                    launchGoogleSignInClient()
                } else {
                    errorMessage = e.localizedMessage ?: "Unknown error"
                    showErrorDialog.value = true
                }
            }
        }
    }

    private fun launchGoogleSignInClient() {

    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
        com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
    )
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)

    val signInIntent = googleSignInClient.signInIntent
    googleSignInLauncher.launch(signInIntent)

    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w("GoogleSignIn", "Credential is not of type Google ID!")
            showErrorDialog.value = true   // 👈 trigger the modal

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Login success: ${auth.currentUser?.email}")
                    val user = auth.currentUser
                    updateUI(user)

                } else {
                    Log.e("FirebaseAuth", "Login failed", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            val intent = Intent(this, MainScreenActivity::class.java)
            startActivity(intent)

        } else {
            // Show sign in screen again

            setContent {
                GoogleSignInButton(onClick = { signInWithGoogle() })
            }
        }
    }



    @Composable
    fun LoginScreen(
        onLoginClick: (String, String) -> Unit
    ) {
        var code by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var loginMessage by remember { mutableStateOf("") }
        var showForgotPasswordDialog by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }



        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.scandinaviaico), // replace with your image
                    contentDescription = "Decorative image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Código", color = Color.White) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.White) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onLoginClick(code, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81D709)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Ingresar",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = loginMessage, color = Color.White)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(55.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Olvide mi contraseña",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Thin)

                        if (showDialog) {
                            ForgotPasswordDialog(
                                onDismiss = { showDialog = false }
                            )
                        }
                }
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 24.dp)
                        .height(55.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Google logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign in with Google", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }



    fun loginUser(code: String, password: String, onResult: (Boolean) -> Unit) {
        val db = Firebase.firestore

        // Query the user-collection where both code and password match
        db.collection("user-collection")
            .whereEqualTo("code", code)
            .whereEqualTo("name", password)
            .get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                if (!documents.isEmpty) {
                    // Match found
                    onResult(true)
                    val intent = Intent(this@GoogleSigInActivity, MainScreenActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // No match found
                    onResult(false)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                onResult(false)
            }
    }


    @Composable
    fun GoogleSignInButton(onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp), contentAlignment = Alignment.Center

        ){

            Column (

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ){

            }

            Button(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.Center) // 👈 puts button at bottom
                    .fillMaxWidth()

            ) {
                Text("Sign in with Google")
            }

            if (showErrorDialog.value) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog.value = false },
                    title = { Text("Sign-in Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = { showErrorDialog.value = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }

    }


    @Composable
    fun ForgotPasswordDialog(onDismiss: () -> Unit) {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Restablecer contraseña") },
            text = {
                Column {
                    Text("Ingresa tu correo electrónico para enviar el enlace de recuperación.")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val auth = FirebaseAuth.getInstance()
                    if (email.isNotEmpty()) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Correo de recuperación enviado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onDismiss()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, ingresa tu correo electrónico.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
            }
        )
    }



}
