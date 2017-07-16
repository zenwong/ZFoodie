package com.zen.zfoodie

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.recycler.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity(), OnSuccessListener<Location> {
	var loc: Location? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.recycler)
	}

	override fun onResume() {
		super.onResume()
		val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)
		fusedClient.lastLocation.addOnSuccessListener(this@MainActivity)
	}

	override fun onSuccess(location: Location) {
		loc = location
		launch(UI) {
			val nearbys = Client.fetchNearby(location, baseContext).await()
			val adapter = NearbyAdapter(baseContext, nearbys)
			val layoutManager = LinearLayoutManager(baseContext)
			layoutManager.orientation = LinearLayoutManager.VERTICAL
			rv.layoutManager = layoutManager
			rv.adapter = adapter
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
		R.id.menu_search -> {
			launch(UI) {
				Client.fetchNear(loc!!, baseContext)
			}
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

}
