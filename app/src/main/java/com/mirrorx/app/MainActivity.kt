package com.mirrorx.app

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorx.data.BrightnessLevel
import com.mirrorx.data.SettingsRepository
import com.mirrorx.viewmodel.MirrorViewModel
import com.mirrorx.viewmodel.MirrorViewModelFactory

import android.os.Build

class MainActivity : ComponentActivity() {
    private val settingsRepository by lazy {
        SettingsRepository(applicationContext)
    }

    private val mirrorViewModel: MirrorViewModel by viewModels {
        MirrorViewModelFactory(settingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()

        setContent {
            val uiState by mirrorViewModel.uiState.collectAsStateWithLifecycle()
            ApplyScreenBrightness(uiState.brightnessLevel)
            MirrorXApp(viewModel = mirrorViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    override fun onDestroy() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Composable
private fun ApplyScreenBrightness(level: BrightnessLevel) {
    val activity = LocalContext.current.findActivity() ?: return

    DisposableEffect(activity, level) {
        val originalBrightness = activity.window.attributes.screenBrightness
        val attributes = activity.window.attributes
        attributes.screenBrightness = level.windowBrightness
        activity.window.attributes = attributes

        onDispose {
            val resetAttributes = activity.window.attributes
            resetAttributes.screenBrightness = originalBrightness
            activity.window.attributes = resetAttributes
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
