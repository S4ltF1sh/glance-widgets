package com.s4ltf1sh.glance_widgets.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalEntity
import com.s4ltf1sh.glance_widgets.widget.component.WidgetImage
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType

@Composable
fun ClockDigitalSelectionScreen(
    widgetSize: WidgetSize,
    clockType: WidgetType.Clock.Digital,
    clockDigitalBackgrounds: List<ClockDigitalEntity>,
    onClockDigitalSelected: (ClockDigitalEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Clock Digital Background",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Widget Size: ${widgetSize.name}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Clock Type: ${if (clockType == WidgetType.Clock.Digital.Type1) "Day → Month → Time" else "Time → Day, Month"}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (clockDigitalBackgrounds.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No backgrounds available for this size and type",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = getGridCellSize(widgetSize)),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(clockDigitalBackgrounds) { clockDigital ->
                    ClockDigitalItem(
                        clockDigital = clockDigital,
                        widgetSize = widgetSize,
                        onClick = { onClockDigitalSelected(clockDigital) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockDigitalItem(
    clockDigital: ClockDigitalEntity,
    widgetSize: WidgetSize,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(getAspectRatio(widgetSize))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            WidgetImage(
                image = clockDigital.backgroundUrl,
                modifier = Modifier.fillMaxSize()
            )

            // Background name overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "BG ${clockDigital.id}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Clock type indicator
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = when (clockDigital.type) {
                        is WidgetType.Clock.Digital.Type1 -> "T1"
                        is WidgetType.Clock.Digital.Type2 -> "T2"
                        else -> "?"
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

private fun getGridCellSize(widgetSize: WidgetSize): Dp = when (widgetSize) {
    WidgetSize.SMALL -> 120.dp
    WidgetSize.MEDIUM -> 160.dp
    WidgetSize.LARGE -> 200.dp
}

private fun getAspectRatio(widgetSize: WidgetSize): Float = when (widgetSize) {
    WidgetSize.SMALL -> 1f
    WidgetSize.MEDIUM -> 2f
    WidgetSize.LARGE -> 1f
}