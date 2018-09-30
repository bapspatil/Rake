package com.bapspatil.rake

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree



/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class RakeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}