package com.zen.zfoodie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class PlaceActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val address = intent.extras.getString("address")
		supportActionBar!!.title = address
	}

}