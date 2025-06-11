package com.s4ltf1sh.glance_widgets.widget

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.widget.model.WidgetSize

@Composable
fun QuoteSelectionScreen(
    widgetId: Int,
    widgetSize: WidgetSize,
    quotes: List<QuoteEntity>,
    onQuoteSelected: (QuoteEntity) -> Unit
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
            text = "Widget Size: ${widgetSize.name}",
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
                columns = GridCells.Adaptive(minSize = getGridCellSize(widgetSize)),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quotes) { quote ->
                    QuoteItem(
                        quote = quote,
                        widgetSize = widgetSize,
                        onClick = { onQuoteSelected(quote) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteItem(
    quote: QuoteEntity,
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
            // If using resource name
            if (!quote.imageResourceName.isNullOrEmpty()) {
                val context = LocalContext.current
                val resourceId = context.resources.getIdentifier(
                    quote.imageResourceName,
                    "drawable",
                    context.packageName
                )
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Quote: ${quote.setName}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // If using URL
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(quote.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Quote: ${quote.setName}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Set name overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = quote.setName,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
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