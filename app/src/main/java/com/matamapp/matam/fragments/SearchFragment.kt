package com.matamapp.matam.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.adapters.AlbumAdapter
import com.matamapp.matam.adapters.NauhaKhuwanAdapter
import com.matamapp.matam.data.ArtistData
import com.matamapp.matam.data.TrackData
import com.matamapp.matam.data.YearData
import org.json.JSONObject

class SearchFragment(private val context: Context) : Fragment() {

    private lateinit var nauhaKhuwanRecyclerView: RecyclerView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var nauhaKhuwanAdapter: NauhaKhuwanAdapter
    private lateinit var albumAdapter: AlbumAdapter
    private val artists: MutableList<ArtistData> = mutableListOf()
    private val tracks: MutableList<TrackData> = mutableListOf()

    private lateinit var artistTitle: TextView
    private lateinit var tracksTitle: TextView
    private lateinit var artistLoading: ProgressBar
    private lateinit var artistNoResult: TextView
    private lateinit var tracksLoading: ProgressBar
    private lateinit var tracksNoResult: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        artistTitle = view.findViewById(R.id.search_nauha_khuwan_title)
        tracksTitle = view.findViewById(R.id.search_tracks_title)
        artistLoading = view.findViewById(R.id.search_nauha_khuwan_loading)
        artistNoResult = view.findViewById(R.id.search_nauha_khuwan_no_result)
        tracksLoading = view.findViewById(R.id.search_tracks_loading)
        tracksNoResult = view.findViewById(R.id.search_tracks_no_result)

        val searchBox: EditText = view.findViewById(R.id.search_box)
        searchBox.requestFocus()

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 2) {
                    fetchArtist(s.toString())
                    fetchTracks(s.toString())
                } else if (count == 0) {
                    artistTitle.visibility = View.GONE
                    tracksTitle.visibility = View.GONE
                    artistLoading.visibility = View.GONE
                    artistNoResult.visibility = View.GONE
                    tracksLoading.visibility = View.GONE
                    tracksNoResult.visibility = View.GONE
                    nauhaKhuwanRecyclerView.visibility = View.GONE
                    tracksRecyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        nauhaKhuwanRecyclerView = view.findViewById(R.id.nauha_khuwan_recyclerview)
        tracksRecyclerView = view.findViewById(R.id.tracks_recyclerview)

        nauhaKhuwanRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        tracksRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        nauhaKhuwanAdapter = NauhaKhuwanAdapter(context, artists)
        nauhaKhuwanRecyclerView.adapter = nauhaKhuwanAdapter

        albumAdapter = AlbumAdapter(context, tracks)
        tracksRecyclerView.adapter = albumAdapter
    }

    private fun fetchArtist(s: String) {
        artistLoading.visibility = View.VISIBLE
        artistTitle.visibility = View.VISIBLE
        artistNoResult.visibility = View.GONE
        nauhaKhuwanRecyclerView.visibility = View.GONE

        val queue = Volley.newRequestQueue(context)
        val query = formatURL(s)
        val url = CommonData.API_URL + "artists/" + query

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonResponse = JSONObject(response)
            val code = jsonResponse.getString("code")
            if (code == "200") {
                artists.clear()
                val artistsList = jsonResponse.optJSONArray("data")
                if (artistsList != null) {
                    artistNoResult.visibility = View.GONE
                    artistLoading.visibility = View.GONE
                    nauhaKhuwanRecyclerView.visibility = View.VISIBLE
                    for (i in 0 until artistsList.length()) {
                        val artist = artistsList.getJSONObject(i)
                        val id = artist.optString("id")
                        val name = artist.optString("name")
                        val image = artist.optString("image")
                        val nationality = artist.optString("nationality")

                        val artistData = ArtistData(id, name, image, nationality)
                        artists.add(artistData)
                    }
                    nauhaKhuwanRecyclerView.adapter?.notifyDataSetChanged()
                }
            } else {
                //Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                artistLoading.visibility = View.GONE
                nauhaKhuwanRecyclerView.visibility = View.GONE
                artistNoResult.visibility = View.VISIBLE
            }
        }, {
            Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show()
        })

        queue.add(stringRequest)
    }

    private fun fetchTracks(s: String) {
        tracksLoading.visibility = View.VISIBLE
        tracksTitle.visibility = View.VISIBLE
        tracksNoResult.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE

        val query = formatURL(s)
        val url = CommonData.API_URL + "tracks/" + query
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonResponse = JSONObject(response)
            val code = jsonResponse.getString("code")
            if (code == "200") {
                tracks.clear()
                val tracks = jsonResponse.optJSONArray("data")
                if (tracks != null) {
                    tracksLoading.visibility = View.GONE
                    tracksRecyclerView.visibility = View.VISIBLE
                    for (i in 0 until tracks.length()) {
                        val data = tracks.getJSONObject(i)
                        val id = data.optString("id")
                        val title = data.optString("title")
                        val trackURL = data.optString("track_url")
                        val trackImage = data.optString("track_image")
                        val artist = data.optJSONObject("artist")
                        val name = artist?.optString("name")
                        val image = artist?.optString("image")
                        val year = data.optJSONObject("year")
                        val yearAD = year?.optString("year_ad")
                        val yearHijri = year?.optString("year_hijri")

                        val artistData = ArtistData("", name!!, image!!, "")
                        val yearData = YearData("0", yearAD!!, yearHijri!!)

                        val trackData =
                            TrackData(id, title, trackURL, trackImage, artistData, yearData)
                        this.tracks.add(trackData)
                    }
                    tracksRecyclerView.adapter?.notifyDataSetChanged()
                }
            } else {
                //Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                tracksLoading.visibility = View.GONE
                tracksNoResult.visibility = View.VISIBLE
                tracksRecyclerView.visibility = View.GONE
            }
        }, {
        })

        queue.add(stringRequest)
    }

    private fun formatURL(rawURL: String): String {
        return rawURL.replace(" ", "%20")
    }
}