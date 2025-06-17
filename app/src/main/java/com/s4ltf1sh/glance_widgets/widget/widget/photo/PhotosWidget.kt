package com.s4ltf1sh.glance_widgets.widget.widget.photo

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.model.Widget
import com.s4ltf1sh.glance_widgets.model.photo.WidgetPhotoData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@SuppressLint("RestrictedApi")
@Composable
fun PhotoWidget(widget: Widget, widgetId: Int) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.Magenta)
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
            val photosData =
                moshi.adapter(WidgetPhotoData::class.java).fromJson(widget.data)
                    ?: throw Exception("Invalid quote data")
            val index = photosData.index
            val currentPhotoPath = photosData.photoPaths[if (index == -1) 0 else index]

            // Try to load from resources first
            Image(
                provider = getImageProvider(currentPhotoPath),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier
                    .fillMaxSize()
            )
        } else {
            // Show empty state
            PhotoEmptyState()
        }
    }
}

@Composable
private fun PhotoEmptyState() {
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