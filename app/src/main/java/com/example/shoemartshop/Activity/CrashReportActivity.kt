package com.example.shoemartshop.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoemartshop.R

class CrashReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_report)

        val errorMessage = intent.getStringExtra("error_message") ?: "Unknown Error"
        val stackTrace = intent.getStringExtra("stack_trace") ?: "No trace available"

        val txtStackTrace = findViewById<TextView>(R.id.txtStackTrace)
        val btnCopyTrace = findViewById<Button>(R.id.btnCopyTrace)
        val btnRestartApp = findViewById<Button>(R.id.btnRestartApp)

        txtStackTrace.text = "Error: $errorMessage\n\n$stackTrace"

        btnCopyTrace.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ShoeMart Crash Trace", txtStackTrace.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Technical details copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        btnRestartApp.setOnClickListener {
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
