package com.ismaindra.alquranapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("prayer_name") ?: "Sholat"
        NotificationHelper(context).showNotification(
            "Waktunya Sholat",
            "Saatnya menunaikan ibadah $name"
        )
    }
}