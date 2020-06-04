package de.phil.solidsabissupershinysammlung.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
class MainViewModelTest {

//    @Test
//    fun sampleTest() {
//        val viewModel = MainViewModel()
//
//        val liveData: LiveData<MutableList<PokemonData>> = MutableLiveData(mutableListOf())
//
//        val list = mutableListOf<PokemonData>()
//
//        val pokemonData = PokemonData(
//            "Bisasam", 1, 1, 342, HuntMethod.Hatch, PokemonEdition.ORAS, 0
//        )
//
//        viewModel.addPokemon(pokemonData)
//        val data = viewModel.getShinyListData()
//
//        assertEquals(1, data.value!!.size)
//        assertEquals(1, list.size)
//        assertEquals(pokemonData, list.first())
//    }

}