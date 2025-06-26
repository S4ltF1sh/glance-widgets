package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.utils.toColor
import com.s4ltf1sh.glance_widgets.widget.component.GlanceIcon

@SuppressLint("RestrictedApi")
@Composable
private fun Header(
    modifier: GlanceModifier = GlanceModifier,
    monthAndYearMonth: String,
    textSize: Float,
    textColor: Color,
    iconSize: Dp,
    iconColor: Color,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthAndYearMonth,
            style = TextStyle(
                fontSize = TextUnit(textSize, TextUnitType.Sp),
                color = ColorProvider(textColor)
            )
        )

        GlanceIcon(
            resId = R.drawable.ic_widget_arrow_right,
            tint = "#0A84FF".toColor(),
            size = textSize.dp
        )

        Spacer(
            modifier = GlanceModifier.fillMaxWidth()
        )

        GlanceIcon(
            resId = R.drawable.ic_widget_arrow_left,
            tint = iconColor,
            size = iconSize,
            onClick = onGoToPreviousMonth
        )

        GlanceIcon(
            resId = R.drawable.ic_widget_arrow_right,
            tint = iconColor,
            size = iconSize,
            onClick = onGoToNextMonth
        )
    }
}