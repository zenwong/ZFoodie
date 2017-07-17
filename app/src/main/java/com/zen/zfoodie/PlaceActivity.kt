package com.zen.zfoodie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class PlaceActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val address = intent.extras.getString("address")
		supportActionBar!!.title = address

		launch(UI) {
			Client.fetchPlaceDetails(address)
		}
	}

}