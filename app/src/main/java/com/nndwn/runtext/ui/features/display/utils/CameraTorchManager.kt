package com.nndwn.runtext.ui.features.display.utils

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log

class CameraTorchManager(context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var cameraId: String? = null

    init {
        try {
            cameraId = cameraManager?.cameraIdList?.firstOrNull()
        } catch (e: Exception) {
            Log.e("CameraTorchManager", "Gagal mendapatkan Camera ID: ${e.message}")
        }
    }

    fun setTorchEnabled(enabled: Boolean) {
        val id = cameraId ?: return
        try {
            cameraManager?.setTorchMode(id, enabled)
        } catch (e: Exception) {
            Log.e("CameraTorchManager", "Gagal mengontrol Flashlight: ${e.message}")
        }
    }
}