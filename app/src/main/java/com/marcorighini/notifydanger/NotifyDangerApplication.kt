package com.marcorighini.notifydanger

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.marcorighini.notifydanger.misc.AppComponent
import com.marcorighini.notifydanger.misc.DaggerAppComponent
import timber.log.Timber


class NotifyDangerApplication: Application() {
    @set:VisibleForTesting
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        component = DaggerAppComponent.builder().application(this).build()
    }
}

val Context.component: AppComponent
    get() = (applicationContext as NotifyDangerApplication).component

val Fragment.component: AppComponent
    get() = activity!!.component