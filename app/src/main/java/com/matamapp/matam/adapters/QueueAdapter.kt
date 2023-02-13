package com.matamapp.matam.adapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.fragments.MediaPlayerFragment
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.CHANGE_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAYER_PREPARED
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_UPDATE
import com.matamapp.matam.mediaPlayer.QueueManagement

class QueueAdapter(val context: Context) : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

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

            holder.cardView.setOnClickListener {
                if (position != QueueManagement.currentPosition) {
                    //notifyItemChanged(QueueManagement.currentPosition)
                    val oldPosition = QueueManagement.currentPosition
                    QueueManagement.currentPosition = position
                    LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(Intent(CHANGE_TRACK))
                    notifyItemChanged(position)
                    notifyItemChanged(oldPosition)
                }
            }

            if (QueueManagement.currentPosition == position) {
                holder.progressBar.visibility = View.VISIBLE
                holder.progressBar.max = MediaPlayerFragment.duration

                val seekUpdateListener = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent != null) {
                            holder.progressBar.progress = intent.getIntExtra("current_position", 0)
                        }
                    }
                }
                LocalBroadcastManager.getInstance(context)
                    .registerReceiver(seekUpdateListener, IntentFilter(SEEK_UPDATE))

                val playerPreparedListener = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        holder.progressBar.max = MediaPlayerFragment.duration
                        notifyItemChanged(QueueManagement.currentPosition)
                    }
                }
                LocalBroadcastManager.getInstance(context)
                    .registerReceiver(playerPreparedListener, IntentFilter(PLAYER_PREPARED))
            }
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
        val progressBar: ProgressBar = itemView.findViewById(R.id.queue_progress_bar)
    }
}