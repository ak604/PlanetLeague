package app.planetleague.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import app.planetleague.ui.screens.splash.SplashScreen
import app.planetleague.ui.screens.onboarding.OnboardingScreen
import app.planetleague.ui.screens.MainLayout
import app.planetleague.ui.screens.gamehub.GameHubScreen
import app.planetleague.ui.screens.profile.ProfileScreen
import app.planetleague.ui.screens.liveops.LiveOpsScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object GameHub : Screen("gamehub")
    object Profile : Screen("profile")
    object LiveOps : Screen("liveops")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Track if we're at the root of our app for back handling
    var isAtRoot by rememberSaveable { mutableStateOf(false) }

    // Handle back press at root level screens (exit app rather than navigating)
    BackHandler(enabled = isAtRoot) {
        // Let system handle back press to exit the app
        return@BackHandler
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
            isAtRoot = false
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
            isAtRoot = false
        }
        composable(Screen.Main.route) {
            // Once we reach main, we're at the root of our regular navigation
            isAtRoot = true
            
            // Use a shared view model for all tabs to avoid recreating screen state
            MainLayout(
                navController = navController,
                showGameHub = { GameHubScreen(navController) },
                showProfile = { ProfileScreen(navController) },
                showLiveOps = { LiveOpsScreen(navController) }
            )
        }
        
        // We'll keep these routes for deep linking purposes, but they won't be part of normal navigation
        composable(Screen.GameHub.route) {
            MainLayout(
                navController = navController,
                showGameHub = { GameHubScreen(navController) },
                showProfile = { ProfileScreen(navController) },
                showLiveOps = { LiveOpsScreen(navController) }
            )
        }
        composable(Screen.Profile.route) {
            MainLayout(
                navController = navController,
                showGameHub = { GameHubScreen(navController) },
                showProfile = { ProfileScreen(navController) },
                showLiveOps = { LiveOpsScreen(navController) }
            )
        }
        composable(Screen.LiveOps.route) {
            MainLayout(
                navController = navController,
                showGameHub = { GameHubScreen(navController) },
                showProfile = { ProfileScreen(navController) },
                showLiveOps = { LiveOpsScreen(navController) }
            )
        }
    }
} 