package com.matamapp.matam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.R
import com.matamapp.matam.adapters.NauhaKhuwanAdapter

class HomeFragment : Fragment() {

    private lateinit var xmlView: View
    private lateinit var newReleaseRecyclerView: RecyclerView
    private lateinit var trendingRecyclerView: RecyclerView
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var nauhaKhuwanAdapter: NauhaKhuwanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        xmlView = inflater.inflate(R.layout.fragment_home, container, false)
        newReleaseRecyclerView = xmlView.findViewById(R.id.new_release_recyclerview)
        trendingRecyclerView = xmlView.findViewById(R.id.trending_recycler_view)
        popularRecyclerView = xmlView.findViewById(R.id.popular_recyclerview)

        newReleaseRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        trendingRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        popularRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //nauhaKhuwanAdapter = NauhaKhuwanAdapter(xmlView.context)

        return xmlView
    }

    override fun onResume() {
        super.onResume()
        //newReleaseRecyclerView.adapter = nauhaKhuwanAdapter
        //trendingRecyclerView.adapter = nauhaKhuwanAdapter
        //popularRecyclerView.adapter = nauhaKhuwanAdapter
    }

    companion object
}