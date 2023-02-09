package com.matamapp.matam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.mediaPlayer.QueueManagement

class QueueAdapter : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.queue_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (CommonData.serviceRunning) {
            holder.title.text = QueueManagement.currentQueue[position].title
            holder.artist.text = QueueManagement.currentQueue[position].artist.name

            val trackImage = if (QueueManagement.currentQueue[position].trackImage != "null") {
                QueueManagement.currentQueue[position].trackImage
            } else {
                QueueManagement.currentQueue[position].artist.image
            }

            Glide.with(holder.image.context).load(trackImage).into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return QueueManagement.currentQueue.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.queue_track_title)
        val artist: TextView = itemView.findViewById(R.id.queue_track_artist)
        val image: ImageView = itemView.findViewById(R.id.queue_track_image)
        val cardView: CardView = itemView.findViewById(R.id.queue_item)
    }
}