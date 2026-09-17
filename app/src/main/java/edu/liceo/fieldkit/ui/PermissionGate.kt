package edu.liceo.fieldkit.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import edu.liceo.fieldkit.permissions.PermStatus

@Composable
fun PermissionGate(
    state: MutableState<PermStatus>,
    feature: String,
    reason: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    when (state.value) {
        // TODO 4a: PermStatus.Granted -> content()
        PermStatus.Granted -> content()
        
        // TODO 4b: PermStatus.Rationale -> show reason and "Try again" button
        PermStatus.Rationale -> {
            Column {
                Text(reason)
                Button(onClick = { state.value = PermStatus.Rationale }) {
                    Text("Try again")
                }
            }
        }
        
        // TODO 4c: PermStatus.Blocked -> show "Blocked" and "Open Settings" button
        PermStatus.Blocked -> {
            Column {
                Text("$feature permission is blocked. Please enable it in Settings.")
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            }
        }
        
        // TODO 4d: PermStatus.NotAsked -> show "Allow <feature>" button
        PermStatus.NotAsked -> {
            Button(onClick = { state.value = PermStatus.NotAsked }) {
                Text("Allow $feature")
            }
        }
    }
}