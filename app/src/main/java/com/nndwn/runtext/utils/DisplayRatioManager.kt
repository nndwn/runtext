package com.nndwn.runtext.utils

import android.content.Context

/**
 * Singleton untuk mengelola dan menghitung dimensi layar (width, height) serta rasio landscape.
 *
 * Nilai diinisialisasi pertama kali saat aplikasi dibuka dan akan tetap bertahan di memori
 * selama aplikasi tidak dimatikan.
 *
 * Fitur:
 * 1. Menghitung rasio landscape (selalu max(width, height) / min(width, height)).
 * 2. Mengubah tinggi akan menyesuaikan lebar secara otomatis berdasarkan rasio.
 * 3. Mengubah lebar akan menyesuaikan tinggi secara otomatis berdasarkan rasio.
 * 4. Fungsi bantu untuk kalkulasi tanpa mengubah nilai internal.
 */
object DisplayRatioManager {

    @Volatile
    private var isInitialized = false

    /** Rasio landscape (selalu >= 1.0f, yaitu max / min) */
    var ratio: Float = 16f / 9f
        private set

    /** Lebar saat ini */
    var width: Float = 0f
        private set

    /** Tinggi saat ini */
    var height: Float = 0f
        private set

    /**
     * Menginisialisasi Singleton menggunakan Context perangkat.
     * Hanya akan menghitung rasio pada pemanggilan pertama agar nilainya tetap bertahan
     * selama aplikasi berjalan.
     */
    @Synchronized
    fun init(context: Context) {
        if (isInitialized) return

        val displayMetrics = context.resources.displayMetrics
        val w = displayMetrics.widthPixels.toFloat()
        val h = displayMetrics.heightPixels.toFloat()

        init(w, h)
    }

    /**
     * Menginisialisasi Singleton dengan lebar dan tinggi secara langsung.
     * Menggaransi rasio berorientasi landscape (max(w, h) / min(w, h)).
     */
    @Synchronized
    fun init(w: Float, h: Float) {
        if (isInitialized || w <= 0f || h <= 0f) return

        val landscapeWidth = maxOf(w, h)
        val landscapeHeight = minOf(w, h)

        this.ratio = landscapeWidth / landscapeHeight
        this.width = landscapeWidth
        this.height = landscapeHeight
        this.isInitialized = true
    }

    /**
     * Mengatur nilai tinggi baru, lebar akan disesuaikan otomatis berdasarkan rasio.
     * @param newHeight Nilai tinggi baru
     * @return Nilai lebar baru yang telah disesuaikan
     */
    @Synchronized
    fun updateHeight(newHeight: Float): Float {
        val validHeight = newHeight.coerceAtLeast(0f)
        this.height = validHeight
        this.width = validHeight * ratio
        return this.width
    }

    /**
     * Mengatur nilai lebar baru, tinggi akan disesuaikan otomatis berdasarkan rasio.
     * @param newWidth Nilai lebar baru
     * @return Nilai tinggi baru yang telah disesuaikan
     */
    @Synchronized
    fun updateWidth(newWidth: Float): Float {
        val validWidth = newWidth.coerceAtLeast(0f)
        this.width = validWidth
        this.height = if (ratio != 0f) validWidth / ratio else 0f
        return this.height
    }

    /**
     * Menghitung lebar berdasarkan tinggi tertentu sesuai rasio yang telah dihitung.
     */
    fun getWidthForHeight(targetHeight: Float): Float {
        return targetHeight * ratio
    }

    /**
     * Menghitung tinggi berdasarkan lebar tertentu sesuai rasio yang telah dihitung.
     */
    fun getHeightForWidth(targetWidth: Float): Float {
        return if (ratio != 0f) targetWidth / ratio else 0f
    }

    /**
     * Mengecek apakah Singleton sudah diinisialisasi.
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * Mengatur ulang state (digunakan untuk unit testing).
     */
    @Synchronized
    fun resetForTesting() {
        isInitialized = false
        width = 0f
        height = 0f
        ratio = 16f / 9f
    }
}
