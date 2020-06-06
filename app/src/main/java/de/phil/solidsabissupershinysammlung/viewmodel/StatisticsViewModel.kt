package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.mikephil.charting.data.Entry
import de.phil.solidsabissupershinysammlung.database.PokemonDao
import de.phil.solidsabissupershinysammlung.database.PokemonDatabase
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.UpdateStatisticsData
import de.phil.solidsabissupershinysammlung.utils.round

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonDao: PokemonDao =
        PokemonDatabase.instance(application.applicationContext).pokemonDao()

    private fun getAverageEncounterUpTo(data: List<PokemonData>, n: Int): Float {

        if (n <= 1)
            return data[0].encounterNeeded.toFloat()

        val res = getAverageEncounterUpTo(data, n - 1)
        return (res * (n - 1) + data[n - 1].encounterNeeded.toFloat()) / n
    }

    fun getDataEntries(): List<Entry> {
        val data =
            pokemonDao.getAllPokemonData()
                .filter{ it.huntMethod == HuntMethod.Hatch && it.encounterNeeded != 0 }
                .sortedBy { it.internalId }

        val list = mutableListOf<Entry>()
        for (i in 1..data.size) {
            list.add(Entry(i.toFloat(), getAverageEncounterUpTo(data, i)))
        }

        return list
    }

    fun getAllPokemon(): List<PokemonData> {
        return pokemonDao.getAllPokemonDataFromTabIndex(0)
    }

    fun getStatistics(): UpdateStatisticsData {

        val numShinys: Int
        var numEggsShinys = 0
        var numSosShinys = 0
        var avgSos = 0f
        var numEggs = 0
        val avgEggs: Float

        for (edition in PokemonEdition.values()) {
            numEggsShinys += pokemonDao.getTotalNumberOfEggShinys(edition.ordinal)
            numSosShinys += pokemonDao.getTotalNumberOfSosShinys(edition.ordinal)
            numEggs += pokemonDao.getTotalEggsCount(edition.ordinal)
            avgSos += pokemonDao.getAverageSosCount(edition.ordinal)
        }

        numShinys = numEggsShinys + numSosShinys
        avgSos = (avgSos / PokemonEdition.values().size).round(2)
        avgEggs = (numEggs.toFloat() / numEggsShinys).round(2)

        return UpdateStatisticsData(numShinys, numEggsShinys, numSosShinys, avgSos, numEggs, avgEggs)
    }

}