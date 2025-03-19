package app.planetleague.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

data class GameHistoryEntry(
    val gameName: String,
    val score: Int,
    val pltEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

object GameHistoryManager {
    private const val PREFS_NAME = "app_prefs"
    private const val HISTORY_KEY = "game_history"
    private const val GAMES_PLAYED_KEY = "games_played_count"
    private val gson = Gson()
    
    fun addGameToHistory(context: Context, entry: GameHistoryEntry) {
        try {
            val history = getGameHistory(context).toMutableList()
            history.add(0, entry) // Add new entry at the beginning (most recent first)
            
            // Limit history to most recent 50 games
            val limitedHistory = if (history.size > 50) history.take(50) else history
            
            // Save updated history
            val json = gson.toJson(limitedHistory)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(HISTORY_KEY, json)
                .apply()
            
            // Increment games played count
            incrementGamesPlayed(context)
            
        } catch (e: Exception) {
            Log.e("GameHistoryManager", "Error saving game history", e)
        }
    }
    
    fun getGameHistory(context: Context): List<GameHistoryEntry> {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
            
            val type = object : TypeToken<List<GameHistoryEntry>>() {}.type
            return gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e("GameHistoryManager", "Error retrieving game history", e)
            return emptyList()
        }
    }
    
    private fun incrementGamesPlayed(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(GAMES_PLAYED_KEY, 0)
        prefs.edit().putInt(GAMES_PLAYED_KEY, current + 1).apply()
    }
    
    fun getGamesPlayedCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(GAMES_PLAYED_KEY, 0)
    }
} 