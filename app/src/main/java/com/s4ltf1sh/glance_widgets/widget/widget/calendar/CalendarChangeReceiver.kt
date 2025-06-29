package com.s4ltf1sh.glance_widgets.widget.widget.calendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.s4ltf1sh.glance_widgets.di.RedrawCalendarWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CalendarChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var redrawCalendarWidgetUseCase: RedrawCalendarWidgetUseCase

    companion object {
        private const val TAG = "CalendarChangeReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")
        
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED -> {
                Log.d(TAG, "Date changed - updating calendar widgets")
                redrawCalendarWidgetUseCase.execute(context)
            }
            
            Intent.ACTION_LOCALE_CHANGED -> {
                Log.d(TAG, "Locale changed - updating calendar widgets")
                redrawCalendarWidgetUseCase.execute(context)
            }
            
            Intent.ACTION_TIME_CHANGED -> {
                Log.d(TAG, "Time changed - updating calendar widgets")
                redrawCalendarWidgetUseCase.execute(context)
            }
            
            Intent.ACTION_TIMEZONE_CHANGED -> {
                Log.d(TAG, "Timezone changed - updating calendar widgets")
                redrawCalendarWidgetUseCase.execute(context)
            }
            
            Intent.ACTION_PROVIDER_CHANGED -> {
                // Calendar provider changed (calendar events updated)
                val data = intent.data
                if (data?.host == "com.android.calendar") {
                    Log.d(TAG, "Calendar provider changed - updating calendar widgets")
                    redrawCalendarWidgetUseCase.execute(context)
                }
            }
            
            else -> {
                Log.d(TAG, "Unhandled action: ${intent.action}")
            }
        }
    }
}