package de.phil.solidsabissupershinysammlung.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import de.phil.solidsabissupershinysammlung.database.*
import de.phil.solidsabissupershinysammlung.worker.BackgroundDataManager
import de.phil.solidsabissupershinysammlung.worker.DataManager
import javax.inject.Singleton


@Module(includes = [(ViewModelModule::class)])
class AppModule {

    @Provides
    @Singleton
    fun providePokemonDao(application: Application): PokemonDao {
        return PokemonRoomDatabase.instance(application.applicationContext).pokemonDao()
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        androidPokemonResources: AndroidPokemonResources,
        pokemonDao: PokemonDao
    ): IPokemonRepository {
        return PokemonRepository(androidPokemonResources, pokemonDao)
    }

    @Provides
    @Singleton
    fun provideAndroidPokemonResources(): IAndroidPokemonResources {
        return AndroidPokemonResources()
    }

    @Provides
    @Singleton
    fun provideBackgroundDataManager(): BackgroundDataManager {
        return DataManager()
    }

}