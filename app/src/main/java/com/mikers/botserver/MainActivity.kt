package com.mikers.botserver

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var broadcastReceiver: MainActivity.DataBroadcastReceiver
    private lateinit var outputTextView: TextView
    private lateinit var textView: TextView
    private lateinit var enableButton: Button

    private companion object {
        const val ACTION_DATA = "com.mikers.botserver.DATA"
        val filters = arrayOf(ACTION_DATA)
        val intentFilter: IntentFilter by lazy {
            IntentFilter().apply {
                filters.forEach { addAction(it) }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        broadcastReceiver = DataBroadcastReceiver()
        applicationContext.registerReceiver(broadcastReceiver, intentFilter)

        textView = findViewById(R.id.textView)
        enableButton = findViewById(R.id.enableButton)
        outputTextView = findViewById(R.id.outputTextView)

        enableButton.setOnClickListener {
            val intent = Intent(this, BackgroundService::class.java)
            if (!isMyServiceRunning(BackgroundService::class.java)) {
                setEnabled()
                intent.action = ServiceState.ACTION_START
            } else {
                setDisabled()
                intent.action = ServiceState.ACTION_STOP
            }
            startService(intent)
        }

        if (isMyServiceRunning(BackgroundService::class.java)) {
            setEnabled()
        } else {
            setDisabled()
        }
    }

    private fun setEnabled() {
        textView.text = "Service running"
        enableButton.text = "Stop"
    }

    private fun setDisabled() {
        textView.text = "Service stopped"
        enableButton.text = "Start"
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    private fun showData(intent: Intent) {
        outputTextView.text = "${intent.getStringExtra("text")}\n${outputTextView.text}"
    }

    inner class DataBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_DATA -> showData(intent)
            }
        }
    }

}