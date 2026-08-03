package com.nndwn.runtext.ui.features.main.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.UiEffect
import com.nndwn.runtext.ui.features.main.MainUiEvent

@Composable
fun MorseTorchConfig(
    enable : Boolean,
    event: (MainUiEvent) -> Unit,
){
    val context = LocalContext.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            event(MainUiEvent.UpdateTorchEnabled(true))
        } else {
            event(MainUiEvent.UpdateTorchEnabled(false))
            event(MainUiEvent.Toast(R.string.notice_camera_permission_denied))

        }
    }
    ConfigCard {
        SwitchRow(
            title = stringResource(R.string.set_config_morse_flashlight),
            subtitle = stringResource(R.string.set_config_morse_flashlight_desc),
            checked = enable,
            onCheckedChange = { isChecked ->
                if (isChecked){
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        event(MainUiEvent.UpdateTorchEnabled(true))
                    } else {
                        val activity = context as? Activity
                        val showRationale = activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
                        } ?: false

                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        if (!showRationale && activity != null) {
                            event(MainUiEvent.Toast(R.string.notice_camera_permission_permanently_denied))
                        }
                    }
                } else {
                    event(MainUiEvent.UpdateTorchEnabled(false)) }
                }
        )
    }
}