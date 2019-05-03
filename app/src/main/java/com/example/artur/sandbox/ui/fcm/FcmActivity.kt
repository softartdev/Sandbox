package com.example.artur.sandbox.ui.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artur.sandbox.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_fcm.*
import timber.log.Timber

class FcmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fcm)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        intent.extras?.let {
            val info = StringBuilder()
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                val msg = "Key: $key Value: $value"
                info.append(msg)
                info.append("\n")
            }
            information_text_view.text = info.toString()
            Timber.d(information_text_view.text.toString())
        }
        subscribe_button.setOnClickListener {
            Timber.d("Subscribing to sandbox topic")
            FirebaseMessaging.getInstance().subscribeToTopic("sandbox")
                    .addOnCompleteListener { task ->
                        var msg = getString(R.string.msg_subscribed)
                        if (!task.isSuccessful) {
                            msg = getString(R.string.msg_subscribe_failed)
                        }
                        Timber.d(msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    }
        }
        log_token_button.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.w(task.exception, "getInstanceId failed")
                            return@OnCompleteListener
                        }
                        val token = task.result?.token
                        val msg = getString(R.string.msg_token_fmt, token)
                        Timber.d(msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    })
        }
    }
}
