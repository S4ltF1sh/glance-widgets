package com.s4ltf1sh.glance_widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.s4ltf1sh.glance_widgets.di.RedrawCalendarWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var redrawCalendarWidgetUseCase: RedrawCalendarWidgetUseCase

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                Log.d(TAG, "Boot/Package update completed - reinitializing calendar widgets")

                // Update all calendar widgets immediately
                redrawCalendarWidgetUseCase.execute(context)
            }
        }
    }
}