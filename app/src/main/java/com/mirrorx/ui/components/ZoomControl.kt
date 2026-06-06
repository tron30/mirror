package com.mirrorx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun ZoomControl(
    zoomRatio: Float,
    maxZoomRatio: Float,
    onZoomChange: (Float) -> Unit,
    onInteractionStart: () -> Unit,
    onInteractionEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectiveMax = maxZoomRatio.coerceIn(1f, 4f)
    val sliderMax = max(effectiveMax, 1.01f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ZoomIn,
                    contentDescription = null,
                    tint = Color.White,
                )
                Text(
                    text = "Zoom",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = "${String.format("%.1f", zoomRatio)}x",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Slider(
            value = zoomRatio.coerceIn(1f, sliderMax),
            onValueChange = {
                onInteractionStart()
                onZoomChange(it.coerceIn(1f, effectiveMax))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            enabled = effectiveMax > 1.01f,
            valueRange = 1f..sliderMax,
            onValueChangeFinished = onInteractionEnd,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.28f),
                disabledThumbColor = Color.White.copy(alpha = 0.7f),
                disabledActiveTrackColor = Color.White.copy(alpha = 0.35f),
                disabledInactiveTrackColor = Color.White.copy(alpha = 0.18f),
            ),
        )
    }
}
