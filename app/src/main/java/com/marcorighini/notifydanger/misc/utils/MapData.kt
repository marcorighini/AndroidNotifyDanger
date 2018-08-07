package com.marcorighini.notifydanger.misc.utils

data class Point(val lat: Double, val lon: Double)
data class Points(val points: List<Point>)
data class PointData(val point: Point, val vel: Double, val stepMeters: Double, val time: Double)
data class Route(val pointsData: List<PointData>)