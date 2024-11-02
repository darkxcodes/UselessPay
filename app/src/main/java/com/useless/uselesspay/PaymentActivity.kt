package com.useless.uselesspay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URLDecoder
import java.util.regex.Pattern
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar

class PaymentActivity : AppCompatActivity() {
    private var amount: Int = 0

    val phrases = arrayOf(
        "\"Panam Potte, Power Varatte.\" ",
        "\"Adich Keri Vaaaa.\" ",
        "\"Paisa Enikk Oru Preshname Alla.\" ",
        "\"Enikk Swanthamayi Rand Reserve Bank Und.\" ",
        "\"Cashinte Kazhappa.\" ",
        "\"Enka Appa Rich Da.\" "
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val payButton = findViewById<Button>(R.id.payButton)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)

        // Get the scanned QR code data from the intent
        val qrData = intent.getStringExtra("upi_id") ?: ""

        // Extract UPI ID and name from the QR code data
        val upiId = extractParameter(qrData, "pa") ?: "Unknown"
        val name = extractParameter(qrData, "pn")?.let { URLDecoder.decode(it, "UTF-8") } ?: "Unknown"

        // Display extracted details
        findViewById<TextView>(R.id.recipientName).text = name
        findViewById<TextView>(R.id.recipientUPI).text = upiId

        // Check if amount is passed from ManualPaymentActivity
        amount = intent.getIntExtra("amount", -1)
        if (amount == -1) {
            // If no amount was passed, generate a random amount
            amount = (1..100000).random()
        }
        findViewById<TextView>(R.id.amountTextView).text = "₹$amount"

        // Set random phrase in notes section
        val randomPhrase = phrases.random()
        findViewById<TextView>(R.id.noteText).text = randomPhrase

        payButton.setOnClickListener {
            // Show loading animation
            loadingProgressBar.visibility = View.VISIBLE

            // Simulate a transaction delay (e.g., 2 seconds)
            Handler(Looper.getMainLooper()).postDelayed({
                // Deduct the amount from balance
                val balanceManager = BalanceManager.getInstance(this)
                balanceManager.deductAmount(amount)

                // Proceed to the SuccessActivity
                val intent = Intent(this, SuccessActivity::class.java)
                intent.putExtra("amount", amount)
                intent.putExtra("recipient", name)
                startActivity(intent)

                // Hide loading animation
                loadingProgressBar.visibility = View.GONE

                // Optionally finish this activity if needed
                finish()
            }, 2000) // Delay in milliseconds (2000ms = 2 seconds)
        }
    }

    // Helper function to extract parameters from the UPI URL
    private fun extractParameter(data: String, key: String): String? {
        val pattern = Pattern.compile("$key=([^&]+)")
        val matcher = pattern.matcher(data)
        return if (matcher.find()) matcher.group(1) else null
    }
}