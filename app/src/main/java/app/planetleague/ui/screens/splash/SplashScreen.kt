package app.planetleague.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.planetleague.R
import app.planetleague.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var progress by remember { mutableStateOf(0f) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (progress > 0f) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (progress > 0f) 1f else 0.5f,
        animationSpec = tween(durationMillis = 500),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        // Animate progress from 0 to 1 over 2 seconds
        val startTime = System.currentTimeMillis()
        val duration = 2000f
        
        while (progress < 1f) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime
            progress = (elapsed / duration).coerceIn(0f, 1f)
            delay(16) // Approximately 60fps
        }
        
        // Wait a moment at 100% before navigating
        delay(300)
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_planet_league_logo),
                contentDescription = "PLG Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Name
            Text(
                text = "Planet League",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress Bar
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                strokeWidth = 3.dp
            )
        }
    }
} 