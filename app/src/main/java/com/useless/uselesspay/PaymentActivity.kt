package com.useless.uselesspay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URLDecoder
import java.util.regex.Pattern

class PaymentActivity : AppCompatActivity() {
    private var randomAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Get the scanned QR code data from the intent
        val qrData = intent.getStringExtra("upi_id") ?: ""

        // Extract UPI ID and name from the QR code data
        val upiId = extractParameter(qrData, "pa") ?: "Unknown"
        val name = extractParameter(qrData, "pn")?.let { URLDecoder.decode(it, "UTF-8") } ?: "Unknown"

        // Display extracted details
        findViewById<TextView>(R.id.recipientName).text = name
        findViewById<TextView>(R.id.recipientUPI).text = upiId

        // Generate random amount between ₹1 and ₹10000
        randomAmount = (1..100000).random()
        findViewById<TextView>(R.id.amountTextView).text = "₹$randomAmount"


        findViewById<Button>(R.id.payButton).setOnClickListener {
            // Deduct the amount from balance
            val balanceManager = BalanceManager.getInstance(this)
            balanceManager.deductAmount(randomAmount)

            val intent = Intent(this, SuccessActivity::class.java)
            intent.putExtra("amount", randomAmount)
            intent.putExtra("recipient", name)
            startActivity(intent)
        }

    }

    // Helper function to extract parameters from the UPI URL
    private fun extractParameter(data: String, key: String): String? {
        val pattern = Pattern.compile("$key=([^&]+)")
        val matcher = pattern.matcher(data)
        return if (matcher.find()) matcher.group(1) else null
    }
}
