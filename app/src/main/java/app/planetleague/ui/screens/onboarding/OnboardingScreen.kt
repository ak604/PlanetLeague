package app.planetleague.ui.screens.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.planetleague.navigation.Screen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.security.SecureRandom
import app.planetleague.utils.Constants.INITIAL_PLT_BALANCE

class EthereumWallet(
    val address: String,
    val privateKey: String
)

@Composable
fun OnboardingScreen(navController: NavController) {
    var showWalletSetup by remember { mutableStateOf(false) }
    var walletAddress by remember { mutableStateOf("") }
    var walletBalance by remember { mutableStateOf(100) } // Default PLT balance
    val context = LocalContext.current
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
    var isConnectingMetaMask by remember { mutableStateOf(false) }
    
    // Configure Google Sign In
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
    
    // Activity result launcher for Google Sign-In
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            
            // Save user information
            val editor = sharedPrefs.edit()
            editor.putBoolean("has_signed_up", true)
            editor.putInt("plt_balance", INITIAL_PLT_BALANCE)
            account?.email?.let { editor.putString("user_email", it) }
            account?.displayName?.let { editor.putString("user_name", it) }
            account?.id?.let { editor.putString("user_id", it) }
            editor.apply()
            
            Toast.makeText(context, "Signed in as ${account?.displayName}", Toast.LENGTH_SHORT).show()
            
            // Generate Ethereum wallet for Google users
            coroutineScope.launch {
                try {
                    val wallet = withContext(Dispatchers.IO) {
                        createEthereumWallet("google_${account?.id ?: "user"}")
                    }
                    // Save wallet info
                    editor.putString("eth_address", wallet.address)
                    editor.putString("eth_private_key", wallet.privateKey)
                    editor.apply()
                    
                    walletAddress = wallet.address
            showWalletSetup = true
                } catch (e: Exception) {
                    signInError = "Failed to create wallet: ${e.message}"
                    Log.e("EthereumWallet", "Error creating wallet", e)
                }
            }
            
            signInError = null
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                10 -> "Developer Error (10): SHA1 fingerprint or package name mismatch"
                else -> "Sign-in failed: ${e.statusCode}"
            }
            signInError = errorMessage
            Log.e("GoogleSignIn", "Error: Code ${e.statusCode}, Message: ${e.message}")
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
    
    // Activity result launcher for MetaMask
    val metaMaskLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle MetaMask return
        isConnectingMetaMask = false
        
        // In a real app, you'd need to handle the result properly
        // For this demo, we'll just simulate a successful connection
        val address = "0x" + (1..40).map { "0123456789ABCDEF".random() }.joinToString("")
        
        // Save wallet info
        val editor = sharedPrefs.edit()
        editor.putBoolean("has_signed_up", true)
        editor.putInt("plt_balance", INITIAL_PLT_BALANCE)
        editor.putString("eth_address", address)
        editor.putString("wallet_type", "metamask")
        editor.apply()
        
        walletAddress = address
        showWalletSetup = true
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { 
                    // Launch MetaMask connection flow
                    connectToMetaMask(context, metaMaskLauncher)
                    isConnectingMetaMask = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9E0D)
                )
            ) {
                Text("Connect with MetaMask")
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
                        // Create a new wallet without sign-in
                        coroutineScope.launch {
                            try {
                                val wallet = withContext(Dispatchers.IO) {
                                    createEthereumWallet("guest_user")
                                }
                                
                                // Save wallet info
                                val editor = sharedPrefs.edit()
                                editor.putBoolean("has_signed_up", true)
                                editor.putInt("plt_balance", INITIAL_PLT_BALANCE)
                                editor.putString("eth_address", wallet.address)
                                editor.putString("eth_private_key", wallet.privateKey)
                                editor.apply()
                                
                                walletAddress = wallet.address
                        showWalletSetup = true
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to create wallet: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("EthereumWallet", "Error creating wallet", e)
                            }
                        }
                    },
                modifier = Modifier.fillMaxWidth()
            ) {
                    Text("Continue without Sign-In")
                }
            }
            
            // Show MetaMask connecting dialog
            if (isConnectingMetaMask) {
                AlertDialog(
                    onDismissRequest = { isConnectingMetaMask = false },
                    title = { Text("Connecting to MetaMask") },
                    text = { Text("Please open the MetaMask app to complete the connection.") },
                    confirmButton = {
                        TextButton(onClick = { isConnectingMetaMask = false }) {
                            Text("Cancel")
                        }
                    }
                )
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
            
            // Show wallet address
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ethereum Address:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = walletAddress.take(10) + "..." + walletAddress.takeLast(8),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Initial Balance: $INITIAL_PLT_BALANCE PLT",
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

private fun connectToMetaMask(context: Context, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    try {
        // Try to open MetaMask app first
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://metamask.app.link/dapp/planetleague.app/connect")
            putExtra("from", "Planet League")
        }
        
        launcher.launch(intent)
    } catch (e: Exception) {
        // If we can't open the app, try to open the website
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://metamask.io/download/"))
            context.startActivity(webIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to connect to MetaMask: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

private suspend fun createEthereumWallet(password: String): EthereumWallet {
    return withContext(Dispatchers.IO) {
        try {
            // Generate a new private key
            val privateKey = ByteArray(32)
            SecureRandom().nextBytes(privateKey)
            
            // Convert to ECKeyPair
            val keyPair = ECKeyPair.create(privateKey)
            
            // Get the address
            val credentials = Credentials.create(keyPair)
            val address = credentials.address
            
            // Return the wallet
            EthereumWallet(
                address = address,
                privateKey = keyPair.privateKey.toString(16) // Hex format
            )
        } catch (e: Exception) {
            Log.e("EthereumWallet", "Error creating wallet", e)
            throw e
        }
    }
} 