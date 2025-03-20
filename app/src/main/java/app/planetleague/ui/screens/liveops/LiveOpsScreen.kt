package app.planetleague.ui.screens.liveops

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class DailyQuest(
    val title: String,
    val description: String,
    val reward: Int,
    val isCompleted: Boolean
)

data class Tournament(
    val name: String,
    val entryFee: Int,
    val prizePool: Int,
    val participants: Int,
    val endDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveOpsScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val dailyQuests = listOf(
        DailyQuest("Play 3 Games", "Complete 3 games to earn rewards", 25, false),
        DailyQuest("Win a Tournament", "Win any tournament to earn rewards", 50, false),
        DailyQuest("Collect 100 PLT", "Earn 100 PLT through gameplay", 25, false)
    )
    
    val tournaments = listOf(
        Tournament("Weekly Championship", 10, 1000, 45, "Ends in 3 days"),
        Tournament("Speed Runner Cup", 5, 500, 32, "Ends in 1 day"),
        Tournament("Puzzle Masters", 15, 1500, 28, "Ends in 5 days")
    )

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Daily Quests") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tournaments") }
                )
            }

            when (selectedTab) {
                0 -> DailyQuestsList(quests = dailyQuests)
                1 -> TournamentsList(tournaments = tournaments)
            }
        }
    }
}

@Composable
fun DailyQuestsList(quests: List<DailyQuest>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quests) { quest ->
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
                            text = quest.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = quest.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "PLT")
                        Text("${quest.reward} PLT")
                        if (quest.isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = "Completed")
                        } else {
                            Button(onClick = { /* Handle quest completion */ }) {
                                Text("Complete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentsList(tournaments: List<Tournament>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tournaments) { tournament ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = tournament.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Entry Fee: ${tournament.entryFee} PLT")
                            Text("Prize Pool: ${tournament.prizePool} PLT")
                            Text("Participants: ${tournament.participants}")
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(tournament.endDate)
                            Button(onClick = { /* Handle tournament entry */ }) {
                                Text("Enter Tournament")
                            }
                        }
                    }
                }
            }
        }
    }
} 