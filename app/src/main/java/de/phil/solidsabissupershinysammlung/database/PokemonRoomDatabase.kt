package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.phil.solidsabissupershinysammlung.model.PokemonData

@TypeConverters(Converters::class)
@Database(entities = [PokemonData::class], version = 1, exportSchema = false)
abstract class PokemonRoomDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao

    companion object {

        @Volatile
        private var INSTANCE: PokemonRoomDatabase? = null

        fun instance(context: Context): PokemonRoomDatabase {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonRoomDatabase::class.java,
                    "pokemondata"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}