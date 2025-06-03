package com.s4ltf1sh.glance_widgets.widget_core.large

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text

class WidgetLarge : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            Content()
        }
    }
}

@Composable
private fun Content() {
    Box(
        modifier = GlanceModifier.fillMaxSize()
            .cornerRadius(12.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Widget Large")
    }
}