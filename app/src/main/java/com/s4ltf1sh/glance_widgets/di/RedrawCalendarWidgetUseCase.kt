package com.s4ltf1sh.glance_widgets.di

import android.content.Context
import android.util.Log
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidgetWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedrawCalendarWidgetUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val widgetRepository: WidgetModelRepository
) {
    companion object {
        private const val TAG = "RedrawCalendarUseCase"
    }
    
    /**
     * Execute calendar widget redraw for all calendar widgets
     */
    fun execute(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Starting calendar widgets redraw process")
                
                // Get all calendar widgets from database
                val calendarWidgets = widgetRepository.getAllWidgets().filter { it.type is WidgetType.Calendar }

                Log.d(TAG, "Found ${calendarWidgets.size} calendar widgets to update")
                
                if (calendarWidgets.isEmpty()) {
                    Log.d(TAG, "No calendar widgets found, skipping update")
                    return@launch
                }

                // Update each calendar widget
                calendarWidgets.forEach { widget ->
                    try {
                        Log.d(TAG, "Updating calendar widget ID: ${widget.widgetId}")

                        // Enqueue update for this specific calendar widget
                        CalendarWidgetWorker.enqueueUpdate(
                            context = context,
                            widgetId = widget.widgetId
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating calendar widget ${widget.widgetId}", e)
                    }
                }
                
                Log.d(TAG, "Calendar widgets redraw process completed")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error in calendar widgets redraw process", e)
            }
        }
    }
}