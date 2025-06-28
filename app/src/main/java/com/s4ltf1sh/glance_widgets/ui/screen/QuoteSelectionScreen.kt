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
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteEntity
import com.s4ltf1sh.glance_widgets.widget.component.WidgetImage
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize

@Composable
fun QuoteSelectionScreen(
    glanceWidgetSize: GlanceWidgetSize,
    quotes: List<GlanceQuoteEntity>,
    onQuoteSelected: (GlanceQuoteEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Quote",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Widget Size: ${glanceWidgetSize.name}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (quotes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No quotes available for this size",
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
                items(quotes) { quote ->
                    QuoteItem(
                        quote = quote,
                        glanceWidgetSize = glanceWidgetSize,
                        onClick = { onQuoteSelected(quote) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteItem(
    quote: GlanceQuoteEntity,
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
                image = quote.imageUrl,
                modifier = Modifier.fillMaxSize()
            )

            // Set name overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = quote.id.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
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