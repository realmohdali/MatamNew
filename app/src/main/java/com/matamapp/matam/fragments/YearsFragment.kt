package com.matamapp.matam.fragments

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
import com.matamapp.matam.adapters.YearsAdapter

class YearsFragment : Fragment() {

    private lateinit var yearFragmentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: YearsAdapter


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
        yearFragmentView = inflater.inflate(R.layout.fragment_years, container, false)
        recyclerView = yearFragmentView.findViewById(R.id.years_recycler_view)
        recyclerView.layoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        adapter = YearsAdapter()
        return yearFragmentView
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter = adapter
    }
}