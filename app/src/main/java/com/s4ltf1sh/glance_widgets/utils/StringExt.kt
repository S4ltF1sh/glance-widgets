package com.s4ltf1sh.glance_widgets.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun String.toColor() = Color(this.toColorInt())