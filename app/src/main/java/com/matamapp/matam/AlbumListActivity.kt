package com.matamapp.matam

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.matamapp.matam.adapters.AlbumListAdapter
import com.matamapp.matam.fragments.MediaPlayerFragment

class AlbumListActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetFlag = false
    private lateinit var playerFragment: MediaPlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Album List"
        val recyclerView = findViewById<RecyclerView>(R.id.album_list_view)
        recyclerView.layoutManager = FlexboxLayoutManager(this).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        val adapter = AlbumListAdapter()
        recyclerView.adapter = adapter

        setPlayer()
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
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.playlist -> {
                Toast.makeText(this, "Playlist", Toast.LENGTH_SHORT).show()
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