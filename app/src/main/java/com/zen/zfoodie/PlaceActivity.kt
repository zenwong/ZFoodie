package com.zen.zfoodie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.save_location.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class PlaceActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.save_location)
		val address = intent.extras.getString("address")
		supportActionBar!!.title = address
		//window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

		launch(UI) {
			val place = Client.fetchPlaceDetails(address).await()

			tvTitle.setText(place.title)
			autoTags.setText(place.tags)
			editAddress.setText(address)
			ratingsStar.rating = place.rating.toFloat()
			editReview.setText(place.review)

			val url = Client.dns + "/" + address.replace(" ", "-").toLowerCase() + ".jpg"
			Picasso.with(baseContext).load(url).fit().centerCrop().into(ivMainProfile)
			tvAddPic.visibility = View.GONE
			ivMainProfile.visibility = View.VISIBLE
			btnSave.visibility = View.GONE
		}
	}

}