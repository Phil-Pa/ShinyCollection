package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData

class PokemonDatabase {

    // TODO create a table for the pokemon lists
    // TODO change database layout because of is alola?

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
                    "generation INT" +
                ");")
    }

    fun insert(data: PokemonData) {
        database.execSQL("INSERT INTO $databaseName (pokedexId, huntMethod, name, encounterNeeded, generation)" +
                        " VALUES (${data.pokedexId}, ${data.huntMethod.ordinal}, \"${data.name}\", ${data.encounterNeeded}, ${data.generation});")
    }

    fun getAllPokemon() : List<PokemonData> {
        val cursor = database.rawQuery("SELECT * FROM $databaseName;", null)

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

        return pokemonList
    }

    fun delete(data: PokemonData) {
        database.execSQL("DELETE FROM $databaseName WHERE pokedexId = ${data.pokedexId} AND encounterNeeded = ${data.encounterNeeded};")
    }

    companion object {
        private const val databaseName = "PokemonDatabase"
    }

}