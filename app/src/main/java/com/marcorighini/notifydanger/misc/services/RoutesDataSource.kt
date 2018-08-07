package com.marcorighini.notifydanger.misc.services

import android.app.Application
import com.google.gson.Gson
import com.marcorighini.notifydanger.misc.utils.*
import java.nio.charset.Charset
import java.util.*

class RoutesDataSource(private val application: Application, private val gson: Gson) {
    private val random = Random()

    fun getVehicleRouteData(): Route {
        val inps = application.assets.open("points.json")
        val size = inps.available()
        val buffer = ByteArray(size)
        inps.read(buffer)
        inps.close()
        val json = String(buffer, Charset.forName("UTF-8"))
        val points = gson.fromJson(json, Points::class.java)
        var last: Point? = null
        val steps = mutableListOf<PointData>()
        for (point in points.points) {
            val (vel, distance, time) = last?.let {
                val v = (random.nextGaussian() + MEAN_VELOCITY) * 1000 / 3600
                val d = distanceBetween(it.lat, it.lon, point.lat, point.lon)
                val t = d / v
                Triple(v, d, t)
            } ?: Triple(0.0, 0.0, 0.0)
            steps.add(PointData(point, vel, distance, time))
            last = point
        }
        return Route(steps)
    }



    companion object {
        const val MEAN_VELOCITY = 50.0F
    }
}