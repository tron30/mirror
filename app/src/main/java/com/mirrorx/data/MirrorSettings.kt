package com.mirrorx.data

enum class AppTheme {
    System,
    Light,
    Dark,
}

enum class BrightnessLevel(val label: String, val windowBrightness: Float) {
    Normal("Normal", -1f),
    Medium("Medium", 0.72f),
    High("High", 1f),
}

data class MirrorSettings(
    val defaultZoomLevel: Float = 1f,
    val autoHideControlsMillis: Long = 2_000L,
    val startBrightnessBoost: Boolean = false,
    val startMirrored: Boolean = true,
    val theme: AppTheme = AppTheme.System,
)
