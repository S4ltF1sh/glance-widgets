package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.glance.GlanceModifier
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@SuppressLint("RestrictedApi")
@Composable
private fun Header(
    modifier: GlanceModifier = GlanceModifier,
    monthAndYearMonth: String,
    textSize: Float,
    textColor: Int
) {
    Text(
        modifier = modifier,
        text = monthAndYearMonth,
        style = TextStyle(
            fontSize = TextUnit(textSize, TextUnitType.Sp),
            color = ColorProvider(textColor),
            textAlign = TextAlign.Start
        )
    )
}