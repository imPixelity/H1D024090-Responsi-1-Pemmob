package com.pemmob.orbit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.pemmob.orbit.R

@Composable
fun OrbitBackground(
    modifier: Modifier = Modifier,
    imageAlpha: Float = 1f,
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val scrimTop = if (darkTheme) {
        Color.Black.copy(alpha = 0.55f)
    } else {
        Color.White.copy(alpha = 0.45f)
    }
    val scrimBottom = if (darkTheme) {
        Color.Black.copy(alpha = 0.75f)
    } else {
        Color.White.copy(alpha = 0.65f)
    }
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = imageAlpha
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = listOf(scrimTop, scrimBottom))
                )
        )
        content()
    }
}

@Composable
fun orbitTopBarBrush(): Brush {
    val scheme = MaterialTheme.colorScheme
    return Brush.horizontalGradient(
        colors = listOf(
            scheme.primaryContainer,
            scheme.tertiaryContainer
        )
    )
}
