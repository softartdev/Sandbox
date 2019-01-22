package com.example.artur.sandbox.ui.bc

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.artur.sandbox.R
import com.example.artur.sandbox.receiver.SandboxReceiver
import kotlinx.android.synthetic.main.activity_bc.*
import timber.log.Timber

class BcActivity : AppCompatActivity() {

    private val sandboxReceiver = SandboxReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bc)

        bc_register_button.setOnClickListener { register() }

        bc_unregister_button.setOnClickListener { unregister() }

        bc_send_button.setOnClickListener {
            val intent = Intent(SandboxReceiver.RECEIVER_ACTION_NAME)
            intent.putExtra(SandboxReceiver.RECEIVER_MESSAGE_NAME, bc_message_edit_text.text.toString())
            sendBroadcast(intent)
        }

        bc_toggle_button.setOnCheckedChangeListener { _, isChecked -> repeatReceiver(isChecked) }
    }

    private fun repeatReceiver(repeat: Boolean) {
//        val intent = Intent(this, SandboxReceiver::class.java)
//        intent.action = SandboxReceiver.RECEIVER_ACTION_NAME
        val intent = Intent(SandboxReceiver.RECEIVER_ACTION_NAME)
        intent.putExtra(SandboxReceiver.RECEIVER_MESSAGE_NAME, bc_message_edit_text.text.toString())

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val flagNoCreatePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE)
        if (repeat && flagNoCreatePendingIntent == null) {
            val flagCancelCurrentPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            val intervalSeconds: Long = bc_interval_edit_text.text.toString().toLong()
            val intervalMillis = intervalSeconds * 1000
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, flagCancelCurrentPendingIntent)
        } else {
            alarmManager.cancel(flagNoCreatePendingIntent)
            flagNoCreatePendingIntent.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
        register()
    }

    override fun onStop() {
        super.onStop()
        try {
            unregister()
        } catch (throwable: Throwable) {
            Timber.e(throwable)
        }
    }

    private fun register() = registerReceiver(sandboxReceiver, IntentFilter(SandboxReceiver.RECEIVER_ACTION_NAME))

    private fun unregister() = unregisterReceiver(sandboxReceiver)
}
