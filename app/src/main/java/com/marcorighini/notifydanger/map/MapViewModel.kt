package com.marcorighini.notifydanger.map

import androidx.lifecycle.ViewModel
import com.marcorighini.notifydanger.misc.services.VehicleDataRepository
import com.marcorighini.notifydanger.misc.utils.LiveDataDelegate
import com.marcorighini.notifydanger.misc.utils.distanceBetween
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MapViewModel @Inject constructor(private val vehicleDataProvider: VehicleDataRepository) : ViewModel() {
    val liveData = LiveDataDelegate(MapState(VehicleData(LocationState.INACTIVE, null, null), PersonData(LocationState.INACTIVE, null), DangerState.INACTIVE))
    private val compositeDisposable = CompositeDisposable()
    private var state by liveData

    fun onVehicleStart() {
        compositeDisposable += vehicleDataProvider.vehicleData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    state = state.copy(vehicleData = state.vehicleData.copy(state = LocationState.INACTIVE, dangerRadius = null))
                }
                .subscribeBy(
                        onNext = {
                            val dangerRadius = it.vel * it.vel / (2 * 9.8 * 0.4) + it.vel
                            val vehiclePoint = LatLon(it.point.lat, it.point.lon)
                            state = state.copy(
                                    vehicleData = VehicleData(LocationState.ACTIVE, vehiclePoint, dangerRadius),
                                    dangerState = dangerState(state.dangerState, state.personData.point, vehiclePoint, dangerRadius))
                        }
                )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onNewPosition(latitude: Double, longitude: Double) {
        val personPoint = LatLon(latitude, longitude)
        state = state.copy(
                personData = PersonData(LocationState.ACTIVE, personPoint),
                dangerState = dangerState(state.dangerState, personPoint, state.vehicleData.point, state.vehicleData.dangerRadius))
    }

    private fun dangerState(prevState: DangerState, point: LatLon?, dangerPoint: LatLon?, dangerRadius: Double?): DangerState {
        return if (point != null && dangerPoint != null && dangerRadius != null) {
            val inside = distanceBetween(point.lat, point.lon, dangerPoint.lat, dangerPoint.lon) < dangerRadius
            when (prevState) {
                DangerState.INACTIVE -> if (inside) DangerState.ENTERED_IN_DANGER else DangerState.NO_DANGER
                DangerState.NO_DANGER -> if (inside) DangerState.ENTERED_IN_DANGER else DangerState.NO_DANGER
                DangerState.ENTERED_IN_DANGER -> if (inside) DangerState.IN_DANGER else DangerState.EXIT_FROM_DANGER
                DangerState.IN_DANGER -> if (inside) DangerState.IN_DANGER else DangerState.EXIT_FROM_DANGER
                DangerState.EXIT_FROM_DANGER -> if (inside) DangerState.ENTERED_IN_DANGER else DangerState.NO_DANGER
            }
        } else {
            DangerState.INACTIVE
        }
    }
}