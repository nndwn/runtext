package com.nndwn.runtext.ui.features.display.utils

import android.media.AudioManager
import android.media.ToneGenerator

object MorseAudioPlayer {
    private var toneGenerator : ToneGenerator? = null
    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun playBeep(durationMs: Long) {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}