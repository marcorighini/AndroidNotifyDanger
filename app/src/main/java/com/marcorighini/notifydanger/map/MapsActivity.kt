package com.marcorighini.notifydanger.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import com.marcorighini.notifydanger.R
import com.marcorighini.notifydanger.component
import com.marcorighini.notifydanger.misc.utils.viewModelProvider
import kotlinx.android.synthetic.main.activity_maps.*
import timber.log.Timber
import com.google.android.gms.maps.CameraUpdateFactory
import com.marcorighini.notifydanger.misc.services.LocationService
import com.marcorighini.notifydanger.misc.services.NotificationController
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel by viewModelProvider { component.mapViewModel() }
    private var map: GoogleMap? = null
    private var vehicleCircle: Circle? = null
    private val compositeDisposable = CompositeDisposable()
    private var locationServiceIntent: Intent? = null
    private var locationReceiver: BroadcastReceiver? = null
    @Inject
    lateinit var notificationController: NotificationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel.liveData.observeForever { mapState ->
            Timber.d("MapState: %s", mapState.toString())
            handleVehicleData(mapState.vehicleData)
            handleDangerState(mapState.dangerState)
        }

        compositeDisposable += RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribeBy(onNext = {
                    Timber.d("Location permission request granted: %b", it)
                })

        locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val location = intent?.getParcelableExtra<Location?>(LocationService.EXTRA_LOCATION)
                location?.let {
                    viewModel.onNewPosition(location.latitude, location.longitude)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver!!, IntentFilter(LocationService.ACTION_BROADCAST))
    }

    private fun handleVehicleData(vehicleData: VehicleData) {
        if (vehicleData.state != LocationState.INACTIVE) {
            vehicleCircle?.remove()
            val circleOptions = CircleOptions().apply {
                vehicleData.point?.let {
                    this.center(LatLng(it.lat, it.lon))
                }
                vehicleData.dangerRadius?.let {
                    this.radius(it)
                }
                this.fillColor(Color.parseColor("#FFCDD2"))
                this.strokeColor(Color.parseColor("#D50000"))
            }
            vehicleCircle = map?.addCircle(circleOptions)
        }
        start_vehicle.isEnabled = when (vehicleData.state) {
            LocationState.INACTIVE -> map != null
            LocationState.ACTIVE -> false
        }
    }

    private fun handleDangerState(dangerState: DangerState) {
        when (dangerState) {
            DangerState.ENTERED_IN_DANGER -> {
                notificationController.showDangerNotification()
            }
            DangerState.EXIT_FROM_DANGER -> {
                notificationController.removeDangerNotification()
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        trackLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        trackLocation()
        val update = CameraUpdateFactory.newLatLngZoom(LatLng(43.932315, 10.908198), 15.0F)
        googleMap.moveCamera(update)
        start_vehicle.isEnabled = true
        start_vehicle.setOnClickListener { viewModel.onVehicleStart() }
    }

    private fun trackLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
            locationServiceIntent = Intent(this, LocationService::class.java)
            Timber.d("Start location service")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locationServiceIntent)
            } else {
                startService(locationServiceIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        stopService(locationServiceIntent)
        try {
            unregisterReceiver(locationReceiver)
        } catch (e: IllegalArgumentException) {
        }
    }
}