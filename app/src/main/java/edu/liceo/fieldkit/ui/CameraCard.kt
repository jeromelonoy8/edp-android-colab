package edu.liceo.fieldkit.ui

import android.Manifest
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.liceo.fieldkit.hardware.*
import edu.liceo.fieldkit.permissions.rememberPermission
import java.io.File

@Composable
fun CameraCard() {
    val context = LocalContext.current
    val cameraPermission = rememberPermission(Manifest.permission.CAMERA)
    val capture = remember { ImageCapture.Builder().build() }
    var photo by remember { mutableStateOf<File?>(null) }

    var cam by remember { mutableStateOf<Camera?>(null) }
    var torchOn by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Field photo", style = MaterialTheme.typography.titleMedium)
            PermissionGate(
                state = cameraPermission,
                feature = "Camera",
                reason = "We need the camera to photograph the issue you report."
            ) {
                // TODO 9a: Camera preview
                CameraPreview(capture, Modifier.fillMaxWidth().height(240.dp)) { cam = it }

                // TODO 9b: Take photo button
                Button(onClick = {
                    takePhoto(context, capture) { saved -> photo = saved }
                }) {
                    Text("Take photo")
                }

                // TODO 9c: Photo file path text
                photo?.let { Text("Saved: ${it.name}") }

                // TODO 13: Shake capture (Bonus)
                val shake = rememberAccelerometer()
                val isShaking by remember { derivedStateOf { isShake(shake) } }
                var lastShot by remember { mutableLongStateOf(0L) }

                LaunchedEffect(isShaking) {
                    val now = System.currentTimeMillis()
                    if (isShaking && now - lastShot > 1500) {
                        lastShot = now
                        takePhoto(context, capture) { saved -> photo = saved }
                        context.buzz()
                        Log.d("FieldKit", "Shake capture")
                    }
                }

                // TODO 14: Flashlight / Torch (Bonus)
                if (cam?.cameraInfo?.hasFlashUnit() == true) {
                    Button(onClick = {
                        torchOn = !torchOn
                        cam?.cameraControl?.enableTorch(torchOn)
                    }) {
                        Text(if (torchOn) "Torch off" else "Torch on")
                    }
                }
            }

            // Last photo thumbnail preview
            photo?.let { f ->
                val thumb = remember(f) { loadThumb(f) }
                thumb?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Last photo",
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
        }
    }
}