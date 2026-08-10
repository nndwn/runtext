package com.nndwn.runtext

import androidx.annotation.Keep

@Keep
enum class AppFlavor {
    PLAYSTORE,
    FOSS;

    companion object {
        val current: AppFlavor
            get() = entries.find { it.name.equals(BuildConfig.FLAVOR, ignoreCase = true) }
                ?: PLAYSTORE
    }
}
