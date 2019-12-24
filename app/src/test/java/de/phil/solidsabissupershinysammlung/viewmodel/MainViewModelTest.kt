package de.phil.solidsabissupershinysammlung.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
class MainViewModelTest {

    @Test
    fun sampleTest() {

        val repository = mock(IPokemonRepository::class.java)
        val viewModel = MainViewModel(repository)

        val liveData: LiveData<MutableList<PokemonData>> = MutableLiveData(mutableListOf())

        val list = mutableListOf<PokemonData>()

        val pokemonData = PokemonData(
            "Bisasam", 1, 1, 342,
            HuntMethod.Hatch, 0
        )

        `when`(repository.insert(pokemonData)).then {
            list.add(pokemonData)
            if (pokemonData.tabIndex == 0)
                liveData.value!!.add(pokemonData)
        }
        `when`(repository.getShinyListData()).thenReturn(liveData as LiveData<List<PokemonData>>)

        viewModel.addPokemon(pokemonData)
        val data = viewModel.getShinyListData()

        assertEquals(1, data.value!!.size)
        assertEquals(1, list.size)
        assertEquals(pokemonData, list.first())
    }

}