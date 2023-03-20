package com.matamapp.matam.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.matamapp.matam.AlbumActivity
import com.matamapp.matam.R
import com.matamapp.matam.data.AlbumData

class AlbumListAdapter(
    private var context: Context,
    private var albumList: MutableList<AlbumData>
) : RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.album_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            val intent = Intent(context, AlbumActivity::class.java)
            intent.putExtra("album_id", albumList[position].id)
            intent.putExtra("album_name", albumList[position].name)
            context.startActivity(intent)
        }
        val name = albumList[position].name
        holder.albumName.text = name
        if (albumList[position].cover != "null") {
            Glide.with(holder.albumArt.context)
                .load(albumList[position].cover)
                .into(holder.albumArt)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.album_list_item)
        val albumName: TextView = itemView.findViewById(R.id.album_name)
        val albumArt: ImageView = itemView.findViewById(R.id.album_art)
    }

}