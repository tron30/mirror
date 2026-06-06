package com.mirrorx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessHigh
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
import com.mirrorx.data.BrightnessLevel
import kotlin.math.roundToInt

@Composable
fun BrightnessControl(
    brightnessLevel: BrightnessLevel,
    onBrightnessChange: (BrightnessLevel) -> Unit,
    onInteractionStart: () -> Unit,
    onInteractionEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = BrightnessLevel.entries.indexOf(brightnessLevel).coerceAtLeast(0)

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
                    imageVector = Icons.Rounded.BrightnessHigh,
                    contentDescription = null,
                    tint = Color.White,
                )
                Text(
                    text = "Brightness",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = brightnessLevel.label,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Slider(
            value = selectedIndex.toFloat(),
            onValueChange = { rawValue ->
                onInteractionStart()
                val index = rawValue.roundToInt().coerceIn(0, BrightnessLevel.entries.lastIndex)
                onBrightnessChange(BrightnessLevel.entries[index])
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            valueRange = 0f..BrightnessLevel.entries.lastIndex.toFloat(),
            steps = BrightnessLevel.entries.size - 2,
            onValueChangeFinished = onInteractionEnd,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.28f),
            ),
        )
    }
}
