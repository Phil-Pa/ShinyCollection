package de.phil.solidsabissupershinysammlung.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import de.phil.solidsabissupershinysammlung.database.AndroidPokemonResources
import de.phil.solidsabissupershinysammlung.database.PokemonRepository
import javax.inject.Singleton


@Module(includes = [(ViewModelModule::class)])
class AppModule {


//    @Provides
//    @Singleton
//    fun provideDummyRepository(application: Application): DummyRepository {
//        return DummyRepository(application)
//    }


    @Provides
    @Singleton
    fun providePokemonRepository(androidPokemonResources: AndroidPokemonResources, application: Application): PokemonRepository {
        return PokemonRepository(androidPokemonResources, application)
    }


}