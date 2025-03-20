package app.planetleague.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import app.planetleague.navigation.Screen
import app.planetleague.ui.screens.gamehub.GameHubScreen
import app.planetleague.ui.screens.liveops.LiveOpsScreen
import app.planetleague.ui.screens.profile.ProfileScreen
import app.planetleague.utils.PreferencesHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import app.planetleague.utils.Constants.INITIAL_PLT_BALANCE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    navController: NavController,
    showGameHub: @Composable () -> Unit,
    showProfile: @Composable () -> Unit,
    showLiveOps: @Composable () -> Unit
) {
    // Track selected tab locally instead of using navigation
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    // Remember PLT balance to avoid recalculation on each recomposition
     var pltBalance by remember { mutableStateOf(sharedPrefs.getInt("plt_balance", INITIAL_PLT_BALANCE)) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planet League") },
                actions = {
                    // PLT token balance with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "PLT Tokens",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Use the actual PLT token value from viewModel or state
                        Text(
                            text = "${pltBalance} PLT",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Games, contentDescription = "Games") },
                    label = { Text("Games") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Tournaments") },
                    label = { Text("Tournaments") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(innerPadding)
        ) {
            // Show the appropriate screen based on selected tab
            when (selectedTab) {
                0 -> showGameHub()
                1 -> showProfile()
                2 -> showLiveOps()
            }
        }
    }
}

@Composable
fun GameHubContent(navController: NavController) {
    GameHubScreen(navController)
}

@Composable
fun ProfileContent(navController: NavController) {
    ProfileScreen(navController)
}

@Composable
fun LiveOpsContent(navController: NavController) {
    LiveOpsScreen(navController)
}

private fun getScreenTitle(route: String): String {
    return when (route) {
        Screen.GameHub.route -> "Game Hub"
        Screen.Profile.route -> "Profile"
        Screen.LiveOps.route -> "Live Events"
        else -> "Planet League"
    }
} 