package com.matamapp.matam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.matamapp.matam.R
import com.matamapp.matam.adapters.NauhaKhuwanAdapter

class NauhaKhuwanFragment(private val activityContext: Context) : Fragment() {

    private lateinit var nauhaKhuwanFragmentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NauhaKhuwanAdapter

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
        nauhaKhuwanFragmentView = inflater.inflate(R.layout.fragment_nauha_khuwan, container, false)
        recyclerView =
            nauhaKhuwanFragmentView.findViewById<RecyclerView>(R.id.nauha_khuwan_recycler_view)
        adapter = NauhaKhuwanAdapter(activityContext)
        recyclerView.layoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        return nauhaKhuwanFragmentView
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter = adapter
    }

    companion object
}