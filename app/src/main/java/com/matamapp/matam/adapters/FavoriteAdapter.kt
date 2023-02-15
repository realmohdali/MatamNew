package com.matamapp.matam.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.CHANGE_TRACK
import com.matamapp.matam.mediaPlayer.MediaPlayerService
import com.matamapp.matam.mediaPlayer.QueueManagement
import com.matamapp.matam.data.TrackData

class FavoriteAdapter(val context: Context, private val favoriteList: MutableList<TrackData>) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trackImage = if (favoriteList[position].trackImage != "null") {
            favoriteList[position].trackImage
        } else {
            favoriteList[position].artist.image
        }

        holder.title.text = favoriteList[position].title
        holder.artist.text = favoriteList[position].title

        Glide.with(context)
            .load(trackImage)
            .into(holder.image)

        holder.card.setOnClickListener {
            if (!CommonData.serviceRunning) {
                QueueManagement.currentQueue = favoriteList
                QueueManagement.currentPosition = position
                context.startService(Intent(context, MediaPlayerService::class.java))
            } else if (!QueueManagement.existsInQueue(favoriteList[position])) {
                QueueManagement.currentQueue = favoriteList
                QueueManagement.currentPosition = position
                localBroadcastManager.sendBroadcast(Intent(CHANGE_TRACK))
            } else {
                if (position != QueueManagement.currentPosition) {
                    QueueManagement.currentPosition = position
                    localBroadcastManager.sendBroadcast(Intent(CHANGE_TRACK))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.favorite_track_title)
        val artist: TextView = itemView.findViewById(R.id.favorite_track_artist)
        val image: ImageView = itemView.findViewById(R.id.favorite_track_image)
        val card: CardView = itemView.findViewById(R.id.favorite_item)
    }
}