package com.matamapp.matam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.R

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.vHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return vHolder(view)
    }

    override fun onBindViewHolder(holder: vHolder, position: Int) {
        val pos = position + 1
        holder.title.text = (pos).toString()
    }

    override fun getItemCount(): Int {
        return 20
    }

    class vHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.track_title)
    }
}