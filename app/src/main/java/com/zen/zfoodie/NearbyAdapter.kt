package com.zen.zfoodie

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_nearby.view.*
import java.text.DecimalFormat

class NearbyAdapter(val context: Context, val nearby: ArrayList<NearBys>) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.row_nearby, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount(): Int {
		return nearby.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(nearby[position])
	}

	inner class ViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {

		fun bind(nearby: NearBys) {
			val df = DecimalFormat("###.##")
			itemView.tvNearName.text = nearby.address
			itemView.tvNearDistance.text = df.format(nearby.distance) + " km"
			val url = Client.dns + "/" + nearby.address.replace(" ", "-").toLowerCase() + ".jpg"
			//Log.d("TEST", url)
			Picasso.with(context).load(url).fit().centerCrop().into(itemView.imgPreview)

			itemView.imgPreview.setOnClickListener {
				val intent = Intent(context, PlaceActivity::class.java)
				intent.putExtra("address", nearby.address)
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
				context.startActivity(intent)
			}
		}


	}
}