package de.phil.solidsabissupershinysammlung.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.phil.solidsabissupershinysammlung.activity.AddNewPokemonActivity
import de.phil.solidsabissupershinysammlung.activity.MainActivity

/*
 * The module which provides the android injection service to activities
 */
@Suppress("unused")
@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeAddNewPokemonActivity(): AddNewPokemonActivity

}