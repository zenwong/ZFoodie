package com.zen.zfoodie

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request

object Client {
	val client = OkHttpClient()
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
}

data class NearBys(val name: String, val longitude: Double, val latitude: Double)
data class Posts(val albumId: Int, val id: Int, val title: String, val url: String, val thumbnailUrl: String)