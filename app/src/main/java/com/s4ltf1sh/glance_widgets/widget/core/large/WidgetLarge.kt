package com.s4ltf1sh.glance_widgets.widget.core.large

import androidx.compose.runtime.Composable
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.WidgetEmpty
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidget
import com.s4ltf1sh.glance_widgets.widget.widget.photo.PhotoWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.QuotesWidget
import com.s4ltf1sh.glance_widgets.widget.widget.weather.WeatherWidget

class WidgetLarge : BaseAppWidget() {
    override val widgetSize = WidgetSize.LARGE

    @Composable
    override fun WidgetContent(
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
}