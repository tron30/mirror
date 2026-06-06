package com.mirrorx.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FreezeButton(
    isFrozen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        modifier = modifier.size(64.dp),
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White.copy(alpha = 0.88f),
            contentColor = Color.Black,
        ),
    ) {
        Icon(
            imageVector = if (isFrozen) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
            contentDescription = if (isFrozen) "Unfreeze" else "Freeze",
            modifier = Modifier.size(34.dp),
            tint = MaterialTheme.colorScheme.inverseSurface,
        )
    }
}
