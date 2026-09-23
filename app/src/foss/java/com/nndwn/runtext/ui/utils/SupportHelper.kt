package com.nndwn.runtext.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun handleSupportAction(context: Context, onShowDialog: () -> Unit) {
  val intent = Intent(Intent.ACTION_VIEW, "https://buymeacoffee.com/nndwn".toUri())
  context.startActivity(intent)
}
