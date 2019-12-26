package de.phil.solidsabissupershinysammlung.viewmodel

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
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
            pokemonRepository.getAllPokemonData().filter { it.huntMethod == HuntMethod.Hatch && it.encounterNeeded != 0 }
                .sortedBy { it.internalId }

        val list = mutableListOf<Entry>()
        for (i in 1..data.size) {
            list.add(Entry(i.toFloat(), getAverageEncounterUpTo(data, i)))
        }

        return list
    }


}