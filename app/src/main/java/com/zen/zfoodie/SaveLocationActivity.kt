package com.zen.zfoodie

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.save_location.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SaveLocationActivity : AppCompatActivity() {
	private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
	private val REQUEST_TAKE_PHOTO = 1
	private var imgPath: String? = null
	private var imgUri: Uri? = null
	private var lg: Double? = null
	private var lt: Double? = null
	private var address: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.save_location)
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
		supportActionBar?.let { title = "Save Location" }

		getAddress()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!checkPermissions()) {
				requestPermissions()
				Log.d("TEST", "requesting permissions")
			} else {
				Log.d("TEST", "get address")
				getAddress()
			}
		}

		tvAddPic.setOnClickListener {
			//dispatchTakePictureIntent()
			testIntent()
		}

		btnSave.setOnClickListener {
			launch(UI) {

				try {
					val resp = Client.uploadImage(File(imgPath), "test.jpg",
						tvTitle.text.toString(), autoTags.text.toString(),
						editReview.text.toString(), ratingsStar.rating, lg!!, lt!!, address!!).await()

					Log.d("TEST", "upload code: $resp.code()")

				} catch(ex: IOException) {
					Log.d("TEST", ex.printStackTrace().toString())
				}

			}


		}


	}


	@SuppressLint("MissingPermission")
	fun getAddress() {
		val fusedClient = LocationServices.getFusedLocationProviderClient(this)
		fusedClient.lastLocation.addOnSuccessListener(object : OnSuccessListener<Location> {
			override fun onSuccess(location: Location?) {
				if (location != null) {
					lg = location.longitude
					lt = location.latitude
					val geocoder = Geocoder(baseContext, Locale.getDefault())
					val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
					Log.d("TEST", "location $location.toString()")

					address = addresses[0].getAddressLine(0)
					editAddress.setText(addresses[0].getAddressLine(0))

					if (addresses == null || addresses.size == 0) {
						Log.e("TEST", "no address found")
					} else {
						val address = addresses[0]
						val addressFragments = ArrayList<String>()
						for(i in 1 .. address.maxAddressLineIndex) {
							addressFragments.add(address.getAddressLine(i))
							Log.d("TEST", address.getAddressLine(i))
						}

					}
				}
			}
		})
	}

	fun checkPermissions(): Boolean {
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

	fun testIntent() {
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		val imageFile = createImageFile()
		imgPath = imageFile!!.absolutePath
		imgUri = Uri.fromFile(imageFile)

		intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
		startActivityForResult(intent, REQUEST_TAKE_PHOTO)
	}

	fun dispatchTakePictureIntent() {
		val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(packageManager) != null) {
			// Create the File where the photo should go
			val photoFile = createImageFile()
			// Continue only if the File was successfully created
			if (photoFile != null) {
				val photoURI = FileProvider.getUriForFile(this, "com.zen.zfoodie.provider", photoFile)
				val resInfoList = baseContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
				for (resolveInfo in resInfoList) {
					val packageName = resolveInfo.activityInfo.packageName
					baseContext.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
				}
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		Log.d("TEST", "resultCode = $resultCode requestCode = $requestCode")
		if (requestCode === REQUEST_TAKE_PHOTO && resultCode === Activity.RESULT_OK) {
			try {
				//val mImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(imgPath))
				//ivMainProfile.setImageBitmap(mImageBitmap)

				Picasso.with(baseContext).load(imgUri).fit().centerCrop().into(ivMainProfile)

				tvAddPic.visibility = View.GONE
				ivMainProfile.visibility = View.VISIBLE
			} catch (e: IOException) {
				e.printStackTrace()
			}

		}

//		tvAddPic.visibility = View.GONE
//		ivMainProfile.visibility  = View.VISIBLE
//		ivMainProfile.setImageURI(imgUri)
//
//		if(data != null) {
//			val photo = data.extras.get("data") as Bitmap
//			ivMainProfile.setImageBitmap(photo)
//		}
//
//		Log.d("TEST", imgUri.toString())
		//Picasso.with(baseContext).load(imgUri).into(ivMainProfile)
	}

	private fun galleryAddPic() {
		val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
		val f = File(imgPath)
		val contentUri = Uri.fromFile(f)
		mediaScanIntent.data = contentUri
		this.sendBroadcast(mediaScanIntent)
	}

	fun setPic() {
		// Get the dimensions of the View
		val targetW = ivMainProfile.getWidth()
		val targetH = ivMainProfile.getHeight()

		// Get the dimensions of the bitmap
		val bmOptions = BitmapFactory.Options()
		bmOptions.inJustDecodeBounds = true
		BitmapFactory.decodeFile(imgPath, bmOptions)
		val photoW = bmOptions.outWidth
		val photoH = bmOptions.outHeight

		// Determine how much to scale down the image
		val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false
		bmOptions.inSampleSize = scaleFactor
		bmOptions.inPurgeable = true

		val bitmap = BitmapFactory.decodeFile(imgPath, bmOptions)
		ivMainProfile.setImageBitmap(bitmap)
	}

}