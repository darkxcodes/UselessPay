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
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class PaymentActivity : AppCompatActivity() {
    private var amount: Int = 0
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var loadingProgressBar: ProgressBar

    val phrases = arrayOf(
        "\"Panam Potte, Power Varatte.\" ",
        "\"Adich Keri Vaaaa.\" ",
        "\"Paisa Enikk Oru Preshname Alla.\" ",
        "\"Enikk Swanthamayi Rand Reserve Bank Und.\" ",
        "\"Cashinte Kazhappa.\" ",
        "\"Enka Appa Rich Da.\" "
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        setupBiometricAuth()

        val payButton = findViewById<Button>(R.id.payButton)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

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
            // Show biometric prompt when pay button is clicked
            showBiometricPrompt()
        }
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Proceed with payment after successful authentication
                    processPayment()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Authentication error: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@PaymentActivity,
                        "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirm Payment")
            .setSubtitle("Please authenticate to complete the payment.")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun processPayment() {
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
            intent.putExtra("recipient", findViewById<TextView>(R.id.recipientName).text.toString())
            startActivity(intent)

            // Hide loading animation
            loadingProgressBar.visibility = View.GONE

            // Finish this activity
            finish()
        }, 2000)
    }

    // Helper function to extract parameters from the UPI URL
    private fun extractParameter(data: String, key: String): String? {
        val pattern = Pattern.compile("$key=([^&]+)")
        val matcher = pattern.matcher(data)
        return if (matcher.find()) matcher.group(1) else null
    }
}