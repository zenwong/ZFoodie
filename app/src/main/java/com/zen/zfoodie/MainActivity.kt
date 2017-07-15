package com.zen.zfoodie

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.actionbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.add_location ->
		{
			startActivity(Intent(baseContext, SaveLocationActivity::class.java))
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onResume() {
		super.onResume()

		launch(UI) {
			try {

				val posts = Client.fetchPosts().await()
				for (post in posts) {
					//Log.d("TEST", post.toString())
				}

			} catch(ex: IOException) {
				Toast.makeText(this@MainActivity, "Phone not connected or service down", Toast.LENGTH_SHORT).show()
			}
		}
	}
}
