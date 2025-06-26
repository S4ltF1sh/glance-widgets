package com.s4ltf1sh.glance_widgets.widget.component

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider

@SuppressLint("RestrictedApi")
@Composable
fun GlanceIcon(
    @DrawableRes resId: Int,
    tint: Color,
    size: Dp,
    contentDescription: String? = null,
    modifier: GlanceModifier = GlanceModifier,
    onClick: (() -> Unit)? = null
) {
    GlanceIcon(
        resId = resId,
        tint = tint,
        width = size,
        height = size,
        contentDescription = contentDescription,
        modifier = modifier,
        onClick = onClick
    )
}

@SuppressLint("RestrictedApi")
@Composable
fun GlanceIcon(
    @DrawableRes resId: Int,
    tint: Color,
    width: Dp,
    height: Dp,
    contentDescription: String? = null,
    modifier: GlanceModifier = GlanceModifier,
    onClick: (() -> Unit)? = null
) {
    Image(
        provider = ImageProvider(resId),
        contentDescription = contentDescription,
        modifier = modifier.size(width, height).clickable {
            onClick?.invoke()
        },
        colorFilter = ColorFilter.tint(ColorProvider(tint))
    )
}