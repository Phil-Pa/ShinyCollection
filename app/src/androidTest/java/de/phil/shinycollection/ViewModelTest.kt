package de.phil.shinycollection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.phil.shinycollection.database.PokemonDatabase
import de.phil.shinycollection.model.PokemonEdition
import de.phil.shinycollection.viewmodel.MainViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class ViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: PokemonDatabase
    private lateinit var viewModel: MainViewModel

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val context: ShinyPokemonApplication = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as ShinyPokemonApplication


        viewModel = MainViewModel(context)
    }

    @Test
    fun test() {

        val edition = viewModel.getPokemonEdition()

        viewModel.import(TestData.CLIPBOARD_DATA) {}
        viewModel.setPokemonEdition(PokemonEdition.SWSH)

        for (i in 0..5) {
            val pokemons = viewModel.getAllPokemonDataFromTabIndex(i)

            for (pokemon in pokemons)
                println(pokemon)
        }

        assert(edition == PokemonEdition.SM)

    }

    @After
    fun cleanUp() {

    }

}