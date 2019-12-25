package de.phil.solidsabissupershinysammlung.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.phil.solidsabissupershinysammlung.viewmodel.ViewModelFactory
import de.phil.solidsabissupershinysammlung.di.key.ViewModelKey
import de.phil.solidsabissupershinysammlung.viewmodel.AddNewPokemonViewModel
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel

/**
 * View Model module which provides the viewmodelfactory and viewmodel instances
 **/
@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddNewPokemonViewModel::class)
    fun bindAddNewPokemonViewModel(addNewPokemonViewModel: AddNewPokemonViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}