package com.mirrorx.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorx.theme.MirrorXTheme
import com.mirrorx.ui.screens.MirrorScreen
import com.mirrorx.ui.screens.SettingsScreen
import com.mirrorx.viewmodel.MirrorViewModel

@Composable
fun MirrorXApp(
    viewModel: MirrorViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var settingsOpen by rememberSaveable { mutableStateOf(false) }

    MirrorXTheme(appTheme = uiState.settings.theme) {
        if (settingsOpen) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { settingsOpen = false },
            )
        } else {
            MirrorScreen(
                viewModel = viewModel,
                onOpenSettings = { settingsOpen = true },
            )
        }
    }
}
