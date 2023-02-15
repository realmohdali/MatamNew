package com.matamapp.matam

import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.matamapp.matam.adapters.FavoriteAdapter
import com.matamapp.matam.data.ArtistData
import com.matamapp.matam.data.TrackData
import com.matamapp.matam.data.YearData
import com.matamapp.matam.db.DatabaseHelper
import com.matamapp.matam.fragments.MediaPlayerFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class FavoriteActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Favorite"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val favoriteRecyclerView: RecyclerView = findViewById(R.id.favorite_list_view)
        favoriteRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        //favoriteRecyclerView.itemAnimator = null

        val favoriteList: MutableList<TrackData> = getFavoriteList()

        if (favoriteList.isEmpty()) {
            val textView: TextView = findViewById(R.id.favoriteText)
            textView.visibility = View.VISIBLE
        }

        val adapter = FavoriteAdapter(this, favoriteList)
        favoriteRecyclerView.adapter = adapter

        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                this@FavoriteActivity,
                                R.color.secondary
                            )
                        )
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val instance = DatabaseHelper.getInstance(applicationContext)
                    instance.favoriteDao()
                        .removeFromFavorite(favoriteList[viewHolder.absoluteAdapterPosition].id)
                    favoriteList.removeAt(viewHolder.absoluteAdapterPosition)
                    adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                }
            })

        itemTouchHelper.attachToRecyclerView(favoriteRecyclerView)

        setPlayer()
    }

    private fun setPlayer() {
        val playerFragment = MediaPlayerFragment()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getFavoriteList(): MutableList<TrackData> {
        val favoriteList: MutableList<TrackData> = mutableListOf()
        val dbInstance = DatabaseHelper.getInstance(this)
        val list = dbInstance.favoriteDao().getAllFavorite()
        for (track in list) {
            val trackId = track.trackId
            val trackTitle = track.trackTitle
            val trackURL = track.trackURL
            val trackImage = track.trackImage
            val artistId = track.artistId
            val artistName = track.artistName
            val artistImage = track.artistImage
            val artistNationality = track.artistNationality
            val yearAD = track.yearAD
            val yearHijri = track.yearHijri

            val year = YearData(yearAD, yearHijri)
            val artist = ArtistData(artistId, artistName, artistImage, artistNationality)
            val trackData = TrackData(trackId, trackTitle, trackURL, trackImage, artist, year)
            favoriteList.add(trackData)
        }
        return favoriteList
    }
}