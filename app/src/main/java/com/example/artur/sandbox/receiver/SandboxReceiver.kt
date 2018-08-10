package com.example.artur.sandbox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import timber.log.Timber

class SandboxReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(RECEIVER_MESSAGE_NAME)
        Timber.d("Receive message: %s", message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val RECEIVER_ACTION_NAME = "com.example.action.SANDBOX"
        const val RECEIVER_MESSAGE_NAME = "receiver_message_name"
    }
}
