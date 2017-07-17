package com.zen.zfoodie

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

object Client {
	//val dns = "http://192.168.1.3:8080"
	val dns = "http://zenw.ddns.net"

	val client = OkHttpClient.Builder()
		.connectTimeout(90, TimeUnit.SECONDS)
		.readTimeout(90, TimeUnit.SECONDS)
		.writeTimeout(90, TimeUnit.SECONDS)
		.build()
	val mapper = jacksonObjectMapper()

	fun fetchNear(location: Location, baseContext: Context) {
		async(CommonPool) {
			val geocoder = Geocoder(baseContext, Locale.getDefault())
			val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

			val requestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("longitude", location.longitude.toString())
				.addFormDataPart("latitude", location.latitude.toString())
				.addFormDataPart("address", addresses[0].getAddressLine(0))
				.build()
			val request = Request.Builder().url(dns + "/nearby").post(requestBody).build()
			val response = client.newCall(request).execute()
			//Log.d("TEST", response.body()!!.string())
			val list = mapper.readValue<ArrayList<NearBys>>(response.body()!!.string())
			//Log.d("TEST", list.toString())
		}
	}

	fun fetchNearby(location: Location, baseContext: Context) : Deferred<ArrayList<NearBys>> {
		return async(CommonPool) {
			val geocoder = Geocoder(baseContext, Locale.getDefault())
			val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

			val requestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("longitude", location.longitude.toString())
				.addFormDataPart("latitude", location.latitude.toString())
				.addFormDataPart("address", addresses[0].getAddressLine(0))
				.build()
			val request = Request.Builder().url(dns + "/nearby").post(requestBody).build()
			val response = client.newCall(request).execute()
			mapper.readValue<ArrayList<NearBys>>(response.body()!!.string())
		}
	}

	fun uploadImage(image: File, imageName: String, title: String, tags: String, review: String, rating: Float, lg: Double, lt: Double, address: String) : Deferred<Response> {
		return async(CommonPool) {
			val requestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("title", title)
				.addFormDataPart("tags", tags)
				.addFormDataPart("review", review)
				.addFormDataPart("rating", rating.toString())
				.addFormDataPart("longitude", lg.toString())
				.addFormDataPart("latitude", lt.toString())
				.addFormDataPart("address", address)
				.addFormDataPart("image", address.replace(" ", "-").toLowerCase(), RequestBody.create(MediaType.parse("image/jpeg"), image))
				.build()
			val request = Request.Builder().url(dns + "/save-location").post(requestBody).build()
			client.newCall(request).execute()
		}
	}

	fun fetchPlaceDetails(address: String) : Deferred<Place> {
		return async(CommonPool) {
			val requestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("address", address)
				.build()
			val request = Request.Builder().url(dns + "/place").post(requestBody).build()
			mapper.readValue<Place>(client.newCall(request).execute().body()!!.string())
		}
	}

}

data class Place(val title: String, val rating: String, val review: String, val tags: String)
data class NearBys(val longitude: Double, val latitude: Double, val address: String, val distance: Double)