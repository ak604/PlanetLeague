package app.planetleague.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import app.planetleague.navigation.Screen
import app.planetleague.ui.screens.gamehub.GameHubScreen
import app.planetleague.ui.screens.liveops.LiveOpsScreen
import app.planetleague.ui.screens.profile.ProfileScreen
import app.planetleague.utils.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(navController: NavController) {
    val context = LocalContext.current
    var pltBalance by remember { mutableStateOf(PreferencesHelper.getPLTBalance(context)) }
    
    // Monitor navigation changes to update PLT balance when returning to screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Update PLT balance whenever screen becomes active
    LaunchedEffect(navBackStackEntry) {
        pltBalance = PreferencesHelper.getPLTBalance(context)
    }
    
    // Get current route
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.GameHub.route
    
    val items = listOf(
        Triple(Screen.GameHub.route, "Games", Icons.Default.Gamepad),
        Triple(Screen.Profile.route, "Profile", Icons.Default.Person), 
        Triple(Screen.LiveOps.route, "Events", Icons.Default.EmojiEvents)
    )
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(getScreenTitle(currentRoute)) },
                actions = {
                    // PLT Balance display
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        IconButton(onClick = { /* Optional: Navigate to wallet details */ }) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text("$pltBalance")
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Payments,
                                    contentDescription = "PLT Balance"
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { (route, title, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when navigating back
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Content area
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentRoute) {
                Screen.GameHub.route -> GameHubContent(navController)
                Screen.Profile.route -> ProfileContent(navController)
                Screen.LiveOps.route -> LiveOpsContent(navController)
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