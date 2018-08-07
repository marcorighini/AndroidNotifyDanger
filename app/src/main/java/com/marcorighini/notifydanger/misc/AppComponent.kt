package com.marcorighini.notifydanger.misc

import android.app.Application
import com.marcorighini.notifydanger.map.MapViewModel
import com.marcorighini.notifydanger.map.MapsActivity
import com.marcorighini.notifydanger.misc.services.LocationService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun mapViewModel(): MapViewModel
    fun inject(activity: MapsActivity)
    fun inject(service: LocationService)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}