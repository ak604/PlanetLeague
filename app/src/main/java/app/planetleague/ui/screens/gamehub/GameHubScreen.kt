package app.planetleague.ui.screens.gamehub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.planetleague.navigation.Screen
import androidx.compose.material3.ExperimentalMaterial3Api

data class Game(
    val name: String,
    val description: String,
    val reward: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHubScreen(navController: NavController) {
    var showRewardDialog by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    
    val games = listOf(
        Game("Puzzle Master", "Solve challenging puzzles", 10),
        Game("Trivia Quest", "Test your knowledge", 10),
        Game("Endless Runner", "Run and collect coins", 10),
        Game("Memory Match", "Match pairs of cards", 10)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Hub") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "PLT")
                        Text("100 PLT")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(games) { game ->
                    GameCard(
                        game = game,
                        onClick = {
                            selectedGame = game
                            showRewardDialog = true
                        }
                    )
                }
            }
        }

        if (showRewardDialog && selectedGame != null) {
            AlertDialog(
                onDismissRequest = { showRewardDialog = false },
                title = { Text("Congratulations!") },
                text = { Text("You earned ${selectedGame!!.reward} PLT!") },
                confirmButton = {
                    TextButton(onClick = { showRewardDialog = false }) {
                        Text("Claim Reward")
                    }
                }
            )
        }
    }
}

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