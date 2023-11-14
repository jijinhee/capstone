package com.example.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.json.JSONObject

class ListAdapter(private val context: Context, private val stationList: List<JSONObject>) : BaseAdapter() {

    override fun getCount(): Int {
        return stationList.size
    }

    override fun getItem(position: Int): Any {
        return stationList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val station = stationList[position]
        val stationName = station.getString("stationName")
        val distance = station.getString("distance")
        val direction = station.getString("direction")

        holder.stationNameTextView.text = "정류소명: $stationName"
        holder.distanceTextView.text = "거리: $distance"
        holder.directionTextView.text = "방면: $direction"


        return view!!
    }

    private class ViewHolder(view: View) {
        val directionTextView: TextView = view.findViewById(R.id.directionTextView)
        val stationNameTextView: TextView = view.findViewById(R.id.stationNameTextView)
        val distanceTextView: TextView = view.findViewById(R.id.distanceTextView)
    }
}