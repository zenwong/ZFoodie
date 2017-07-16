package com.zen.zfoodie

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.save_location.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SaveLocationActivity : AppCompatActivity(), OnSuccessListener<Location> {
	private val REQUEST_TAKE_PHOTO = 1
	private var imgPath: String? = null
	private var imgUri: Uri? = null
	private var lg: Double? = null
	private var lt: Double? = null
	private var address: String? = null

	override fun onSuccess(location: Location) {
		val geocoder = Geocoder(baseContext, Locale.getDefault())
		val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
		editAddress.setText(addresses[0].getAddressLine(0))
		lg = location.longitude
		lt = location.latitude
		address = addresses[0].getAddressLine(0)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.save_location)

		val fusedClient = LocationServices.getFusedLocationProviderClient(baseContext)
		fusedClient.lastLocation.addOnSuccessListener(this@SaveLocationActivity)

		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
		supportActionBar?.let { title = "Save Location" }

		tvAddPic.setOnClickListener {
			takePicIntent()
		}

		btnSave.setOnClickListener {
			launch(UI) {
				Client.uploadImage(File(imgPath), "test.jpg",
					tvTitle.text.toString(), autoTags.text.toString(),
					editReview.text.toString(), ratingsStar.rating, lg!!, lt!!, address!!).await()
			}
		}
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
		Picasso.with(baseContext).load(imgUri).fit().centerCrop().into(ivMainProfile)
		tvAddPic.visibility = View.GONE
		ivMainProfile.visibility = View.VISIBLE
	}

}