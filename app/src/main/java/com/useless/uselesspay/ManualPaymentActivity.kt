package com.useless.uselesspay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ManualPaymentActivity : AppCompatActivity() {
    private var randomAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_payment)

        val upiEditText = findViewById<EditText>(R.id.upiEditText)
        val proceedButton = findViewById<Button>(R.id.proceedButton)

        randomAmount = (1..100000).random()
        findViewById<TextView>(R.id.amountTextView).text = "₹${randomAmount}"

        proceedButton.setOnClickListener {
            val upiId = upiEditText.text.toString()

            if (!isValidUpiId(upiId)) {
                Toast.makeText(this, "Invalid UPI ID format. Please use <name>@<bank>.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (upiId.isEmpty()) {
                Toast.makeText(this, "Please enter UPI ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = upiId.substringBefore('@')
            val paymentData = "upi://pay?pa=$upiId&pn=${Uri.encode(name)}"

            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("upi_id", paymentData)
                putExtra("amount", randomAmount) // Pass the random amount here
            }
            startActivity(intent)
        }
    }

    private fun isValidUpiId(upiId: String): Boolean {
        val regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$".toRegex()
        return regex.matches(upiId)
    }
}
