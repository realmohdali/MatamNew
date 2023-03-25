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
import com.matamapp.matam.adapters.NauhaKhuwanAdapter
import com.matamapp.matam.data.ArtistData
import org.json.JSONObject


class NauhaKhuwanFragment(private val activityContext: Context) : Fragment() {

    private lateinit var nauhaKhuwanFragmentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NauhaKhuwanAdapter
    private var artists: MutableList<ArtistData> = mutableListOf()
    private lateinit var loadingBar: ProgressBar

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

        loadingBar = nauhaKhuwanFragmentView.findViewById(R.id.loading_bar)

        recyclerView =
            nauhaKhuwanFragmentView.findViewById(R.id.nauha_khuwan_recycler_view)

        recyclerView.layoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        return nauhaKhuwanFragmentView
    }

    override fun onResume() {
        super.onResume()
        getNauhaKhuwanData()
    }

    private fun getNauhaKhuwanData() {

        loadingBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = CommonData.API_URL + "artists"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val code = jsonResponse.getString("code")
                if(code == "200") {
                    loadingBar.visibility = View.GONE
                    artists.clear()
                    val artistsList = jsonResponse.optJSONArray("data")
                    if (artistsList != null) {
                        for (i in 0 until artistsList.length()){
                            val artist = artistsList.getJSONObject(i)
                            val id = artist.optString("id")
                            val name = artist.optString("name")
                            val image = artist.optString("image")
                            val nationality = artist.optString("nationality")

                            val artistData = ArtistData(id, name, image, nationality)
                            artists.add(artistData)
                            //Toast.makeText(context, "$id $name $nationality", Toast.LENGTH_SHORT).show()

                        }
                        adapter = NauhaKhuwanAdapter(activityContext, artists)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(
                    context,
                    "That didn't work!",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    companion object
}