package com.matamapp.matam.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.AlbumListActivity
import com.matamapp.matam.R

class NauhaKhuwanAdapter(private var context: Context) :
    RecyclerView.Adapter<NauhaKhuwanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.nauha_khuwan_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            context.startActivity(Intent(context, AlbumListActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return 20
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<CardView>(R.id.nauha_khuwan_card_view)
    }
}