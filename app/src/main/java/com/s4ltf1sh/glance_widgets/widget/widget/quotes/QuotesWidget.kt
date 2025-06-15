package com.s4ltf1sh.glance_widgets.widget.widget.quotes

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.model.quotes.WidgetQuoteData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun QuotesWidget(
    widget: WidgetEntity,
    widgetId: Int
) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    /*
    * Fucking glance can't load images from URLs directly,
    * We need to start an Worker to load images from URLs
    * then use URI or Bitmap to display them.
    * Follow this issue for more details: https://stackoverflow.com/questions/74361073/how-to-load-images-from-the-internet-into-a-widget-with-jetpack-glance
    * */
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        BaseAppWidget.KEY_WIDGET_ID to widgetId,
                        BaseAppWidget.KEY_WIDGET_TYPE to widget.type.name,
                        BaseAppWidget.KEY_WIDGET_SIZE to widget.size.name
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (widget.data.isNotEmpty()) {
            val quoteData =
                moshi.adapter(WidgetQuoteData::class.java).fromJson(widget.data)
                    ?: throw Exception("Invalid quote data")

            // Try to load from resources first
            Image(
                provider = getImageProvider(quoteData.imagePath),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier
                    .fillMaxSize()
            )
        } else {
            // Show empty state
            QuoteEmptyState()
        }
    }
}

@Composable
private fun QuoteEmptyState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(_root_ide_package_.androidx.compose.ui.graphics.Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        androidx.glance.text.Text(
            text = "Tap to select a quote",
            style = androidx.glance.text.TextStyle()
        )
    }
}

@Composable
private fun QuoteErrorState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(_root_ide_package_.androidx.compose.ui.graphics.Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        androidx.glance.text.Text(
            text = "Unable to load quote",
            style = androidx.glance.text.TextStyle(
            )
        )
    }
}

private fun getImageProvider(path: String): ImageProvider {
    val bitmap = BitmapFactory.decodeFile(path)
    return ImageProvider(bitmap)
}