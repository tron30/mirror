package com.mirrorx.camera

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlin.math.min

import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy

class CameraManager(
    private val context: Context,
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    fun bindFrontCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        initialZoomRatio: Float,
        onCameraReady: (maxZoomRatio: Float) -> Unit,
        onCameraError: (message: String) -> Unit,
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener(
            {
                try {
                    val provider = providerFuture.get()
                    cameraProvider = provider

                    val resolutionSelector = ResolutionSelector.Builder()
                        .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                        .build()

                    val preview = Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    provider.unbindAll()
                    camera = provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                    )

                    val maxZoomRatio = camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f
                    setZoomRatio(initialZoomRatio)
                    onCameraReady(maxZoomRatio)
                } catch (exception: Exception) {
                    camera = null
                    onCameraError(
                        exception.message?.takeIf { it.isNotBlank() }
                            ?: "The front camera is unavailable.",
                    )
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    fun setZoomRatio(ratio: Float) {
        val activeCamera = camera ?: return
        val zoomState = activeCamera.cameraInfo.zoomState.value
        val minZoom = zoomState?.minZoomRatio ?: 1f
        val maxZoom = min(zoomState?.maxZoomRatio ?: 1f, 4f).coerceAtLeast(minZoom)
        activeCamera.cameraControl.setZoomRatio(ratio.coerceIn(minZoom, maxZoom))
    }

    fun unbind() {
        cameraProvider?.unbindAll()
        camera = null
    }
}
