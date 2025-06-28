package com.s4ltf1sh.glance_widgets.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType

@Composable
internal fun ConfigurationScreen(
    widgetId: Int,
    currentType: GlanceWidgetType,
    onTypeSelected: (GlanceWidgetType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Select Widget Type",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(GlanceWidgetType.getAllMainTypes().filter { it != GlanceWidgetType.None }) { type ->
            WidgetTypeItem(
                type = type,
                onTypeSelected = { onTypeSelected(type) },
                selected = { currentType == type }
            )
        }
    }
}

@Composable
private fun WidgetTypeItem(
    type: GlanceWidgetType,
    onTypeSelected: () -> Unit,
    selected: () -> Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTypeSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected())
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getWidgetIcon(type),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column {
                Text(
                    text = getWidgetTitle(type),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = getWidgetDescription(type),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getWidgetIcon(type: GlanceWidgetType): String {
    return when (type) {
        is GlanceWidgetType.Weather -> "🌤️"
        is GlanceWidgetType.Calendar -> "📅"
        is GlanceWidgetType.Clock -> "⏰"
        GlanceWidgetType.Photo -> "🖼️"
        GlanceWidgetType.Quote -> "💭"
        else -> "Siu"
    }
}

private fun getWidgetTitle(type: GlanceWidgetType): String {
    return when (type) {
        is GlanceWidgetType.Weather -> "Weather"
        is GlanceWidgetType.Calendar -> "Calendar"
        GlanceWidgetType.Clock.Digital.Type1Glance -> "Clock - Digital Type 1"
        GlanceWidgetType.Clock.Digital.Type2Glance -> "Clock - Digital Type 2"
        GlanceWidgetType.Clock.Analog.Type1Glance -> "Clock - Analog Type 2"
        GlanceWidgetType.Clock.Analog.Type2Glance -> "Clock - Analog Type 2"
        GlanceWidgetType.Photo -> "Photo"
        GlanceWidgetType.Quote -> "Quotes"
        else -> "None"
    }
}

private fun getWidgetDescription(type: GlanceWidgetType): String {
    return when (type) {
        is GlanceWidgetType.Weather -> "Current weather conditions"
        is GlanceWidgetType.Calendar -> "Today's events and schedule"
        is GlanceWidgetType.Clock -> "Current time and date"
        GlanceWidgetType.Photo -> "Daily photo gallery"
        GlanceWidgetType.Quote -> "Inspirational quotes"
        else -> "None"
    }
}