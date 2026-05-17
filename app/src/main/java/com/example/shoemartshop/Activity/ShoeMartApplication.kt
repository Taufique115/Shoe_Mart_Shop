package com.example.shoemartshop.Activity

import android.app.Application
import android.content.Intent
import android.os.Process
import java.io.PrintWriter
import java.io.StringWriter

class ShoeMartApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                val stackTrace = sw.toString()

                val intent = Intent(this, CrashReportActivity::class.java).apply {
                    putExtra("error_message", throwable.localizedMessage ?: throwable.message ?: "Critical crash occurred")
                    putExtra("stack_trace", stackTrace)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback to default handler if anything goes wrong in the recovery launcher
                defaultHandler?.uncaughtException(thread, throwable)
            }

            // Immediately kill the crashed process to avoid "App keeps stopping" loop
            Process.killProcess(Process.myPid())
            System.exit(10)
        }
    }
}
