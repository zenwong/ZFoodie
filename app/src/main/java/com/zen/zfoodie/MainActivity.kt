package com.zen.zfoodie

import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity(), OnSuccessListener<Location> {

	override fun onResume() {
		super.onResume()
		val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)
		fusedClient.lastLocation.addOnSuccessListener(this@MainActivity)
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
