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
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalEntity
import com.s4ltf1sh.glance_widgets.widget.component.WidgetImage
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType

@Composable
fun ClockDigitalSelectionScreen(
    glanceWidgetSize: GlanceWidgetSize,
    clockType: GlanceWidgetType.Clock.Digital,
    clockDigitalBackgrounds: List<GlanceClockDigitalEntity>,
    onClockDigitalSelected: (GlanceClockDigitalEntity) -> Unit
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
            text = "Widget Size: ${glanceWidgetSize.name}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Clock Type: ${if (clockType == GlanceWidgetType.Clock.Digital.Type1Glance) "Day → Month → Time" else "Time → Day, Month"}",
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
                columns = GridCells.Adaptive(minSize = getGridCellSize(glanceWidgetSize)),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(clockDigitalBackgrounds) { clockDigital ->
                    ClockDigitalItem(
                        clockDigital = clockDigital,
                        glanceWidgetSize = glanceWidgetSize,
                        onClick = { onClockDigitalSelected(clockDigital) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockDigitalItem(
    clockDigital: GlanceClockDigitalEntity,
    glanceWidgetSize: GlanceWidgetSize,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(getAspectRatio(glanceWidgetSize))
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
                        is GlanceWidgetType.Clock.Digital.Type1Glance -> "T1"
                        is GlanceWidgetType.Clock.Digital.Type2Glance -> "T2"
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

private fun getGridCellSize(glanceWidgetSize: GlanceWidgetSize): Dp = when (glanceWidgetSize) {
    GlanceWidgetSize.SMALL -> 120.dp
    GlanceWidgetSize.MEDIUM -> 160.dp
    GlanceWidgetSize.LARGE -> 200.dp
}

private fun getAspectRatio(glanceWidgetSize: GlanceWidgetSize): Float = when (glanceWidgetSize) {
    GlanceWidgetSize.SMALL -> 1f
    GlanceWidgetSize.MEDIUM -> 2f
    GlanceWidgetSize.LARGE -> 1f
}