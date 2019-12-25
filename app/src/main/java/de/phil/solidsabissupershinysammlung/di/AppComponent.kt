package de.phil.solidsabissupershinysammlung.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication
import de.phil.solidsabissupershinysammlung.di.modules.ActivityBuilderModule
import de.phil.solidsabissupershinysammlung.di.modules.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ActivityBuilderModule::class, AndroidInjectionModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(shinyPokemonApplication: ShinyPokemonApplication)
}