package com.matamapp.matam.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.AlbumListActivity
import com.matamapp.matam.R
import com.matamapp.matam.data.YearData

class YearsAdapter(private val context: Context, private val years: MutableList<YearData>) :
    RecyclerView.Adapter<YearsAdapter.ViewHolder>() {
    private lateinit var activityContext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.year_item, parent, false)
        activityContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.yearName.text = years[position].yearHijri
        holder.yearCardView.setOnClickListener {
            val intent = Intent(context, AlbumListActivity::class.java)
            intent.putExtra("caller", "year")
            intent.putExtra("id", years[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return years.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val yearName: TextView = itemView.findViewById(R.id.year_name)
        val yearCardView: CardView = itemView.findViewById(R.id.year_card_view)
    }
}