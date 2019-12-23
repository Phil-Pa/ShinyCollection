package de.phil.solidsabissupershinysammlung

import android.app.Activity
import android.app.Application
import android.content.Context
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.phil.solidsabissupershinysammlung.di.AppInjector
import javax.inject.Inject

class MyApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>


    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

    }

    override fun activityInjector() = dispatchingAndroidInjector
}