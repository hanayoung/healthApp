package com.example.healthapp

import android.content.Intent
import android.util.Log
import com.example.healthapp.view.main.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DataLayerListenerService: WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent) {
        super.onMessageReceived(event)
        Log.d("hereherehere",event.sourceNodeId)
        when (event.path) {
            START_ACTIVITY_PATH -> {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }

    companion object {
        private const val START_ACTIVITY_PATH = "/start-activity"
    }
}