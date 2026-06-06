package com.mirrorx.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorx.R
import com.mirrorx.data.AppTheme
import com.mirrorx.data.MirrorSettings
import com.mirrorx.viewmodel.MirrorViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: MirrorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Text(
                text = "Settings",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        PrivacyStatement()

        SettingsGroup {
            DefaultZoomSetting(
                settings = settings,
                onChange = viewModel::updateDefaultZoom,
            )
            Spacer(Modifier.height(14.dp))
            AutoHideSetting(
                settings = settings,
                onChange = viewModel::updateAutoHideDuration,
            )
        }

        SettingsGroup {
            ToggleSetting(
                title = "Start with brightness boost",
                checked = settings.startBrightnessBoost,
                onCheckedChange = viewModel::updateStartBrightnessBoost,
            )
            Spacer(Modifier.height(8.dp))
            ToggleSetting(
                title = "Start mirrored",
                checked = settings.startMirrored,
                onCheckedChange = viewModel::updateStartMirrored,
            )
        }

        SettingsGroup {
            Text(
                text = "Theme",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppTheme.entries.forEach { theme ->
                    FilterChip(
                        selected = settings.theme == theme,
                        onClick = { viewModel.updateTheme(theme) },
                        label = { Text(theme.displayName) },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.12f),
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content,
        )
    }
}

@Composable
private fun PrivacyStatement() {
    SettingsGroup {
        Text(
            text = stringResource(R.string.privacy_statement),
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DefaultZoomSetting(
    settings: MirrorSettings,
    onChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Default zoom",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${String.format("%.1f", settings.defaultZoomLevel)}x",
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.labelLarge,
        )
    }
    Slider(
        value = settings.defaultZoomLevel,
        onValueChange = onChange,
        valueRange = 1f..4f,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun AutoHideSetting(
    settings: MirrorSettings,
    onChange: (Long) -> Unit,
) {
    val seconds = (settings.autoHideControlsMillis / 1_000f).roundToInt().coerceIn(1, 5)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Auto-hide controls",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${seconds}s",
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.labelLarge,
        )
    }
    Slider(
        value = seconds.toFloat(),
        onValueChange = { onChange(it.roundToInt().coerceIn(1, 5) * 1_000L) },
        valueRange = 1f..5f,
        steps = 3,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ToggleSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.size(18.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

private val AppTheme.displayName: String
    get() = when (this) {
        AppTheme.System -> "System"
        AppTheme.Light -> "Light"
        AppTheme.Dark -> "Dark"
    }
