package com.s4ltf1sh.glance_widgets

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltAndroidApp
class WidgetApplication : Application() {
    companion object {
        const val APP_TAG = "Widget_Sample"
    }

    override fun onCreate() {
        super.onCreate()

        MainScope().launch {
            delay(2000) // Wait for app to settle
        }
    }
}