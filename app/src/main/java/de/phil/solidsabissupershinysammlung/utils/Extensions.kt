package de.phil.solidsabissupershinysammlung.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import de.phil.solidsabissupershinysammlung.R
import es.dmoral.toasty.Toasty

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return (kotlin.math.round(this * multiplier) / multiplier)
}

enum class MessageType {
    Success,
    Warning,
    Info,
    Error
}

fun Activity.showMessage(message: String, type: MessageType) {
    when (type) {
        MessageType.Success -> Toasty.success(this, message, Toast.LENGTH_LONG).show()
        MessageType.Warning -> Toasty.warning(this, message, Toast.LENGTH_LONG).show()
        MessageType.Info -> Toasty.info(this, message, Toast.LENGTH_LONG).show()
        MessageType.Error -> Toasty.error(this, message, Toast.LENGTH_LONG).show()
    }
}

fun Activity.copyToClipboard(data: String) {
    runOnUiThread {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Pokemon Data", data)
        clipboard.setPrimaryClip(clip)
        showMessage(getString(R.string.copied_data), MessageType.Success)
    }
}

fun Activity.getClipboardStringData(): String? {
    var result: String? = null

    var finished = false

    runOnUiThread {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // 0 -> text, 1 -> uri, 2 -> intent

        result = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
        finished = true
    }

    while (true) {
        if (finished)
            return result
    }
}