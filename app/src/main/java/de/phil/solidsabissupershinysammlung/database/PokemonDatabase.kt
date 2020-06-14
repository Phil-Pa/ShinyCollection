package de.phil.solidsabissupershinysammlung.database

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication
import de.phil.solidsabissupershinysammlung.model.PokemonData

@Suppress("ObjectPropertyName")
@TypeConverters(Converters::class)
@Database(entities = [PokemonData::class], version = 1, exportSchema = false)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao

    companion object {

        @Volatile
        private var _preferences: SharedPreferences? = null

        fun preferences(application: Application): SharedPreferences {
            val temp = _preferences
            if (temp != null)
                return temp

            synchronized(this) {
                val prefs = application.getSharedPreferences(application.packageName + ShinyPokemonApplication.PREFERENCES_NAME, Context.MODE_PRIVATE)
                _preferences = prefs
                return prefs
            }
        }

        @Volatile
        private var _androidPokemonResources: IAndroidPokemonResources? = null

        fun androidPokemonResources(context: Context): IAndroidPokemonResources {
            val temp = _androidPokemonResources
            if (temp != null)
                return temp

            synchronized(this) {
                val resources = AndroidPokemonResources(context)
                _androidPokemonResources = resources
                return resources
            }
        }

        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        fun instance(context: Context): PokemonDatabase {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    "pokemondata"
                )
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                return instance
            }
        }

    }

}