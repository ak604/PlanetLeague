package app.planetleague.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    
    // Remember PLT balance to avoid recalculation on each recomposition
    val pltBalance = remember { 1000 }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        when (selectedTab) {
                            0 -> "Game Hub"
                            1 -> "Profile"
                            2 -> "Tournaments"
                            else -> "Planet League"
                        }
                    ) 
                },
                actions = {
                    // PLT Balance display
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = "PLT",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = pltBalance.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
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