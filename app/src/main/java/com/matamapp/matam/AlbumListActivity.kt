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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.matamapp.matam.adapters.AlbumListAdapter
import com.matamapp.matam.data.AlbumData
import com.matamapp.matam.fragments.MediaPlayerFragment
import org.json.JSONObject

class AlbumListActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetFlag = false
    private lateinit var playerFragment: MediaPlayerFragment
    private lateinit var caller: String
    private lateinit var id: String
    private var albumList: MutableList<AlbumData> = mutableListOf()
    private lateinit var loader: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Albums"
        val intent: Intent = intent
        caller = intent.getStringExtra("caller").toString()
        id = intent.getStringExtra("id").toString()
        loader = findViewById(R.id.loader)

        getData()

        recyclerView = findViewById(R.id.favorite_list_view)
        recyclerView.layoutManager = FlexboxLayoutManager(this).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        setPlayer()
    }

    private fun getData() {

        loader.visibility = View.VISIBLE

        var url = CommonData.API_URL + "albums/"
        url += if (caller == "artist") {
            "artist/$id"
        } else {
            "year/$id"
        }

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val code = jsonResponse.getString("code")
                if (code == "200") {
                    val albumListData = jsonResponse.optJSONArray("data")
                    if (albumListData != null) {
                        for (i in 0 until albumListData.length()) {
                            val album = albumListData.getJSONObject(i)
                            val id = album.optString("id")
                            val name = album.optString("name")
                            val cover = album.optString("album_cover")
                            val featured = album.optString("featured")
                            val newRelease = album.optString("new_release")

                            val albumData = AlbumData(id, name, cover, featured, newRelease)
                            albumList.add(albumData)
                        }
                        val adapter = AlbumListAdapter(this, albumList)
                        recyclerView.adapter = adapter
                        loader.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                }
            },
            {
                Toast.makeText(
                    this,
                    "That didn't work!",
                    Toast.LENGTH_SHORT
                ).show()
            })
        queue.add(stringRequest)
    }

    private fun setPlayer() {
        playerFragment = MediaPlayerFragment()
        supportFragmentManager.beginTransaction().replace(R.id.playerPlaceholder, playerFragment)
            .commitAllowingStateLoss()

        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.player))

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        playerFragment.collapsed()
                        bottomSheetFlag = false
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        playerFragment.expanded()
                        bottomSheetFlag = true
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

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        playerFragment.setVolume(keyCode)
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                return true
            }
            R.id.favorite -> {
                startActivity(Intent(this, FavoriteActivity::class.java))
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
        return super.onKeyDown(keyCode, event)
    }
}