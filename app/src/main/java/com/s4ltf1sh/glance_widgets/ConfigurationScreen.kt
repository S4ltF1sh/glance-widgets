package com.s4ltf1sh.glance_widgets

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
import com.s4ltf1sh.glance_widgets.widget.model.WidgetType

@Composable
internal fun ConfigurationScreen(
    widgetId: Int,
    currentType: WidgetType,
    onTypeSelected: (WidgetType) -> Unit
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

        items(WidgetType.entries.filter { it != WidgetType.NONE }) { type ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeSelected(type) },
                colors = CardDefaults.cardColors(
                    containerColor = if (type == currentType)
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
    }
}

private fun getWidgetIcon(type: WidgetType): String {
    return when (type) {
        WidgetType.WEATHER -> "🌤️"
        WidgetType.CALENDAR -> "📅"
        WidgetType.PHOTO -> "🖼️"
        WidgetType.QUOTES -> "💭"
        else -> "Siu"
    }
}

private fun getWidgetTitle(type: WidgetType): String {
    return when (type) {
        WidgetType.WEATHER -> "Weather"
        WidgetType.CALENDAR -> "Calendar"
        WidgetType.PHOTO -> "Photo"
        WidgetType.QUOTES -> "Quotes"
        else -> "None"
    }
}

private fun getWidgetDescription(type: WidgetType): String {
    return when (type) {
        WidgetType.WEATHER -> "Current weather conditions"
        WidgetType.CALENDAR -> "Today's events and schedule"
        WidgetType.PHOTO -> "Daily photo gallery"
        WidgetType.QUOTES -> "Inspirational quotes"
        else -> "None"
    }
}