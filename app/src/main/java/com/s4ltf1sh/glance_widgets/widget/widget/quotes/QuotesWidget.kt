package com.s4ltf1sh.glance_widgets.widget.widget.quotes

import android.graphics.Bitmap
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
import com.s4ltf1sh.glance_widgets.model.quotes.WidgetQuoteData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import androidx.core.graphics.createBitmap
import com.s4ltf1sh.glance_widgets.model.Widget
import androidx.core.graphics.scale

@Composable
fun QuotesWidget(
    widget: Widget,
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
                        BaseAppWidget.KEY_WIDGET_TYPE to widget.type.typeId,
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

internal fun getImageProvider(path: String): ImageProvider {
    val options = BitmapFactory.Options().apply {
        // Use RGB_565 instead of ARGB_8888 to reduce memory by 50%
        inPreferredConfig = Bitmap.Config.RGB_565

        // First pass: get image dimensions
        inJustDecodeBounds = true
    }

    BitmapFactory.decodeFile(path, options)

    // Calculate sample size to reduce image size
    val maxWidgetSize = 400 // max widget dimension in pixels
    options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxWidgetSize)

    // Second pass: load actual bitmap
    options.inJustDecodeBounds = false

    val bitmap = BitmapFactory.decodeFile(path, options) ?: run {
        // Fallback: create small placeholder if image fails to load
        createBitmap(100, 100, Bitmap.Config.RGB_565).apply {
            eraseColor(0xFFCCCCCC.toInt())
        }
    }

    // Further optimize if still too large (>2MB)
    val optimizedBitmap = if (bitmap.byteCount > 2 * 1024 * 1024) {
        val scaleFactor = kotlin.math.sqrt(2_000_000.0 / bitmap.byteCount)
        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()

        bitmap.scale(newWidth, newHeight).also {
            if (it != bitmap) bitmap.recycle()
        }
    } else {
        bitmap
    }

    return ImageProvider(optimizedBitmap)
}

internal fun getBitmapFromPath(path: String): Bitmap {
    val options = BitmapFactory.Options().apply {
        // Use RGB_565 instead of ARGB_8888 to reduce memory by 50%
        inPreferredConfig = Bitmap.Config.RGB_565

        // First pass: get image dimensions
        inJustDecodeBounds = true
    }

    BitmapFactory.decodeFile(path, options)

    // Calculate sample size to reduce image size
    val maxWidgetSize = 1024 // max widget dimension in pixels
    options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxWidgetSize)

    // Second pass: load actual bitmap
    options.inJustDecodeBounds = false

    val bitmap = BitmapFactory.decodeFile(path, options) ?: run {
        // Fallback: create small placeholder if image fails to load
        createBitmap(100, 100, Bitmap.Config.RGB_565).apply {
            eraseColor(0xFFCCCCCC.toInt())
        }
    }

    // Further optimize if still too large (>2MB)
    val optimizedBitmap = if (bitmap.byteCount > 2 * 1024 * 1024) {
        val scaleFactor = kotlin.math.sqrt(2_000_000.0 / bitmap.byteCount)
        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()

        bitmap.scale(newWidth, newHeight).also {
            if (it != bitmap) bitmap.recycle()
        }
    } else {
        bitmap
    }

    return optimizedBitmap
}

private fun calculateSampleSize(width: Int, height: Int, maxSize: Int): Int {
    var sampleSize = 1
    if (width > maxSize || height > maxSize) {
        val halfWidth = width / 2
        val halfHeight = height / 2

        while ((halfWidth / sampleSize) >= maxSize || (halfHeight / sampleSize) >= maxSize) {
            sampleSize *= 2
        }
    }
    return sampleSize
}