package com.matamapp.matam.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.data.TrackData
import com.matamapp.matam.db.FavoriteData
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.CHANGE_TRACK
import com.matamapp.matam.mediaPlayer.MediaPlayerService
import com.matamapp.matam.mediaPlayer.QueueManagement

class AlbumAdapter(val context: Context, private val trackList: MutableList<TrackData>) :
    RecyclerView.Adapter<AlbumAdapter.Holder>() {

    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val trackImage = if (trackList[position].trackImage != "null") {
            trackList[position].trackImage
        } else {
            trackList[position].artist.image
        }

        holder.title.text = trackList[position].title
        holder.artistName.text = trackList[position].artist.name

        Glide.with(context)
            .load(trackImage)
            .into(holder.trackArt)

        holder.card.setOnClickListener {
            if (!CommonData.serviceRunning) {
                QueueManagement.currentQueue = trackList
                QueueManagement.currentPosition = position
                context.startService(Intent(context, MediaPlayerService::class.java))
            } else if (!QueueManagement.existsInQueue(trackList[position])) {
                QueueManagement.currentQueue = trackList
                QueueManagement.currentPosition = position
                //Ask player to reset and play new queue
                localBroadcastManager.sendBroadcast(Intent(CHANGE_TRACK))
            } else {
                if (position != QueueManagement.currentPosition) {
                    QueueManagement.currentPosition = position
                    //Ask player to change position and play
                    localBroadcastManager.sendBroadcast(Intent(CHANGE_TRACK))
                }
            }
        }

        if (CommonData.existsInFavorite(context, trackList[position].id)) {
            holder.addToFav.setImageResource(R.drawable.ic_baseline_favorite_24_green)
        } else {
            holder.addToFav.setImageResource(R.drawable.ic_baseline_favorite_24)
        }

        holder.addToFav.setOnClickListener {
            if (CommonData.existsInFavorite(context, trackList[position].id)) {
                holder.addToFav.setImageResource(R.drawable.ic_baseline_favorite_24)
                CommonData.removeFavorite(context, trackList[position].id)
                notifyItemChanged(position)
            } else {
                holder.addToFav.setImageResource(R.drawable.ic_baseline_favorite_24_green)
                val favoriteData = FavoriteData(
                    trackList[position].id,
                    trackList[position].title,
                    trackList[position].trackURl,
                    trackImage,
                    trackList[position].artist.id,
                    trackList[position].artist.name,
                    trackList[position].artist.image,
                    trackList[position].artist.nationality,
                    trackList[position].year.yearAD,
                    trackList[position].year.yearHijri
                )

                CommonData.addFavorite(context, favoriteData)
                notifyItemChanged(position)
            }
        }

        holder.addToQueue.setOnClickListener {
            if (CommonData.serviceRunning) {
                QueueManagement.addToQueue(trackList[position])
            } else {
                Toast.makeText(context, "Player is not running", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.favorite_track_title)
        val card: CardView = itemView.findViewById(R.id.album_list_item)
        val addToQueue: ImageView = itemView.findViewById(R.id.add_to_playlist)
        val artistName: TextView = itemView.findViewById(R.id.favorite_track_artist)
        val trackArt: ImageView = itemView.findViewById(R.id.favorite_track_image)
        val addToFav: ImageButton = itemView.findViewById(R.id.add_to_fav)
    }
}