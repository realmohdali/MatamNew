package com.matamapp.matam

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.matamapp.matam.adapters.AlbumAdapter
import com.matamapp.matam.data.ArtistData
import com.matamapp.matam.data.TrackData
import com.matamapp.matam.data.YearData
import com.matamapp.matam.fragments.MediaPlayerFragment
import org.json.JSONObject

class AlbumActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetFlag = false
    private var albumArt = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("album_name").toString()

        val albumId = intent.getStringExtra("album_id").toString()
        albumArt = intent.getStringExtra("album_art").toString()

        setPlayer()
        setAlbum(albumId)
    }

    private fun setPlayer() {
        val playerFragment = MediaPlayerFragment()
        supportFragmentManager.beginTransaction().replace(R.id.playerPlaceholder, playerFragment)
            .commitAllowingStateLoss()

        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.player))

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                bottomSheetFlag = when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        playerFragment.collapsed()
                        false
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        playerFragment.expanded()
                        true
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> return
                    BottomSheetBehavior.STATE_SETTLING -> return
                    BottomSheetBehavior.STATE_HIDDEN -> return
                    else -> return
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
                val x = 1 - slideOffset
                playerFragment.scroll(x)
            }
        })
    }

    fun toggleBottomSheet() {
        if (bottomSheetFlag) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setAlbum(albumId: String) {
        val albumLoader: ProgressBar = findViewById(R.id.album_loader)
        albumLoader.visibility = View.VISIBLE
        val albumView = findViewById<RecyclerView>(R.id.newReleaseRV)
        albumView.layoutManager = LinearLayoutManager(this)

        val albumTrackList: MutableList<TrackData> = mutableListOf()
        val url = CommonData.API_URL + "albums/album_tracks/$albumId"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val jsonResponse = JSONObject(response)
                val code = jsonResponse.getString("code")
                if (code == "200") {
                    val tracks = jsonResponse.optJSONArray("data")
                    if (tracks != null) {
                        for (i in 0 until tracks.length()) {
                            val data = tracks.getJSONObject(i)
                            val track = data.optJSONObject("track")
                            val id = track?.optString("id").toString()
                            val title = track?.optString("title").toString()
                            val trackURL = track?.optString("track_url").toString()
                            val trackImage =
                                if (track?.optString("track_image").toString() != "null") {
                                    track?.optString("track_image").toString()
                                } else {
                                    albumArt
                                }
                            val artist = track?.optJSONObject("artist")
                            val name = artist?.optString("name").toString()
                            val image = artist?.optString("image").toString()
                            val year = track?.optJSONObject("year")
                            val yearAD = year?.optString("year_ad").toString()
                            val yearHijri = year?.optString("year_hijri").toString()

                            val artistData = ArtistData("", name, image, "")
                            val yearData = YearData("0", yearAD, yearHijri)

                            val trackData =
                                TrackData(id, title, trackURL, trackImage, artistData, yearData)

                            albumTrackList.add(trackData)
                        }
                        albumView.adapter = AlbumAdapter(this, albumTrackList, false)
                        albumLoader.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                }
            },
            {

            })
        queue.add(stringRequest)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.profile -> {
//                startActivity(Intent(this, ProfileActivity::class.java))
//                return true
//            }
            R.id.contact -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            R.id.favorite -> {
                startActivity(Intent(this, FavoriteActivity::class.java))
                return true
            }
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bottomSheetFlag) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                finish()
            }
        }
        return false
    }
}