package de.phil.solidsabissupershinysammlung.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import de.phil.solidsabissupershinysammlung.database.AndroidPokemonResources
import de.phil.solidsabissupershinysammlung.database.PokemonDao
import de.phil.solidsabissupershinysammlung.database.PokemonRepository
import de.phil.solidsabissupershinysammlung.database.PokemonRoomDatabase
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
    fun providePokemonDao(application: Application): PokemonDao {
        return PokemonRoomDatabase.instance(application.applicationContext).pokemonDao()
    }

    @Provides
    @Singleton
    fun providePokemonRepository(androidPokemonResources: AndroidPokemonResources, pokemonDao: PokemonDao, application: Application): PokemonRepository {
        return PokemonRepository(androidPokemonResources, pokemonDao, application)
    }


}