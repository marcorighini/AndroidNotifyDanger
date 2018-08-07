package com.marcorighini.notifydanger.misc.services

import com.marcorighini.notifydanger.misc.utils.PointData
import com.marcorighini.notifydanger.misc.utils.Route
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class VehicleDataRepository(routesDataSource: RoutesDataSource) {
    private var route: Route = routesDataSource.getVehicleRouteData()

    fun vehicleData(): Observable<PointData> {
        var timeOffset = 0.0
        return Observable.fromIterable(route.pointsData)
                .concatMap { step ->
                    Observable.just(step)
                            .delay(timeOffset.toLong(), TimeUnit.SECONDS, Schedulers.io())
                            .doOnNext {
                                timeOffset = step.time
                            }
                }
    }
}