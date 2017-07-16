package com.zen.zfoodie

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_nearby.view.*

class NearbyAdapter(val nearby: ArrayList<NearBys>) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {

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

	class ViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {

		fun bind(nearby: NearBys) {
			itemView.tvNearName.text = nearby.address
			itemView.tvNearDistance.text = nearby.distance.toString() + " km"
		}


	}
}