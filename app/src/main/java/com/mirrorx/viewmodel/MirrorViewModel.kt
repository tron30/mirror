package com.mirrorx.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mirrorx.data.AppTheme
import com.mirrorx.data.BrightnessLevel
import com.mirrorx.data.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MirrorViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MirrorUiState())
    val uiState: StateFlow<MirrorUiState> = _uiState.asStateFlow()

    private var startupSettingsApplied = false
    private var autoHideJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { current ->
                    val withSettings = current.copy(settings = settings)
                    if (startupSettingsApplied) {
                        withSettings
                    } else {
                        startupSettingsApplied = true
                        withSettings.copy(
                            zoomRatio = settings.defaultZoomLevel.coerceIn(1f, 4f),
                            mirrored = settings.startMirrored,
                            brightnessLevel = if (settings.startBrightnessBoost) {
                                BrightnessLevel.Medium
                            } else {
                                BrightnessLevel.Normal
                            },
                        )
                    }
                }
                scheduleAutoHide()
            }
        }
    }

    fun revealControls() {
        _uiState.update { it.copy(controlsVisible = true) }
        scheduleAutoHide()
    }

    fun holdControls() {
        autoHideJob?.cancel()
        _uiState.update { it.copy(controlsVisible = true) }
    }

    fun hideControlsSoon() {
        scheduleAutoHide()
    }

    fun freezeFrame(bitmap: Bitmap) {
        val oldFrame = _uiState.value.frozenFrame
        if (oldFrame !== bitmap) {
            oldFrame?.safeRecycle()
        }
        _uiState.update {
            it.copy(
                frozenFrame = bitmap,
                controlsVisible = true,
            )
        }
        scheduleAutoHide()
    }

    fun unfreezeFrame() {
        val oldFrame = _uiState.value.frozenFrame
        _uiState.update { it.copy(frozenFrame = null, controlsVisible = true) }
        oldFrame?.safeRecycle()
        scheduleAutoHide()
    }

    fun setZoomRatio(ratio: Float) {
        _uiState.update {
            it.copy(
                zoomRatio = ratio.coerceIn(1f, it.effectiveMaxZoom),
                controlsVisible = true,
            )
        }
    }

    fun onPinchZoom(zoomChange: Float) {
        _uiState.update {
            it.copy(
                zoomRatio = (it.zoomRatio * zoomChange).coerceIn(1f, it.effectiveMaxZoom),
            )
        }
    }

    fun setBrightnessLevel(level: BrightnessLevel) {
        _uiState.update {
            it.copy(
                brightnessLevel = level,
                controlsVisible = true,
            )
        }
        scheduleAutoHide()
    }

    fun toggleMirror() {
        _uiState.update {
            it.copy(
                mirrored = !it.mirrored,
                controlsVisible = true,
            )
        }
        scheduleAutoHide()
    }

    fun toggleHalfScreen() {
        _uiState.update {
            it.copy(
                isHalfScreen = !it.isHalfScreen,
                controlsVisible = true,
            )
        }
        scheduleAutoHide()
    }

    fun onCameraReady(maxZoomRatio: Float) {
        _uiState.update {
            it.copy(
                cameraReady = true,
                cameraError = null,
                maxZoomRatio = maxZoomRatio.coerceAtLeast(1f),
                zoomRatio = it.zoomRatio.coerceIn(1f, maxZoomRatio.coerceIn(1f, 4f)),
            )
        }
    }

    fun onCameraError(message: String) {
        _uiState.update {
            it.copy(
                cameraReady = false,
                cameraError = message,
                controlsVisible = true,
            )
        }
        autoHideJob?.cancel()
    }

    fun updateDefaultZoom(value: Float) {
        viewModelScope.launch {
            settingsRepository.setDefaultZoomLevel(value)
        }
    }

    fun updateAutoHideDuration(milliseconds: Long) {
        viewModelScope.launch {
            settingsRepository.setAutoHideControlsMillis(milliseconds)
        }
    }

    fun updateStartBrightnessBoost(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setStartBrightnessBoost(enabled)
        }
    }

    fun updateStartMirrored(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setStartMirrored(enabled)
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    private fun scheduleAutoHide() {
        autoHideJob?.cancel()
        if (!_uiState.value.controlsVisible || _uiState.value.cameraError != null) return

        autoHideJob = viewModelScope.launch {
            delay(_uiState.value.settings.autoHideControlsMillis)
            _uiState.update { it.copy(controlsVisible = false) }
        }
    }

    override fun onCleared() {
        _uiState.value.frozenFrame?.safeRecycle()
        super.onCleared()
    }

    private fun Bitmap.safeRecycle() {
        if (!isRecycled) recycle()
    }
}

class MirrorViewModelFactory(
    private val settingsRepository: SettingsRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MirrorViewModel::class.java)) {
            return MirrorViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
