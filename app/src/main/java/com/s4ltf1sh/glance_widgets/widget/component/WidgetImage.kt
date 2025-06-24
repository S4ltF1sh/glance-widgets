package com.s4ltf1sh.glance_widgets.widget.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Scale
import com.s4ltf1sh.glance_widgets.R

@Composable
fun WidgetImage(context: Context, image: String?, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(image)
            .scale(Scale.FILL)
            .crossfade(true)
            .build(),
        loading = { WidgetImagePlaceholder() },
        error = { WidgetImageError() },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        onError = {
//            Log.e("WidgetImage", "Error loading image: ${it.result.throwable.message}")
        }
    )
}

@Composable
fun WidgetImage(image: String?, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .decoderFactory(SvgDecoder.Factory())
            .data(image)
            .scale(Scale.FILL)
            .crossfade(true)
            .build(),
        loading = { WidgetImagePlaceholder() },
        error = { WidgetImageError() },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        onError = {
//            Log.e("WidgetImage", "Error loading image: ${it.result.throwable.message}")
        }
    )
}

@Composable
private fun WidgetImagePlaceholder() {
    Image(
        imageVector = ImageVector.vectorResource(id = R.drawable.img_place_holder),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1))
    )
}

@Composable
private fun WidgetImageError() {
    Image(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_visibility_off),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1))
    )
}