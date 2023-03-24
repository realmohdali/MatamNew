package com.matamapp.matam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.adapters.YearsAdapter
import com.matamapp.matam.data.YearData
import org.json.JSONObject

class YearsFragment(private val context: Context) : Fragment() {

    private lateinit var yearFragmentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: YearsAdapter
    private val years: MutableList<YearData> = mutableListOf()
    private lateinit var loading: ProgressBar


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
        loading = yearFragmentView.findViewById(R.id.progressBar4)
        return yearFragmentView
    }

    override fun onResume() {
        super.onResume()
        getYearData()
    }

    private fun getYearData() {
        loading.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(context)
        val url = CommonData.API_URL + "years"

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonResponse = JSONObject(response)
            val code = jsonResponse.getString("code")
            if (code == "200") {
                loading.visibility = View.GONE
                years.clear()
                val yearList = jsonResponse.optJSONArray("data")
                if (yearList != null) {
                    for (i in 0 until yearList.length()) {
                        val year = yearList.getJSONObject(i)
                        val id = year.optString("id")
                        val yearAD = year.optString("year_ad")
                        val yearHijri = year.optString("year_hijri")

                        val yearData = YearData(id, yearAD, yearHijri)
                        years.add(yearData)
                    }
                    adapter = YearsAdapter(context, years)
                    recyclerView.adapter = adapter
                }
            } else {
                Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                    .show()
            }
        }, {
        })

        queue.add(stringRequest)
    }
}