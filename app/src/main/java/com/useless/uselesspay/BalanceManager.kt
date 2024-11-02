package com.useless.uselesspay

import android.content.Context
import android.content.SharedPreferences

class BalanceManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    init {
        // Initialize balance of 1 crore (10 million) if not already set
        if (!sharedPreferences.contains(KEY_BALANCE)) {
            sharedPreferences.edit().putLong(KEY_BALANCE, 10000000).apply()
        }
    }

    fun getCurrentBalance(): Long {
        return sharedPreferences.getLong(KEY_BALANCE, 10000000)
    }

    fun deductAmount(amount: Int) {
        val currentBalance = getCurrentBalance()
        val newBalance = currentBalance - amount
        sharedPreferences.edit().putLong(KEY_BALANCE, newBalance).apply()
    }

    companion object {
        private const val PREF_NAME = "BalancePrefs"
        private const val KEY_BALANCE = "current_balance"
        private var instance: BalanceManager? = null

        fun getInstance(context: Context): BalanceManager {
            return instance ?: synchronized(this) {
                instance ?: BalanceManager(context).also { instance = it }
            }
        }
    }
}