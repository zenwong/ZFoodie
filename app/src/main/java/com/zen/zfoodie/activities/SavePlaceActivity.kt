package com.zen.zfoodie.activities

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.location.LocationRequest
import com.squareup.picasso.Picasso
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.base.LocationBaseActivity
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.constants.ProviderType
import com.zen.zfoodie.Client
import com.zen.zfoodie.MainActivity
import com.zen.zfoodie.R
import kotlinx.android.synthetic.main.save_location.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SavePlaceActivity : LocationBaseActivity() , LocationListener {
	private val REQUEST_TAKE_PHOTO = 1
	private var imgPath: String? = null
	private var imgUri: Uri? = null
	private var lg: Double? = null
	private var lt: Double? = null
	private var address: String? = null
	private val INTERVAL = (1000 * 10).toLong()
	private val FASTEST_INTERVAL = (1000 * 5).toLong()
	var counter = 0
	val locationRequest = LocationRequest()
	val locationConfig = LocationConfiguration.Builder()
		.keepTracking(true)
		.useGooglePlayServices(GooglePlayServicesConfiguration.Builder()
			.locationRequest(locationRequest)
			.fallbackToDefault(true)
			.askForGooglePlayServices(false)
			.askForSettingsApi(true)
			.failOnConnectionSuspended(true)
			.failOnSettingsApiSuspended(false)
			.ignoreLastKnowLocation(false)
			.setWaitPeriod((20 * 1000).toLong())
			.build())
		.useDefaultProviders(DefaultProviderConfiguration.Builder()
			.requiredTimeInterval((5 * 60 * 1000).toLong())
			.requiredDistanceInterval(0)
			.acceptableAccuracy(5.0f)
			.acceptableTimePeriod((5 * 60 * 1000).toLong())
			.gpsMessage("Turn on GPS?")
			.setWaitPeriod(ProviderType.GPS, (20 * 1000).toLong())
			.setWaitPeriod(ProviderType.NETWORK, (20 * 1000).toLong())
			.build())
		.build()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.save_location)
		//getLocation()

		locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		locationRequest.interval = 5000
		locationRequest.fastestInterval = 2000
		locationRequest.smallestDisplacement = 1F

		val locationManager = LocationManager.Builder(applicationContext)
			.activity(this@SavePlaceActivity)
			.configuration(locationConfig)
			//.locationProvider(new YourCustomLocationProvider())
			.notify(this@SavePlaceActivity)
			.build()

		locationManager.get()

		supportActionBar?.let { title = "Save Location" }

		tvAddPic.setOnClickListener {
			takePicIntent()
		}

		btnSave.setOnClickListener {
			launch(UI) {
				try {
					if(imgPath != null) {

						try {
							val resp = Client.uploadImage(File(imgPath), "test.jpg",
								tvTitle.text.toString(), autoTags.text.toString(),
								editReview.text.toString(), ratingsStar.rating, lg!!, lt!!, address!!).await()

							if(resp.isSuccessful) {
								Snackbar.make(linearLayout, "Saved", Snackbar.LENGTH_LONG).show()
								val intent = Intent(baseContext, MainActivity::class.java)
								startActivity(intent)
							}
						} catch (ex: Exception) {
							Snackbar.make(linearLayout, ex.printStackTrace().toString(), Snackbar.LENGTH_LONG).show()
						}


					} else {
						Snackbar.make(linearLayout, "Take a pic", Snackbar.LENGTH_LONG).show()
					}


				} catch(ex: IOException) {
					Snackbar.make(linearLayout, "Network Error", Snackbar.LENGTH_LONG).show()
				}

			}
		}
	}

	override fun onLocationChanged(location: Location) {
		counter++
		editAddress.setText(location.toString())
		val geocoder = Geocoder(baseContext, Locale.getDefault())

		try {
			val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
			address = addresses[0].getAddressLine(0)
			editAddress.setText(address)
			val concat = location.longitude.toString() + " : " + location.latitude.toString()
			tvCoord.text = concat
			lg = location.longitude
			lt = location.latitude
			//tvTitle.setText(counter.toString())
			Log.d("TEST", "longitude: $lg latitude: $lt")
		} catch(ex: Exception) {

		}


	}

	override fun onLocationFailed(type: Int) {
	}

	override fun getLocationConfiguration(): LocationConfiguration {
		return locationConfig
	}

	fun createImageFile(): File? {
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
		val imageFileName = "JPEG_" + timeStamp + "_"
		val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		val image = File.createTempFile(imageFileName, ".jpg", storageDir)
		imgPath = image.absolutePath
		return image
	}

	fun takePicIntent() {
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		val imageFile = createImageFile()
		imgPath = imageFile!!.absolutePath
		imgUri = Uri.fromFile(imageFile)

		intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
		startActivityForResult(intent, REQUEST_TAKE_PHOTO)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		Picasso.with(baseContext).load(imgUri).fit().centerCrop().into(ivMainProfile)
		tvAddPic.visibility = View.GONE
		ivMainProfile.visibility = View.VISIBLE
	}


	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_save, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.menu_update -> {

			true
		}
		else -> super.onOptionsItemSelected(item)
	}

}