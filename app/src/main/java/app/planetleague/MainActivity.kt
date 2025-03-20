package app.planetleague

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.addCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.planetleague.ui.theme.PlanetLeagueTheme
import app.planetleague.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle back press at the activity level
        onBackPressedDispatcher.addCallback(this) {
            // Just finish the activity when back is pressed
            // Let the BackHandler in the Compose hierarchy handle navigation
            finish()
        }
        
        setContent {
            PlanetLeagueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
} 