package com.useless.uselesspay

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class BalanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)

        val balanceManager = BalanceManager.getInstance(this)
        val currentBalance = balanceManager.getCurrentBalance()

        findViewById<TextView>(R.id.balanceTextView).text = "₹${String.format("%,d", currentBalance)}"
    }
}