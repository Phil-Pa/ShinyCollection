package de.phil.solidsabissupershinysammlung

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log


object AppUtil {

    fun getDrawableFromURL(url: String): Bitmap? {
        val result = DownloadImageTask().execute(url)
        return result.get()
    }

    fun getAllPokemonNames(context: Context) : List<String> {
        val names1 = context.resources.getStringArray(R.array.gen1Names)
        val names2 = context.resources.getStringArray(R.array.gen2Names)
        val names3 = context.resources.getStringArray(R.array.gen3Names)
        val names4 = context.resources.getStringArray(R.array.gen4Names)
        val names5 = context.resources.getStringArray(R.array.gen5Names)
        val names6 = context.resources.getStringArray(R.array.gen6Names)
        val names7 = context.resources.getStringArray(R.array.gen7Names)

        val result = mutableListOf<String>()
        for (i in names1)
            result.add(i)

        for (i in names2)
            result.add(i)

        for (i in names3)
            result.add(i)

        for (i in names4)
            result.add(i)

        for (i in names5)
            result.add(i)

        for (i in names6)
            result.add(i)

        for (i in names7)
            result.add(i)

        return result
    }

}

class DownloadImageTask : AsyncTask<String, Void, Bitmap>() {

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