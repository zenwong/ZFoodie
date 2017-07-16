package com.zen.zfoodie

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnSuccessListener<Location> {
	override fun onSuccess(location: Location) {
		val geocoder = Geocoder(baseContext, Locale.getDefault())
		val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
		fetchNear(location)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)
		fusedClient.lastLocation.addOnSuccessListener(this@MainActivity)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.actionbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.add_location ->
		{
			startActivity(Intent(baseContext, SaveLocationActivity::class.java))
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onResume() {
		super.onResume()

		launch(UI) {
			try {
				//Client.fetchNearby(baseContext)

			} catch(ex: IOException) {
				Toast.makeText(this@MainActivity, "Phone not connected or service down", Toast.LENGTH_SHORT).show()
			}
		}
	}

	@SuppressLint("MissingPermission")
	fun fetchLocation(): Deferred<Location?> {
		return async(CommonPool) {
			val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)
			var loc: Location? = null
			fusedClient.lastLocation.addOnSuccessListener { location ->
				loc = location
				Log.d("TEST", "location: $location")
			}
			loc
		}
	}

	fun test() {
		async(CommonPool) {
			val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)

			fusedClient.lastLocation.addOnSuccessListener { location ->
				val geocoder = Geocoder(baseContext, Locale.getDefault())
				val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

				val requestBody = MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("longitude", location.longitude.toString())
					.addFormDataPart("latitude", location.latitude.toString())
					.addFormDataPart("address", addresses[0].getAddressLine(0))
					.build()
				val request = Request.Builder().url("http://192.168.1.15:8080/nearby").post(requestBody).build()
				val client = OkHttpClient.Builder()
					.connectTimeout(90, TimeUnit.SECONDS)
					.readTimeout(90, TimeUnit.SECONDS)
					.writeTimeout(90, TimeUnit.SECONDS)
					.build()
				client.newCall(request).execute().body()!!.string()
			}
		}
	}

	fun fetchNear(location: Location) {
		async(CommonPool) {
			val geocoder = Geocoder(baseContext, Locale.getDefault())
			val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

			val requestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("longitude", location.longitude.toString())
				.addFormDataPart("latitude", location.latitude.toString())
				.addFormDataPart("address", addresses[0].getAddressLine(0))
				.build()
			val request = Request.Builder().url("http://192.168.1.15:8080/nearby").post(requestBody).build()
			val client = OkHttpClient.Builder()
				.connectTimeout(90, TimeUnit.SECONDS)
				.readTimeout(90, TimeUnit.SECONDS)
				.writeTimeout(90, TimeUnit.SECONDS)
				.build()
			Log.d("TEST", client.newCall(request).execute().body()!!.string())
		}
	}
}
