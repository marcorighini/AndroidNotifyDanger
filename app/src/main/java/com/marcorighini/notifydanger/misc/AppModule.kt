package com.marcorighini.notifydanger.misc

import android.app.Application
import android.app.NotificationManager
import com.google.gson.Gson
import com.marcorighini.notifydanger.misc.services.NotificationController
import com.marcorighini.notifydanger.misc.services.RoutesDataSource
import com.marcorighini.notifydanger.misc.services.VehicleDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import android.content.Context.NOTIFICATION_SERVICE
import com.google.android.gms.location.LocationServices

@Module
class AppModule {
    @Provides
    @Singleton
    fun providesGson() = Gson()

    @Provides
    @Singleton
    fun provideGsonLoader(application: Application, gson: Gson) = RoutesDataSource(application, gson)

    @Provides
    @Singleton
    fun provideVehicleLocationProvider(routesDataSource: RoutesDataSource) = VehicleDataRepository(routesDataSource)

    @Provides
    @Singleton
    fun provideNotificationController(application: Application) = NotificationController(application, application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application) = LocationServices.getFusedLocationProviderClient(application)
}