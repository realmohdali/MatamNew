package com.matamapp.matam.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.R
import com.matamapp.matam.data.TrackData

class AlbumAdapter(val context: Context, private val trackList: MutableList<TrackData>) : RecyclerView.Adapter<AlbumAdapter.vHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return vHolder(view)
    }

    override fun onBindViewHolder(holder: vHolder, position: Int) {
        val title = trackList[position].title
        holder.title.text = title
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    class vHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.album_title)
    }
}