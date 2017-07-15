package com.zen.zfoodie

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.design.widget.Snackbar
import android.view.View
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SaveLocationActivity : AppCompatActivity() {
	private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
	private val REQUEST_TAKE_PHOTO = 1
	private var imgPath : String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.save_location)

		supportActionBar?.let { title = "Save Location" }

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
			if(!checkPermissions()) {
				requestPermissions()
			} else {
				getAddress()
			}
		}

	}

	@SuppressLint("MissingPermission")
	fun getAddress() {
		val fusedClient = LocationServices.getFusedLocationProviderClient(this)
		fusedClient.lastLocation.addOnSuccessListener(object : OnSuccessListener<Location> {
			override fun onSuccess(location: Location?) {
				location.let {
					Log.d("TEST", "location $location.toString()")
				}
			}
		})
	}

	fun checkPermissions() : Boolean {
		val permissionState = ActivityCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION)
		return permissionState == PackageManager.PERMISSION_GRANTED

	}

	fun requestPermissions() {
		val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
			Manifest.permission.ACCESS_FINE_LOCATION)

		// Provide an additional rationale to the user. This would happen if the user denied the
		// request previously, but didn't check the "Don't ask again" checkbox.
		if (shouldProvideRationale) {
			Log.i("TEST", "Displaying permission rationale to provide additional context.")

			Snackbar.make(findViewById(android.R.id.content), "Request Permissions", Snackbar.LENGTH_LONG).show();

		} else {
			Log.i("TEST", "Requesting permission")
			// Request permission. It's possible this can be auto answered if device policy
			// sets the permission in a given state or the user denied the permission
			// previously and checked "Never ask again".
			ActivityCompat.requestPermissions(this@SaveLocationActivity,
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				REQUEST_PERMISSIONS_REQUEST_CODE)
		}
	}

	fun createImageFile(): File? {
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
		val imageFileName = "JPEG_" + timeStamp + "_"
		val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		val image = File.createTempFile(imageFileName, ".jpg", storageDir)

		// Save a file: path for use with ACTION_VIEW intents
		imgPath = image.absolutePath
		return image
	}

	fun dispatchTakePictureIntent() {
		val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(packageManager) != null) {
			// Create the File where the photo should go
			val photoFile = createImageFile()
			// Continue only if the File was successfully created
			if (photoFile != null) {
				val photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile)
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	private fun galleryAddPic() {
		val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
		val f = File(imgPath)
		val contentUri = Uri.fromFile(f)
		mediaScanIntent.data = contentUri
		this.sendBroadcast(mediaScanIntent)
	}

}