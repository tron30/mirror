package com.mirrorx.viewmodel

import android.graphics.Bitmap
import com.mirrorx.data.BrightnessLevel
import com.mirrorx.data.MirrorSettings

data class MirrorUiState(
    val settings: MirrorSettings = MirrorSettings(),
    val controlsVisible: Boolean = true,
    val frozenFrame: Bitmap? = null,
    val zoomRatio: Float = 1f,
    val maxZoomRatio: Float = 4f,
    val mirrored: Boolean = true,
    val brightnessLevel: BrightnessLevel = BrightnessLevel.Normal,
    val cameraError: String? = null,
    val cameraReady: Boolean = false,
    val isHalfScreen: Boolean = false,
) {
    val isFrozen: Boolean = frozenFrame != null
    val effectiveMaxZoom: Float = maxZoomRatio.coerceIn(1f, 4f)
}
