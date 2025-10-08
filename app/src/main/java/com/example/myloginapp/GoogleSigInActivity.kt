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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.example.myloginapp.MainScreenActivity
import com.google.android.gms.common.api.ApiException

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
            GoogleSignInButton(onClick = { signInWithGoogle() })

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
    fun GoogleSignInButton(onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // margin from edges
        ){
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
}

