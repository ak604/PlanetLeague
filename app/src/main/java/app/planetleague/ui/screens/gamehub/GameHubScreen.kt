package app.planetleague.ui.screens.gamehub

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import app.planetleague.navigation.Screen
import androidx.compose.material3.ExperimentalMaterial3Api

data class Game(
    val name: String,
    val description: String,
    val reward: Int,
    val gameAssetPath: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHubScreen(navController: NavController) {
    var showRewardDialog by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    
    val games = listOf(
        Game(
            "Puzzle Master", 
            "Solve challenging puzzles", 
            10,
            "file:///android_asset/puzzle_master.html"
        ),
        Game(
            "Trivia Quest", 
            "Test your knowledge", 
            10,
            "file:///android_asset/trivia_quest.html"
        ),
        Game(
            "Endless Runner", 
            "Run and collect coins", 
            10,
            "file:///android_asset/endless_runner.html"
        ),
        Game(
            "Memory Match", 
            "Match pairs of cards", 
            10,
            "file:///android_asset/memory_match.html"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Hub") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                    IconButton(onClick = { navController.navigate(Screen.LiveOps.route) }) {
                        Icon(Icons.Default.Star, contentDescription = "Live Ops")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Available Games",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(games) { game ->
                    GameCard(game = game) {
                        selectedGame = game
                        showGameDialog = true
                    }
                }
            }
        }
        
        // Game Dialog - Shows the local HTML5 game in WebView
        if (showGameDialog && selectedGame != null) {
            Dialog(
                onDismissRequest = { 
                    showGameDialog = false
                    // Show reward after closing game
                    showRewardDialog = true
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                ) {
                    // HTML5 Game WebView loading local file
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                webViewClient = WebViewClient()
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    setSupportZoom(false)
                                }
                                loadUrl(selectedGame!!.gameAssetPath)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Close button overlay
                    IconButton(
                        onClick = { 
                            showGameDialog = false 
                            showRewardDialog = true
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(48.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Game",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Reward Dialog
        if (showRewardDialog && selectedGame != null) {
            AlertDialog(
                onDismissRequest = { showRewardDialog = false },
                title = { Text("Congratulations!") },
                text = { Text("You earned ${selectedGame!!.reward} PLT by playing ${selectedGame!!.name}!") },
                confirmButton = {
                    TextButton(onClick = { showRewardDialog = false }) {
                        Text("Claim Reward")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = game.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reward: ${game.reward} PLT",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 