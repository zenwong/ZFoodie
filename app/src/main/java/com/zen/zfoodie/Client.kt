package com.zen.zfoodie

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.*
import java.io.File
import java.util.concurrent.TimeUnit

object Client {
	val client = OkHttpClient.Builder()
		.connectTimeout(90, TimeUnit.SECONDS)
		.readTimeout(90, TimeUnit.SECONDS)
		.writeTimeout(90, TimeUnit.SECONDS)
		.build()
	val mapper = jacksonObjectMapper()

	fun fetchPosts() : Deferred<List<Posts>> {
		return async(CommonPool) {
			val request = Request.Builder().url("https://jsonplaceholder.typicode.com/photos").build()
			val response =  client.newCall(request).execute().body()!!.string()
			mapper.readValue<ArrayList<Posts>>(response)
		}
	}

	fun fetchNearBy() : Deferred<List<NearBys>> {
		return async(CommonPool) {
			val request = Request.Builder().url("https://jsonplaceholder.typicode.com/posts").build()
			val response =  client.newCall(request).execute().body()!!.string()
			mapper.readValue<ArrayList<NearBys>>(response)
		}
	}

	fun uploadImage(image: File, imageName: String, title: String, tags: String, review: String, rating: Float) : Deferred<Response> {
		return async(CommonPool) {
			val requestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("title", title)
				.addFormDataPart("tags", tags)
				.addFormDataPart("review", review)
				.addFormDataPart("rating", rating.toString())
				.addFormDataPart("image", imageName, RequestBody.create(MediaType.parse("image/jpeg"), image))
				.build()
			val request = Request.Builder().url("http://192.168.1.15:8080/save-location").post(requestBody).build()
			client.newCall(request).execute()
		}
	}

}

data class NearBys(val name: String, val longitude: Double, val latitude: Double)
data class Posts(val albumId: Int, val id: Int, val title: String, val url: String, val thumbnailUrl: String)