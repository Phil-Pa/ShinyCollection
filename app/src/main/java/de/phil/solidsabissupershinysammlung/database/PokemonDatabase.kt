package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData

class PokemonDatabase {

    // TODO logging all database operations?

    private lateinit var database: SQLiteDatabase

    fun init(context: Context) {
        database = context.openOrCreateDatabase(databaseName, MODE_PRIVATE, null)
    }

    fun close() {
        database.close()
    }

    fun create() {
        database.execSQL("CREATE TABLE IF NOT EXISTS $databaseName" +
                "(" +
                    "pokedexId INT," +
                    "huntMethod INT," +
                    "name VARCHAR(20)," +
                    "encounterNeeded INT," +
                    "generation INT," +
                    "tabIndex INT" +
                ");")
    }

    fun insert(data: PokemonData, tabIndex: Int) {
        database.execSQL("INSERT INTO $databaseName (pokedexId, huntMethod, name, encounterNeeded, generation, tabIndex)" +
                        " VALUES (${data.pokedexId}, ${data.huntMethod.ordinal}, \"${data.name}\", ${data.encounterNeeded}, ${data.generation}, $tabIndex);")
    }

    fun delete(data: PokemonData, tabIndex: Int) {
        database.execSQL("DELETE FROM $databaseName WHERE pokedexId = ${data.pokedexId} AND encounterNeeded = ${data.encounterNeeded} AND tabIndex = $tabIndex;")
    }

    fun getAllPokemonOfTabIndex(tabIndex: Int): List<PokemonData> {
        val cursor = database.rawQuery("SELECT * FROM $databaseName WHERE tabIndex = $tabIndex;", null)

        val pokemonList = mutableListOf<PokemonData>()

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val pokedexId = cursor.getInt(cursor.getColumnIndex("pokedexId"))
                val huntMethod = HuntMethod.fromInt(
                    cursor.getInt(
                        cursor.getColumnIndex(
                            "huntMethod"
                        )
                    )
                )!!
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val eggsNeeded = cursor.getInt(cursor.getColumnIndex("encounterNeeded"))
                val generation = cursor.getInt(cursor.getColumnIndex("generation"))

                val pokemon = PokemonData(
                    name,
                    pokedexId,
                    generation,
                    eggsNeeded,
                    huntMethod
                )
                pokemonList.add(pokemon)

                cursor.moveToNext()
            }
        }

        cursor.close()

        return pokemonList.toList()
    }

    companion object {
        private const val databaseName = "PokemonDatabase"
    }

}