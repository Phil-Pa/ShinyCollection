package de.phil.solidsabissupershinysammlung.utils

import android.app.Activity
import android.widget.Toast
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