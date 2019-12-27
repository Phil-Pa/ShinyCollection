package de.phil.solidsabissupershinysammlung.viewmodel

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.UpdateStatisticsData
import de.phil.solidsabissupershinysammlung.utils.round
import javax.inject.Inject

class StatisticsViewModel @Inject constructor
    (private val pokemonRepository: IPokemonRepository) : ViewModel() {

    private fun getAverageEncounterUpTo(data: List<PokemonData>, n: Int): Float {

        if (n <= 1)
            return data[0].encounterNeeded.toFloat()

        val res = getAverageEncounterUpTo(data, n - 1)
        return (res * (n - 1) + data[n - 1].encounterNeeded.toFloat()) / n
//        var sum = 0f
//        for (i in 0 until n)
//            sum += data[i].encounterNeeded
//
//        return sum / n.toFloat()
    }

    fun getDataEntries(): List<Entry> {
        val data =
            pokemonRepository.getAllPokemonData()
                .filter{ it.huntMethod == HuntMethod.Hatch && it.encounterNeeded != 0 }
                .sortedBy { it.internalId }

        val list = mutableListOf<Entry>()
        for (i in 1..data.size) {
            list.add(Entry(i.toFloat(), getAverageEncounterUpTo(data, i)))
        }

        return list
    }

    fun getStatistics(): UpdateStatisticsData {

        val numShinys: Int
        var numEggsShinys = 0
        var numSosShinys = 0
        var avgSos = 0f
        var numEggs = 0
        val avgEggs: Float

        for (edition in PokemonEdition.values()) {
            numEggsShinys += pokemonRepository.getTotalNumberOfEggShinys(edition)
            numSosShinys += pokemonRepository.getTotalNumberOfSosShinys(edition)
            numEggs += pokemonRepository.getTotalNumberOfHatchedEggs(edition)
            avgSos += pokemonRepository.getAverageSosEncounter(edition)
        }

        numShinys = numEggsShinys + numSosShinys
        avgSos = (avgSos / PokemonEdition.values().size).round(2)
        avgEggs = (numEggs.toFloat() / numEggsShinys).round(2)

        return UpdateStatisticsData(numShinys, numEggsShinys, numSosShinys, avgSos, numEggs, avgEggs)
    }

}