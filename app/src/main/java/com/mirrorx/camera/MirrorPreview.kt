package com.mirrorx.camera

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun MirrorPreview(
    mirrored: Boolean,
    zoomRatio: Float,
    modifier: Modifier = Modifier,
    onPreviewViewReady: (PreviewView) -> Unit,
    onCameraReady: (Float) -> Unit,
    onCameraError: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            scaleType = PreviewView.ScaleType.FILL_CENTER
            keepScreenOn = true
            scaleX = if (mirrored) -1f else 1f
        }
    }
    val cameraManager = remember(context) {
        CameraManager(context.applicationContext)
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView },
        update = { view ->
            view.scaleX = if (mirrored) -1f else 1f
            onPreviewViewReady(view)
        },
    )

    DisposableEffect(cameraManager, lifecycleOwner, previewView) {
        cameraManager.bindFrontCamera(
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            initialZoomRatio = zoomRatio,
            onCameraReady = onCameraReady,
            onCameraError = onCameraError,
        )

        onDispose {
            cameraManager.unbind()
        }
    }

    LaunchedEffect(cameraManager, zoomRatio) {
        cameraManager.setZoomRatio(zoomRatio)
    }
}
