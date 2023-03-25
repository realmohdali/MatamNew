package com.matamapp.matam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.matamapp.matam.CommonData
import com.matamapp.matam.R
import com.matamapp.matam.adapters.AlbumListAdapter
import com.matamapp.matam.adapters.NauhaKhuwanAdapter
import com.matamapp.matam.data.AlbumData
import com.matamapp.matam.data.ArtistData
import org.json.JSONObject

class HomeFragment(private val context: Context) : Fragment() {

    private lateinit var view: View
    private lateinit var newReleaseRV: RecyclerView
    private lateinit var featuredAlbumRV: RecyclerView
    private lateinit var featuredArtistRV: RecyclerView
    private lateinit var nauhaKhuwanAdapter: NauhaKhuwanAdapter
    private lateinit var albumListAdapter: AlbumListAdapter
    private lateinit var volleyRequestQueue: RequestQueue
    private lateinit var newReleaseLoader: ProgressBar
    private lateinit var featuredAlbumsLoader: ProgressBar
    private lateinit var featuredArtistsLoader: ProgressBar

    private val newAlbumsList: MutableList<AlbumData> = mutableListOf()
    private val featuredAlbumsList: MutableList<AlbumData> = mutableListOf()
    private val featuredArtistsList: MutableList<ArtistData> = mutableListOf()


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
        view = inflater.inflate(R.layout.fragment_home, container, false)
        newReleaseRV = view.findViewById(R.id.newReleaseRV)
        featuredAlbumRV = view.findViewById(R.id.featuredAlbumRV)
        featuredArtistRV = view.findViewById(R.id.featuredArtistRV)

        newReleaseRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        featuredAlbumRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        featuredArtistRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        volleyRequestQueue = Volley.newRequestQueue(context)

        newReleaseLoader = view.findViewById(R.id.newReleaseLoader)
        featuredAlbumsLoader = view.findViewById(R.id.featuredAlbumsLoader)
        featuredArtistsLoader = view.findViewById(R.id.featuredArtistsLoader)

        return view
    }

    override fun onResume() {
        super.onResume()
        getNewReleaseAlbums()
        getFeaturedAlbums()
        getFeaturedArtists()
    }

    private fun getNewReleaseAlbums() {
        newReleaseRV.visibility = View.GONE
        newReleaseLoader.visibility = View.VISIBLE
        val url = CommonData.API_URL + "albums/new"
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonResponse = JSONObject(response)
            val code = jsonResponse.getString("code")
            if (code == "200") {
                newAlbumsList.clear()
                newReleaseLoader.visibility = View.GONE
                newReleaseRV.visibility = View.VISIBLE
                val list = jsonResponse.optJSONArray("data")
                if (list != null) {
                    for (i in 0 until list.length()) {
                        val album = list.optJSONObject(i)
                        val id = album.optString("id")
                        val name = album.optString("name")
                        val cover = album.optString("album_cover")
                        val featured = album.optString("featured")
                        val newAlbum = album.optString("new")

                        val albumData = AlbumData(id, name, cover, featured, newAlbum)
                        newAlbumsList.add(albumData)
                    }
                    albumListAdapter = AlbumListAdapter(context, newAlbumsList)
                    newReleaseRV.adapter = albumListAdapter
                }
            } else {
                Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                    .show()
            }
        }, {
        })
        volleyRequestQueue.add(stringRequest)
    }

    private fun getFeaturedAlbums() {
        featuredArtistRV.visibility = View.GONE
        featuredAlbumsLoader.visibility = View.VISIBLE
        val url = CommonData.API_URL + "albums/featured"
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonResponse = JSONObject(response)
            val code = jsonResponse.getString("code")
            if (code == "200") {
                featuredAlbumsList.clear()
                featuredAlbumsLoader.visibility = View.GONE
                featuredArtistRV.visibility = View.VISIBLE
                val list = jsonResponse.optJSONArray("data")
                if (list != null) {
                    for (i in 0 until list.length()) {
                        val album = list.optJSONObject(i)
                        val id = album.optString("id")
                        val name = album.optString("name")
                        val cover = album.optString("album_cover")
                        val featured = album.optString("featured")
                        val new = album.optString("new")

                        val albumData = AlbumData(id, name, cover, featured, new)
                        featuredAlbumsList.add(albumData)
                    }
                    albumListAdapter = AlbumListAdapter(context, featuredAlbumsList)
                    featuredAlbumRV.adapter = albumListAdapter
                }
            } else {
                Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                    .show()
            }
        }, {
        })
        volleyRequestQueue.add(stringRequest)
    }

    private fun getFeaturedArtists() {
        featuredArtistRV.visibility = View.GONE
        featuredArtistsLoader.visibility = View.VISIBLE
        val url = CommonData.API_URL + "artists/get/featured"
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonResponse = JSONObject(response)
            val code = jsonResponse.getString("code")
            if (code == "200") {
                featuredArtistsList.clear()
                featuredArtistsLoader.visibility = View.GONE
                featuredArtistRV.visibility = View.VISIBLE
                val list = jsonResponse.optJSONArray("data")
                if (list != null) {
                    for (i in 0 until list.length()) {
                        val artist = list.optJSONObject(i)
                        val id = artist.optString("id")
                        val name = artist.optString("name")
                        val image = artist.optString("image")
                        val nationality = artist.optString("nationality")

                        val artistData = ArtistData(id, name, image, nationality)
                        featuredArtistsList.add(artistData)
                    }
                    nauhaKhuwanAdapter = NauhaKhuwanAdapter(context, featuredArtistsList)
                    featuredArtistRV.adapter = nauhaKhuwanAdapter
                }
            } else {
                Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                    .show()
            }
        }, {
        })
        volleyRequestQueue.add(stringRequest)
    }
}