package de.phil.shinycollection.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.phil.shinycollection.database.PokemonDao
import de.phil.shinycollection.database.PokemonDatabase
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonEdition
import de.phil.shinycollection.model.UpdateStatisticsData
import de.phil.shinycollection.utils.round

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonDao: PokemonDao =
        PokemonDatabase.instance(application.applicationContext).pokemonDao()

    fun getAllPokemon(): List<PokemonData> {
        return pokemonDao.getAllPokemonDataFromTabIndex(0)
    }

    fun getStatistics(): UpdateStatisticsData {

        val numShinys: Int
        var numEggsShinys = 0
        var numSosShinys = 0
        var avgSos = 0f
        var numEggs = 0
        var avgEggs: Float

        for (edition in PokemonEdition.values()) {
            numEggsShinys += pokemonDao.getTotalNumberOfEggShinys(edition.ordinal)
            numSosShinys += pokemonDao.getTotalNumberOfSosShinys(edition.ordinal)
            numEggs += pokemonDao.getTotalEggsCount(edition.ordinal)
            avgSos += pokemonDao.getAverageSosCount(edition.ordinal)
        }

        numShinys = numEggsShinys + numSosShinys
        avgSos = (avgSos / PokemonEdition.values().size).round(2)
        avgEggs = (numEggs.toFloat() / numEggsShinys).round(2)

        if (avgEggs.isNaN())
            avgEggs = 0.0f

        return UpdateStatisticsData(numShinys, numEggsShinys, numSosShinys, avgSos, numEggs, avgEggs)
    }

}