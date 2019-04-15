package de.phil.solidsabissupershinysammlung

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log


object AppUtil {

    fun getDrawableFromURL(url: String): Bitmap? {
        val result = DownloadImageTask().execute(url)
        return result.get()
    }

}

private class DownloadImageTask : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val url = urls[0]
        var image: Bitmap? = null
        try {
            val `in` = java.net.URL(url).openStream()
            image = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }

        return image
    }
}