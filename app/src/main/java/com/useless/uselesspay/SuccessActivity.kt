package com.useless.uselesspay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val amount = intent.getIntExtra("amount", 0)
        val recipient = intent.getStringExtra("recipient") ?: "Unknown"

        findViewById<TextView>(R.id.amountText).text = "₹$amount"
        findViewById<TextView>(R.id.recipientText).text = "to $recipient"

        findViewById<Button>(R.id.doneButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


    }
}