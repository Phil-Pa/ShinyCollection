package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData

class PokemonDatabase {

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
                    "tabIndex INT," +
                    "internalId INT" +
                ");")
    }

    fun insert(data: PokemonData) {
        database.execSQL("INSERT INTO $databaseName (pokedexId, huntMethod, name, encounterNeeded, generation, tabIndex, internalId)" +
                        " VALUES (${data.pokedexId}, ${data.huntMethod.ordinal}, \"${data.name}\", ${data.encounterNeeded}, ${data.generation}, ${data.tabIndex}, ${data.internalId});")
    }

    fun delete(data: PokemonData) {
        database.execSQL("DELETE FROM $databaseName WHERE internalId = ${data.internalId};")
    }

    fun deleteAll() {
        database.execSQL("DELETE FROM $databaseName;")
    }

    fun delete(pokemonName: String, tabIndex: Int) {
        database.execSQL("DELETE FROM $databaseName WHERE name = '$pokemonName' AND tabIndex = $tabIndex")
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
                val dataTabIndex = cursor.getInt(cursor.getColumnIndex("tabIndex"))
                val internalId = cursor.getInt(cursor.getColumnIndex("internalId"))

                val pokemon = PokemonData(
                    name,
                    pokedexId,
                    generation,
                    eggsNeeded,
                    huntMethod,
                    dataTabIndex,
                    internalId
                )
                pokemonList.add(pokemon)

                cursor.moveToNext()
            }
        }

        cursor.close()

        return pokemonList.toList()
    }

    fun getMaxInternalId(): Int {
        val cursor = database.rawQuery("SELECT MAX(internalId) FROM $databaseName;", null)

        if (!cursor.moveToFirst())
            throw IllegalStateException("can not get max internalId")

        val result = cursor.getInt((cursor.getColumnIndex("internalId")))
        cursor.close()
        return result
    }

    companion object {
        private const val databaseName = "PokemonDatabase"
    }

}