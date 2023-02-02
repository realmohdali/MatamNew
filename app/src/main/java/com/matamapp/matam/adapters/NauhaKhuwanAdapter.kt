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
import com.matamapp.matam.AlbumListActivity
import com.matamapp.matam.R
import com.matamapp.matam.data.ArtistData

class NauhaKhuwanAdapter(private var context: Context, private var artists: MutableList<ArtistData>) :
    RecyclerView.Adapter<NauhaKhuwanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.nauha_khuwan_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            val intent: Intent = Intent(context, AlbumListActivity::class.java)
            intent.putExtra("caller", "artist")
            intent.putExtra("id", artists[position].id)
            context.startActivity(intent)
        }
        val name = artists[position].name
        holder.artistName.text = name
        val url = artists[position].image
        Glide
            .with(context)
            .load(url)
            .into(holder.artistImage)
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.nauha_khuwan_card_view)
        val artistName: TextView = itemView.findViewById(R.id.nauha_khuwan_name)
        val artistImage: ImageView = itemView.findViewById(R.id.nauha_khuwan_image)
    }
}