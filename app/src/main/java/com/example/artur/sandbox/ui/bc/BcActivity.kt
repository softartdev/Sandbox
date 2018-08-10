package com.example.artur.sandbox.ui.bc

import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
