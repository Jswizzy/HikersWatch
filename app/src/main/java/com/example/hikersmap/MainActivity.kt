package com.example.hikersmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        geocoder = Geocoder(this, Locale.getDefault())

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationListener = MyLocationListener()
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L * 60L * 5L, 20f, locationListener)
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                updateLocation(lastKnownLocation)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocation(location: Location) {
        Log.i("Location", location.toString())

        with(location) {
            val df = DecimalFormat("#.##")
            longTextView.text = "Longitude: ${df.format(longitude)}"
            latTextView.text = "Latitude: ${df.format(latitude)}"
            accuracyTextView.text = "Accuracy: ${df.format(accuracy)}"
            altTextView.text = "Altitude: ${df.format(altitude)}"

            var address: String = ""
            try {
                val listAddress = geocoder.getFromLocation(latitude, longitude, 1)
                address = StringBuilder().apply {
                    with(listAddress.first()) {
                        appendln(thoroughfare ?: "")
                        appendln(locality ?: "")
                        appendln(postalCode ?: "")
                        appendln(adminArea ?: "")
                    }
                }.toString()
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "Cannot Find Address", Toast.LENGTH_SHORT).show()
            }

            addressTextView.text = "Address:\n$address"
        }
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L * 60L * 5L, 20f, locationListener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            startListening()
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateLocation(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

    }
}
