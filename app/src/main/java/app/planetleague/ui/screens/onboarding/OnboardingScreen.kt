package app.planetleague.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.planetleague.navigation.Screen

@Composable
fun OnboardingScreen(navController: NavController) {
    var showWalletSetup by remember { mutableStateOf(false) }
    var walletBalance by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!showWalletSetup) {
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
                onClick = { showWalletSetup = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up with Email")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { showWalletSetup = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up with Google")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { showWalletSetup = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect Wallet (MetaMask/Phantom)")
            }
        } else {
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
                onClick = { navController.navigate(Screen.GameHub.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Playing")
            }
        }
    }
} 