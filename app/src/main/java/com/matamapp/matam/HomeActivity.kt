package com.matamapp.matam

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.matamapp.matam.adapters.HomeViewPagerAdapter
import com.matamapp.matam.fragments.MediaPlayerFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetFlag = false
    private lateinit var playerFragment: MediaPlayerFragment
    private lateinit var bottomNavigationView: BottomNavigationView
    private var scale = 0f
    private lateinit var nestedScrollView: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        scale = resources.displayMetrics.density

        //findViewById<ConstraintLayout>(R.id.player).visibility = View.GONE
        nestedScrollView = findViewById(R.id.nested_scroll_view)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setActivity()
        setPlayer()
    }

    private fun setActivity() {

        val navigationHome = R.id.navigation_home
        val navigationNauhaKhuwan = R.id.navigation_Nauha_Khuwan
        val navigationYears = R.id.navigation_year

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        val viewPagerAdapter = HomeViewPagerAdapter(supportFragmentManager, this.lifecycle, this)

        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    bottomNavigationView.selectedItemId = navigationHome
                } else if (position == 1) {
                    bottomNavigationView.selectedItemId = navigationNauhaKhuwan
                } else {
                    bottomNavigationView.selectedItemId = navigationYears
                }
            }
        })

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                navigationHome -> {
                    viewPager.currentItem = 0
                    true
                }
                navigationNauhaKhuwan -> {
                    viewPager.currentItem = 1
                    true
                }
                navigationYears -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setPlayer() {
        playerFragment = MediaPlayerFragment()
        supportFragmentManager.beginTransaction().replace(R.id.playerPlaceholder, playerFragment)
            .commitAllowingStateLoss()

        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.player))

        sheetBehavior.peekHeight = (150 * scale + 0.5f).toInt()

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
                        bottomNavigationView.visibility = View.GONE
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
                bottomNavigationView.visibility = View.VISIBLE
                bottomNavigationView.alpha = x
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
            R.id.profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java))
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