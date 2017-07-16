package com.zen.zfoodie

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.experimental.CommonPool
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

	override fun onResume() {
		super.onResume()

		val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)
		fusedClient.lastLocation.addOnSuccessListener(this@MainActivity)

		launch(UI) {
			try {
				//Client.fetchNearby(baseContext)

			} catch(ex: IOException) {
				Toast.makeText(this@MainActivity, "Phone not connected or service down", Toast.LENGTH_SHORT).show()
			}
		}
	}

	override fun onSuccess(location: Location) {
		launch(UI) {
			Client.fetchNear(location, baseContext)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.actionbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.add_location -> {
			startActivity(Intent(baseContext, SaveLocationActivity::class.java))
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

}
