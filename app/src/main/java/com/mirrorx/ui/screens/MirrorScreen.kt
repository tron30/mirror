package com.mirrorx.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorx.R
import com.mirrorx.camera.MirrorPreview
import com.mirrorx.ui.components.BrightnessControl
import com.mirrorx.ui.components.FreezeButton
import com.mirrorx.ui.components.ZoomControl
import com.mirrorx.viewmodel.MirrorUiState
import com.mirrorx.viewmodel.MirrorViewModel

@Composable
fun MirrorScreen(
    viewModel: MirrorViewModel,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraRetryKey by remember { mutableIntStateOf(0) }
    var hasAskedForPermission by rememberSaveable { mutableStateOf(false) }

    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        cameraPermissionGranted = granted
        if (granted) {
            viewModel.revealControls()
        }
    }

    LaunchedEffect(cameraPermissionGranted) {
        if (!cameraPermissionGranted && !hasAskedForPermission) {
            hasAskedForPermission = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { viewModel.revealControls() })
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoomChange, _ ->
                    viewModel.onPinchZoom(zoomChange)
                }
            },
    ) {
        if (cameraPermissionGranted) {
            key(cameraRetryKey) {
                MirrorPreview(
                    mirrored = uiState.mirrored,
                    zoomRatio = uiState.zoomRatio,
                    modifier = if (uiState.isHalfScreen) {
                        Modifier.fillMaxWidth().fillMaxHeight(0.5f).align(Alignment.TopCenter)
                    } else {
                        Modifier.fillMaxSize()
                    },
                    onPreviewViewReady = { previewView = it },
                    onCameraReady = viewModel::onCameraReady,
                    onCameraError = viewModel::onCameraError,
                )
            }

            uiState.frozenFrame?.let { frozenFrame ->
                Image(
                    bitmap = frozenFrame.asImageBitmap(),
                    contentDescription = null,
                    modifier = (if (uiState.isHalfScreen) {
                        Modifier.fillMaxWidth().fillMaxHeight(0.5f).align(Alignment.TopCenter)
                    } else {
                        Modifier.fillMaxSize()
                    }).graphicsLayer {
                        scaleX = if (uiState.mirrored) -1f else 1f
                    },
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
            PermissionContent(
                onRequestPermission = {
                    hasAskedForPermission = true
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
            )
        }

        uiState.cameraError?.let { message ->
            CameraErrorContent(
                message = message,
                onRetry = {
                    cameraRetryKey += 1
                    viewModel.revealControls()
                },
                modifier = Modifier.align(Alignment.Center),
            )
        }

        AnimatedVisibility(
            visible = cameraPermissionGranted && uiState.controlsVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(16.dp),
            enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.96f),
            exit = fadeOut(tween(220)) + scaleOut(tween(220), targetScale = 0.96f),
        ) {
            MirrorControls(
                uiState = uiState,
                onFreezeToggle = {
                    if (uiState.isFrozen) {
                        viewModel.unfreezeFrame()
                    } else {
                        previewView?.captureFrame()?.let(viewModel::freezeFrame)
                    }
                },
                onZoomChange = viewModel::setZoomRatio,
                onBrightnessChange = viewModel::setBrightnessLevel,
                onMirrorToggle = viewModel::toggleMirror,
                onHalfScreenToggle = viewModel::toggleHalfScreen,
                onOpenSettings = onOpenSettings,
                onInteractionStart = viewModel::holdControls,
                onInteractionEnd = viewModel::hideControlsSoon,
            )
        }
    }
}

@Composable
private fun MirrorControls(
    uiState: MirrorUiState,
    onFreezeToggle: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onBrightnessChange: (com.mirrorx.data.BrightnessLevel) -> Unit,
    onMirrorToggle: () -> Unit,
    onHalfScreenToggle: () -> Unit,
    onOpenSettings: () -> Unit,
    onInteractionStart: () -> Unit,
    onInteractionEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.14f),
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassIconButton(
                    icon = Icons.Rounded.Cameraswitch,
                    contentDescription = if (uiState.mirrored) "Mirror off" else "Mirror on",
                    selected = uiState.mirrored,
                    onClick = onMirrorToggle,
                )

                GlassIconButton(
                    icon = if (uiState.isHalfScreen) Icons.Rounded.Fullscreen else Icons.Rounded.FullscreenExit,
                    contentDescription = "Toggle Fullscreen",
                    selected = uiState.isHalfScreen,
                    onClick = onHalfScreenToggle,
                )

                FreezeButton(
                    isFrozen = uiState.isFrozen,
                    onClick = onFreezeToggle,
                )

                GlassIconButton(
                    icon = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    selected = false,
                    onClick = onOpenSettings,
                )
            }

            ZoomControl(
                zoomRatio = uiState.zoomRatio,
                maxZoomRatio = uiState.effectiveMaxZoom,
                onZoomChange = onZoomChange,
                onInteractionStart = onInteractionStart,
                onInteractionEnd = onInteractionEnd,
            )

            BrightnessControl(
                brightnessLevel = uiState.brightnessLevel,
                onBrightnessChange = onBrightnessChange,
                onInteractionStart = onInteractionStart,
                onInteractionEnd = onInteractionEnd,
            )
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        modifier = modifier.size(52.dp),
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.86f)
            } else {
                Color.White.copy(alpha = 0.16f)
            },
            contentColor = if (selected) Color.Black else Color.White,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Composable
private fun PermissionContent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "MirrorX",
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.privacy_statement),
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("Allow Camera")
        }
    }
}

@Composable
private fun CameraErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .padding(24.dp)
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.size(8.dp))
                Text("Retry")
            }
        }
    }
}

private fun PreviewView.captureFrame(): Bitmap? =
    runCatching {
        bitmap?.copy(Bitmap.Config.ARGB_8888, false)
    }.getOrNull()
