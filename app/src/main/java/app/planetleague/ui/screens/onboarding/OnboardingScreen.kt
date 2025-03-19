package app.planetleague.ui.screens.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.planetleague.navigation.Screen
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun OnboardingScreen(navController: NavController) {
    var showWalletSetup by remember { mutableStateOf(false) }
    var walletBalance by remember { mutableStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Check if user has already signed up
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val hasSignedUp = remember { sharedPrefs.getBoolean("has_signed_up", false) }
    
    // Skip onboarding if user has already signed up
    LaunchedEffect(hasSignedUp) {
        if (hasSignedUp) {
            navController.navigate(Screen.GameHub.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }
    
    var signInError by remember { mutableStateOf<String?>(null) }
    
    // Configure Google Sign In with better error handling
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("722533014078-ph3ud0bil0q2rj509lcaqq718v5skkls.apps.googleusercontent.com")
            .build()
    }
    
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    
    // Check if user is already signed in with Google
    LaunchedEffect(Unit) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            // User is already signed in
            sharedPrefs.edit().putBoolean("has_signed_up", true).apply()
            navController.navigate(Screen.GameHub.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }
    
    // Activity result launcher for Google Sign-In with enhanced logging
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            
            // Save user information if needed
            val editor = sharedPrefs.edit()
            editor.putBoolean("has_signed_up", true)
            account?.email?.let { editor.putString("user_email", it) }
            account?.displayName?.let { editor.putString("user_name", it) }
            account?.id?.let { editor.putString("user_id", it) }
            editor.apply()
            
            Toast.makeText(context, "Signed in as ${account?.displayName}", Toast.LENGTH_SHORT).show()
            showWalletSetup = true
            signInError = null
        } catch (e: ApiException) {
            // Enhanced error reporting
            val errorMessage = when (e.statusCode) {
                10 -> "Developer Error (10): SHA1 fingerprint or package name mismatch"
                else -> "Sign-in failed: ${e.statusCode}"
            }
            signInError = errorMessage
            Log.e("GoogleSignIn", "Error: Code ${e.statusCode}, Message: ${e.message}")
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!showWalletSetup && !hasSignedUp) {
            Text(
                text = "Welcome to Planet League",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Choose your sign-up method",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    try {
                        val signInIntent = googleSignInClient.signInIntent
                        signInLauncher.launch(signInIntent)
                    } catch (e: Exception) {
                        signInError = "Error initiating sign-in: ${e.message}"
                        Log.e("GoogleSignIn", "Init error: ${e.message}", e)
                        Toast.makeText(context, signInError, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up with Google")
            }
            
            // Show error message if any
            signInError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // Add fallback button when there's an error
            if (signInError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { 
                        // Skip authentication and proceed
                        sharedPrefs.edit().putBoolean("has_signed_up", true).apply()
                        showWalletSetup = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue without Google Sign-In")
                }
            }
            
        } else if (showWalletSetup) {
            Text(
                text = "Wallet Setup",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Your wallet has been created!",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Initial Balance: 100 PLT",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    navController.navigate(Screen.GameHub.route) 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Playing")
            }
        }
    }
} 