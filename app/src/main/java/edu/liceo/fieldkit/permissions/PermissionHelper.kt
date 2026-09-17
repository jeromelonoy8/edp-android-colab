package edu.liceo.fieldkit.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// TODO 2a: enum PermStatus { NotAsked, Rationale, Blocked, Granted }
enum class PermStatus { NotAsked, Rationale, Blocked, Granted }

// TODO 2b: fun Context.permStatus(permission: String): PermStatus
fun Context.permStatus(permission: String): PermStatus {
    val granted = ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
    if (granted) return PermStatus.Granted

    val activity = this as? ComponentActivity
    val rationale = activity?.shouldShowRequestPermissionRationale(permission) == true
    if (rationale) return PermStatus.Rationale

    // If not granted and no rationale, it could be NotAsked OR Blocked.
    // The simple way for this lab: if it's not rationale and not granted,
    // we assume it's NotAsked until we try to launch it.
    return PermStatus.NotAsked
}

// TODO 3: rememberPermission(permission)
@Composable
fun rememberPermission(permission: String): MutableState<PermStatus> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(context.permStatus(permission)) }

    // TODO 3a: val launcher = rememberLauncherForActivityResult
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // TODO 3b: update state based on isGranted and rationale
        state.value = if (isGranted) PermStatus.Granted
        else {
            val activity = context as? ComponentActivity
            if (activity?.shouldShowRequestPermissionRationale(permission) == true)
                PermStatus.Rationale else PermStatus.Blocked
        }
    }

    // A small hack for "NotAsked" -> "Blocked" transition:
    // If launch() is called and result is false, and rationale is also false, it's Blocked.
    
    return remember(launcher) {
        object : MutableState<PermStatus> by state {
            override var value: PermStatus
                get() = state.value
                set(v) {
                    if (v == PermStatus.NotAsked || v == PermStatus.Rationale) {
                        launcher.launch(permission)
                    }
                }
        }
    }
}