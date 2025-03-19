package app.planetleague.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.planetleague.ui.screens.splash.SplashScreen
import app.planetleague.ui.screens.onboarding.OnboardingScreen
import app.planetleague.ui.screens.MainLayout
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

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Main.route) {
            LaunchedEffect(Unit) {
                navController.navigate(Screen.GameHub.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
            MainLayout(navController)
        }
        composable(Screen.GameHub.route) {
            MainLayout(navController)
        }
        composable(Screen.Profile.route) {
            MainLayout(navController)
        }
        composable(Screen.LiveOps.route) {
            MainLayout(navController)
        }
    }
} 