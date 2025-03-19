package app.planetleague.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.planetleague.ui.screens.splash.SplashScreen
import app.planetleague.ui.screens.onboarding.OnboardingScreen
import app.planetleague.ui.screens.gamehub.GameHubScreen
import app.planetleague.ui.screens.profile.ProfileScreen
import app.planetleague.ui.screens.liveops.LiveOpsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object GameHub : Screen("gamehub")
    object Profile : Screen("profile")
    object LiveOps : Screen("liveops")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.GameHub.route) {
            GameHubScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.LiveOps.route) {
            LiveOpsScreen(navController)
        }
    }
} 