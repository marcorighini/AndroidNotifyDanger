package com.marcorighini.notifydanger.map

data class LatLon(val lat: Double, val lon: Double)
data class VehicleData(val state: LocationState, val point: LatLon?, val dangerRadius: Double?)
data class PersonData(val state: LocationState, val point: LatLon?)
enum class LocationState {
    INACTIVE, ACTIVE
}
enum class DangerState {
    INACTIVE, NO_DANGER, ENTERED_IN_DANGER, IN_DANGER, EXIT_FROM_DANGER
}

data class MapState(val vehicleData: VehicleData, val personData: PersonData, val dangerState: DangerState)
