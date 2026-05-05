package com.app.crowns.vently

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VentlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}