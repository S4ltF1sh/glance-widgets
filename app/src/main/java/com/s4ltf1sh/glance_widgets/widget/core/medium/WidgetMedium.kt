package com.s4ltf1sh.glance_widgets.widget.core.medium

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.glance.GlanceId
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.WidgetEmpty
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidget
import com.s4ltf1sh.glance_widgets.widget.widget.photo.PhotoWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.QuotesWidget
import com.s4ltf1sh.glance_widgets.widget.widget.weather.WeatherWidget

class WidgetMedium : BaseAppWidget() {
    override val widgetSize = WidgetSize.MEDIUM

    @Composable
    override fun ContentSuccess(
        widget: WidgetEntity,
        widgetId: Int
    ) {
        when (widget.type) {
            WidgetType.WEATHER -> WeatherWidget(widget, widgetId)
            WidgetType.CALENDAR -> CalendarWidget(widget, widgetId)
            WidgetType.PHOTO -> PhotoWidget(widget, widgetId)
            WidgetType.QUOTES -> QuotesWidget(widget, widgetId)
            else -> WidgetEmpty(widget, widgetId)
        }
    }

    override fun workerEnqueue(
        context: Context,
        size: DpSize,
        glanceId: GlanceId
    ) {
        TODO("Not yet implemented")
    }

    override fun workerCancel(
        context: Context,
        glanceId: GlanceId
    ) {
        TODO("Not yet implemented")
    }
}