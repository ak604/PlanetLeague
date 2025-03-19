package app.planetleague.utils

import android.content.Context

object PreferencesHelper {
    private const val PREFS_NAME = "app_prefs"
    
    fun getPLTBalance(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt("plt_balance", Constants.INITIAL_PLT_BALANCE)
    }

    fun updatePLTBalance(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt("plt_balance", Constants.INITIAL_PLT_BALANCE)
        prefs.edit().putInt("plt_balance", current + amount).apply()
    }
    
    fun getEarnedPLT(context: Context): Int {
        val totalBalance = getPLTBalance(context)
        return totalBalance - Constants.INITIAL_PLT_BALANCE
    }
} 