package com.nndwn.runtext.ui.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import java.io.File
import java.io.RandomAccessFile
import java.util.Locale

object DeviceSpecsLogger {

    private const val TAG = "DEBUG_DEVICE_SPECS"

    fun logSpecs(context: Context, windowSizeClass: WindowSizeClass) {
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density
        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels

        val screenWidthDp = (screenWidthPx / density).toInt()
        val screenHeightDp = (screenHeightPx / density).toInt()

        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager)?.defaultDisplay
        }

        val refreshRate = display?.refreshRate ?: -1f

        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val totalRamGb = String.format(Locale.US, "%.2f GB", memInfo.totalMem / (1024f * 1024f * 1024f))
        val availRamGb = String.format(Locale.US, "%.2f GB", memInfo.availMem / (1024f * 1024f * 1024f))

        val cpuCores = Runtime.getRuntime().availableProcessors()
        val cpuHardware = Build.HARDWARE
        val cpuBoard = Build.BOARD
        val cpuName = getCpuName()

        val sb = StringBuilder().apply {
            appendLine("\n==================================================")
            appendLine("            DEVICE DEBUG SPECIFICATIONS           ")
            appendLine("==================================================")
            appendLine(" Brand & Model   : ${Build.MANUFACTURER.uppercase()} ${Build.MODEL} (${Build.DEVICE})")
            appendLine(" Android Version : Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine(" System Build    : ${Build.DISPLAY}")
            appendLine("--------------------------------------------------")
            appendLine(" CPU / Chipset   : $cpuName ($cpuHardware / $cpuBoard)")
            appendLine(" CPU Cores       : $cpuCores Cores")
            appendLine(" RAM (Total/Free): $totalRamGb Total / $availRamGb Available")
            appendLine(" Low RAM Device  : ${memInfo.lowMemory}")
            appendLine("--------------------------------------------------")
            appendLine(" Resolution (Px) : ${screenWidthPx}x${screenHeightPx} px")
            appendLine(" Resolution (Dp) : ${screenWidthDp}x${screenHeightDp} dp")
            appendLine(" Screen Density  : ${displayMetrics.densityDpi} dpi (Scale Factor: ${density}x)")
            appendLine(" Refresh Rate    : $refreshRate Hz")
            appendLine("--------------------------------------------------")
            appendLine(" Window Width    : ${windowSizeClass.widthSizeClass}")
            appendLine(" Window Height   : ${windowSizeClass.heightSizeClass}")
            appendLine("==================================================")
        }

        Log.d(TAG, sb.toString())
    }

    private fun getCpuName(): String {
        return try {
            val file = File("/proc/cpuinfo")
            if (file.exists()) {
                val reader = RandomAccessFile(file, "r")
                var line: String?
                var hardwareName = ""
                while (reader.readLine().also { line = it } != null) {
                    if (line?.startsWith("Hardware") == true || line?.startsWith("model name") == true) {
                        hardwareName = line.split(":").getOrNull(1)?.trim() ?: ""
                        if (hardwareName.isNotEmpty()) break
                    }
                }
                reader.close()
                if (hardwareName.isNotEmpty()) hardwareName else Build.HARDWARE
            } else {
                Build.HARDWARE
            }
        } catch (e: Exception) {
            Build.HARDWARE
        }
    }
}