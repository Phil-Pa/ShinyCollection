package de.phil.shinycollection.activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.utils.MessageType


fun Activity.vibrate(millis: Long) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
}

fun Activity.getClipboardStringData(): String? {

    fun Activity.getLatestStringOnClipboard(): String? {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 0 -> text, 1 -> uri, 2 -> intent
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }

    var result: String? = null

    var finished = false

    runOnUiThread {
        result = getLatestStringOnClipboard()
        finished = true
    }

    while (true) {
        if (finished)
            return result
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

fun Activity.showMessage(message: String, type: MessageType) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.initTheme(themeAsString: String) {
    when (themeAsString) {
        getString(R.string.theme_orange) -> activity?.setTheme(R.style.AppThemeSabi)
        getString(R.string.theme_purple) -> activity?.setTheme(R.style.AppThemeTorben)
        getString(R.string.theme_blue) -> activity?.setTheme(R.style.AppThemeJohannes)
        getString(R.string.theme_red) -> activity?.setTheme(R.style.AppThemePhil)
    }
}

fun Activity.initTheme(): String? {

    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    val isMainActivity = this is MainActivity

    val themeAsString = prefs.getString(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME, null)

    when (themeAsString) {
        getString(R.string.theme_orange) -> if (isMainActivity) setTheme(R.style.AppThemeSabi_NoActionBar) else setTheme(R.style.AppThemeSabi)
        getString(R.string.theme_purple) -> if (isMainActivity) setTheme(R.style.AppThemeTorben_NoActionBar) else setTheme(R.style.AppThemeTorben)
        getString(R.string.theme_blue) -> if (isMainActivity) setTheme(R.style.AppThemeJohannes_NoActionBar) else setTheme(R.style.AppThemeJohannes)
        getString(R.string.theme_red) -> if (isMainActivity) setTheme(R.style.AppThemePhil_NoActionBar) else setTheme(R.style.AppThemePhil)
    }

    return themeAsString
}

fun Activity.hideKeyboard() {
    val activity = this

    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// yes = true, no = false
fun Activity.showYesNoDialog(title: String, action: (answer: Boolean) -> Unit) {

    val dialogClickListener = DialogInterface.OnClickListener { _, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                action(true)
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                action(false)
            }
            DialogInterface.BUTTON_NEUTRAL -> {
                showMessage(getString(R.string.dialog_action_canceled), MessageType.Info)
            }
        }
    }

    val builder = AlertDialog.Builder(this)
    builder.setMessage(title)
        .setNeutralButton(getString(R.string.dialog_cancel), dialogClickListener)
        .setPositiveButton(getString(R.string.dialog_yes), dialogClickListener)
        .setNegativeButton(getString(R.string.dialog_no), dialogClickListener)
        .show()
}

fun Activity.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}