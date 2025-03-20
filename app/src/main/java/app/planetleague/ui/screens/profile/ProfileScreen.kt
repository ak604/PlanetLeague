package app.planetleague.ui.screens.profile

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.planetleague.utils.Constants.INITIAL_PLT_BALANCE
import app.planetleague.utils.GameHistoryEntry
import app.planetleague.utils.GameHistoryManager
import app.planetleague.utils.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NFT(
    val name: String,
    val description: String,
    val imageUrl: String,
    val isEquipped: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var pltBalance by remember { mutableStateOf(sharedPrefs.getInt("plt_balance", INITIAL_PLT_BALANCE)) }
    
    // Get game history and played count
    val gameHistory = remember { mutableStateOf(GameHistoryManager.getGameHistory(context)) }
    val gamesPlayed = remember { mutableStateOf(GameHistoryManager.getGamesPlayedCount(context)) }
    
    // Refresh data when screen is shown
    LaunchedEffect(Unit) {
        pltBalance = sharedPrefs.getInt("plt_balance", INITIAL_PLT_BALANCE)
        gameHistory.value = GameHistoryManager.getGameHistory(context)
        gamesPlayed.value = GameHistoryManager.getGamesPlayedCount(context)
    }

    var selectedTab by remember { mutableStateOf(0) }
    
    val nfts = listOf(
        NFT("Rare Planet", "A unique planet NFT", "placeholder_url", false),
        NFT("Epic Space Ship", "A powerful space ship", "placeholder_url", true),
        NFT("Legendary Character", "A legendary character skin", "placeholder_url", false)
    )

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your PLT Balance", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$pltBalance PLT",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Games Played: ${gamesPlayed.value}")
                        Text("Total Earned: ${pltBalance - INITIAL_PLT_BALANCE} PLT")
                    }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Game History") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("NFTs") }
                )
            }

            when (selectedTab) {
                0 -> GameHistoryList()
                1 -> NFTsList(nfts = nfts)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun GameHistoryList() {
    val context = LocalContext.current
    val gameHistory = remember { mutableStateOf(GameHistoryManager.getGameHistory(context)) }
    
    LaunchedEffect(Unit) {
        gameHistory.value = GameHistoryManager.getGameHistory(context)
    }
    
    if (gameHistory.value.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "No games played yet. Go play some games!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gameHistory.value) { entry ->
                GameHistoryItem(entry)
            }
        }
    }
}

@Composable
fun GameHistoryItem(entry: GameHistoryEntry) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(entry.timestamp) { 
        dateFormat.format(Date(entry.timestamp)) 
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.gameName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Score: ${entry.score}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = "PLT")
                Text("${entry.pltEarned} PLT")
            }
        }
    }
}

@Composable
fun NFTsList(nfts: List<NFT>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(nfts) { nft ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nft.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = nft.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (nft.isEquipped) {
                            OutlinedButton(onClick = { /* Handle unequip */ }) {
                                Text("Unequip")
                            }
                        } else {
                            Button(onClick = { /* Handle equip */ }) {
                                Text("Equip")
                            }
                        }
                        OutlinedButton(onClick = { /* Handle sell */ }) {
                            Text("Sell")
                        }
                    }
                }
            }
        }
    }
} 