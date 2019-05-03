package com.example.artur.sandbox

import android.app.Application

import timber.log.Timber

class SandboxApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
