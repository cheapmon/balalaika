package com.github.cheapmon.balalaika

import android.app.Application
import com.github.cheapmon.balalaika.di.DaggerAppComponent

class Application : Application() {
    val appComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}