package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import java.lang.IllegalStateException

class PokemonDatabase {

    private lateinit var database: SQLiteDatabase

    fun init(context: Context) {
        database = context.openOrCreateDatabase(databaseName, MODE_PRIVATE, null)
    }

    fun close() {
        database.close()
    }

    fun create() {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS $databaseName" +
                    "(" +
                    "pokedexId INT," +
                    "huntMethod INT," +
                    "name VARCHAR(20)," +
                    "encounterNeeded INT," +
                    "generation INT," +
                    "tabIndex INT," +
                    "internalId INT" +
                    ");"
        )
    }

    fun insert(data: PokemonData) {
        database.execSQL(
            "INSERT INTO $databaseName (pokedexId, huntMethod, name, encounterNeeded, generation, tabIndex, internalId)" +
                    " VALUES (${data.pokedexId}, ${data.huntMethod.ordinal}, \"${data.name}\", ${data.encounterNeeded}, ${data.generation}, ${data.tabIndex}, ${data.internalId});"
        )
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
        // filter query
        val cursor = database.rawQuery("SELECT * FROM $databaseName WHERE tabIndex = $tabIndex;", null)

        // create new list
        val pokemonList = mutableListOf<PokemonData>()

        // iterate over cursor
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {

                // get current cursor item data

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

                // add data to the result

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

    fun getNumberOfDataSets(): Int {
        val cursor = database.rawQuery("SELECT COUNT(*) FROM $databaseName", null)
        cursor.moveToFirst()
        val numDataSets = cursor.getInt(0)
        cursor.close()
        return numDataSets
    }

    fun getMaxInternalId(): Int {

        if (getNumberOfDataSets() == 0) {
            return 0
        }

        val cursor = database.rawQuery("SELECT MAX(internalId) FROM $databaseName;", null)

        if (!cursor.moveToFirst())
            throw IllegalStateException()

        val result = cursor.getInt(0)
        cursor.close()
        return result
    }

    companion object {
        private const val databaseName = "PokemonDatabase"
    }

}