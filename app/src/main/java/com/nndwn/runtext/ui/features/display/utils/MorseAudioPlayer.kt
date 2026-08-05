package com.nndwn.runtext.ui.features.display.utils

import android.media.AudioManager
import android.media.ToneGenerator

object MorseAudioPlayer {
    private var toneGenerator: ToneGenerator? = null

    private fun getToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                // Gunakan STREAM_MUSIC agar mengikuti volume media HP
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return toneGenerator
    }

    fun playBeep(durationMs: Long) {
        try {
            val generator = getToneGenerator()
            generator?.stopTone()
            generator?.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            release()
        }
    }

    fun stop() {
        try {
            toneGenerator?.stopTone()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            toneGenerator = null
        }
    }
}