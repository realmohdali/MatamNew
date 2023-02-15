package com.matamapp.matam

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.matamapp.matam.fragments.MediaPlayerFragment

class ProfileActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val managePlaylistCard = findViewById<CardView>(R.id.manage_playlist_card)
        val resetPasswordCard = findViewById<CardView>(R.id.reset_password_card)
        val logoutCard = findViewById<CardView>(R.id.logout_card)

        managePlaylistCard.setOnClickListener {

        }

        resetPasswordCard.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        logoutCard.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.confirmation_dialog)
            dialog.findViewById<Button>(R.id.yes_button).setOnClickListener {
                val sharedPreferences = getSharedPreferences(CommonData.PREFERENCES, MODE_PRIVATE)
                val preferencesEditor = sharedPreferences.edit()
                preferencesEditor.putString(CommonData.SESSION_TOKEN, "0")
                preferencesEditor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            dialog.findViewById<Button>(R.id.no_button).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
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
        return super.onKeyDown(keyCode, event)
    }
}