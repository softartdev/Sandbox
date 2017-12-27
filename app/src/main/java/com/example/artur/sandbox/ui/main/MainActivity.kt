package com.example.artur.sandbox.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.artur.sandbox.R
import com.example.artur.sandbox.ui.login.LoginActivity
import com.example.artur.sandbox.ui.map.MapsActivity
import com.example.artur.sandbox.ui.master.ItemListActivity
import com.example.artur.sandbox.ui.settings.SettingsActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        login_button.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        chrome_button.setOnClickListener { chromeCustomTab() }

        items_button.setOnClickListener { startActivity(Intent(this, ItemListActivity::class.java)) }

        maps_button.setOnClickListener { startActivity(Intent(this, MapsActivity::class.java)) }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun chromeCustomTab() {
        val url = "https://google.com/"
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
