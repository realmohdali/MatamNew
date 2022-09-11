package com.matamapp.matam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.R

class YearsAdapter : RecyclerView.Adapter<YearsAdapter.ViewHolder>() {

    var array = arrayOf(
        "2022",
        "2021",
        "2020",
        "2019",
        "2018",
        "2017",
        "2016",
        "2015",
        "2014",
        "2013",
        "2012",
        "2011",
        "2010",
        "2009",
        "2008",
        "2007",
        "2006",
        "2005",
        "2004",
        "2003",
        "2002",
        "2001",
        "2000"
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.year_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.yearName.text = array[position]
    }

    override fun getItemCount(): Int {
        return array.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val yearName = itemView.findViewById<TextView>(R.id.year_name)
    }
}