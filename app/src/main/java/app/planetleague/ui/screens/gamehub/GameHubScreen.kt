package app.planetleague.ui.screens.gamehub

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.JavascriptInterface
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
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import app.planetleague.utils.Constants.INITIAL_PLT_BALANCE
import app.planetleague.utils.GameHistoryEntry
import app.planetleague.utils.GameHistoryManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.Extension

data class Game(
    val name: String,
    val description: String,
    val reward: Int,
    val gameAssetPath: String
)

// JavaScript interface to communicate between HTML games and Kotlin
class GameJsInterface(private val onGameCompleted: (Int) -> Unit) {
    @JavascriptInterface
    fun gameCompleted(score: Int) {
        onGameCompleted(score)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHubScreen(navController: NavController) {
    var showRewardDialog by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf(false) }
    var showCloseConfirmation by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var gameScore by remember { mutableStateOf(0) }
    
    val games = listOf(
        Game(
            "Puzzle Master", 
            "Solve puzzles",
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(8.dp)
        ) {
            items(games) { game ->
                GameGridItem(game = game) {
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
                // Show confirmation instead of closing directly
                showCloseConfirmation = true
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
                // HTML5 Game WebView loading local file with JavaScript interface
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
                            
                            // Set up JavaScript interface for game completion
                            addJavascriptInterface(
                                GameJsInterface { score ->
                                    gameScore = score
                                    showGameDialog = false
                                    showRewardDialog = true
                                }, 
                                "AndroidGameInterface"
                            )
                            
                            // Inject JavaScript to add the game completion callback
                            webViewClient = object : WebViewClient() {
                                @SuppressLint("SetJavaScriptEnabled")
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    
                                    // Inject JavaScript to listen for game completion
                                    view?.evaluateJavascript("""
                                        // Watch for game over or game completion
                                        const observer = new MutationObserver(function(mutations) {
                                            mutations.forEach(function(mutation) {
                                                // Check if game over screen is displayed
                                                if (document.getElementById('game-over') && 
                                                    document.getElementById('game-over').style.display === 'flex') {
                                                    // Extract score if available
                                                    let score = 0;
                                                    if (document.getElementById('final-score')) {
                                                        const scoreText = document.getElementById('final-score').innerText;
                                                        const scoreMatch = scoreText.match(/\\d+/);
                                                        if (scoreMatch) score = parseInt(scoreMatch[0]);
                                                    }
                                                    // Send score to Android
                                                    AndroidGameInterface.gameCompleted(score);
                                                }
                                                
                                                // For trivia and other games that use 'result' instead
                                                if (document.getElementById('result') && 
                                                    document.getElementById('result').style.display === 'flex') {
                                                    // Extract score if available
                                                    let score = 0;
                                                    if (document.getElementById('result-text')) {
                                                        const scoreText = document.getElementById('result-text').innerText;
                                                        const scoreMatch = scoreText.match(/\\d+/);
                                                        if (scoreMatch) score = parseInt(scoreMatch[0]);
                                                    }
                                                    // Send score to Android
                                                    AndroidGameInterface.gameCompleted(score);
                                                }
                                            });
                                        });
                                        
                                        // Observe changes to DOM
                                        observer.observe(document.body, { 
                                            attributes: true, 
                                            childList: true, 
                                            subtree: true 
                                        });
                                        
                                        // Add listeners to all play-again buttons
                                        document.addEventListener('click', function(event) {
                                            if (event.target && event.target.id === 'play-again') {
                                                // Hide any game over screens when restarting
                                                if (document.getElementById('game-over')) {
                                                    document.getElementById('game-over').style.display = 'none';
                                                }
                                                if (document.getElementById('result')) {
                                                    document.getElementById('result').style.display = 'none';
                                                }
                                            }
                                        }, true);
                                    """, null)
                                }
                            }
                            
                            loadUrl(selectedGame!!.gameAssetPath)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Close button overlay
                IconButton(
                    onClick = { 
                        // Show confirmation dialog instead of closing directly
                        showCloseConfirmation = true
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
    
    // Close Confirmation Dialog
    if (showCloseConfirmation) {
        AlertDialog(
            onDismissRequest = { showCloseConfirmation = false },
            title = { Text("Close Game?") },
            text = { Text("Are you sure you want to quit? You won't receive any rewards unless you complete the game.") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showCloseConfirmation = false
                        showGameDialog = false
                    }
                ) {
                    Text("Yes, Quit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCloseConfirmation = false }
                ) {
                    Text("Continue Playing")
                }
            }
        )
    }
    
    // Reward Dialog - Only appears when game is completed via JavaScript callback
    if (showRewardDialog && selectedGame != null) {
        val context = LocalContext.current
        val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
        
        AlertDialog(
            onDismissRequest = { showRewardDialog = false },
            title = { Text("Congratulations!") },
            text = { 
                Column {
                    Text("You earned ${selectedGame!!.reward} PLT by playing ${selectedGame!!.name}!")
                    Text("Total PLT: ${sharedPrefs.getInt("plt_balance", INITIAL_PLT_BALANCE)}")
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    // Update PLT balance
                    val currentBalance = sharedPrefs.getInt("plt_balance", INITIAL_PLT_BALANCE)
                    sharedPrefs.edit()
                        .putInt("plt_balance", currentBalance + selectedGame!!.reward)
                        .apply()
                    
                    // Record game history
                    val historyEntry = GameHistoryEntry(
                        gameName = selectedGame!!.name,
                        score = gameScore,
                        pltEarned = selectedGame!!.reward
                    )
                    GameHistoryManager.addGameToHistory(context, historyEntry)
                    
                    showRewardDialog = false
                }) {
                    Text("Claim Reward")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameGridItem(
    game: Game,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Game Icon with Gradient Background
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = when (game.name.hashCode() % 4) {
                        0 -> Icons.Filled.SportsEsports
                        1 -> Icons.Filled.Casino
                        2 -> Icons.Filled.Extension
                        else -> Icons.Filled.VideogameAsset
                    },
                    contentDescription = game.name,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = game.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Reward",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${game.reward} PLT",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
} 