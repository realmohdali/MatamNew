package com.matamapp.matam.fragments

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.matamapp.matam.AlbumActivity
import com.matamapp.matam.HomeActivity
import com.matamapp.matam.R

class MediaPlayerFragment : Fragment() {
    //Static Variables
    companion object {
        var imageURL = ""
        var progress = 0
        var currentTime = ""
        var remainingTime = ""
        var trackTitle = ""
        var trackArtist = ""
        var volume = 0
        var PLAY = false
        var FAV = false
        var LoopStatus = 0
        var isLoading = false
        var isPlaying = false
        var isShuffleEnabled = false
    }
    //End Static Variables

    private lateinit var playerView: View
    private lateinit var miniPlayer: View
    private var isPlayerExpanded = false
    private lateinit var audioManager: AudioManager

    //Main player elements
    private lateinit var collapsePlayer: ImageButton
    private lateinit var trackImageView: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var remainingTime: TextView
    private lateinit var trackTitleView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var volumeBar: SeekBar
    private lateinit var shuffleButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var playPause: ImageButton
    private lateinit var loadingBuffering: ProgressBar
    private lateinit var nextButton: ImageButton
    private lateinit var loopButton: ImageButton
    private lateinit var favButton: ImageButton
    private lateinit var lyricsButton: Button
    private lateinit var queueButton: ImageButton

    //Mini player elements
    private lateinit var progressBarMini: ProgressBar
    private lateinit var trackImageViewMini: ImageView
    private lateinit var trackTitleMini: TextView
    private lateinit var artistNameMini: TextView
    private lateinit var prevButtonMini: ImageView
    private lateinit var playPauseMini: ImageView
    private lateinit var loadingMini: ProgressBar
    private lateinit var nextButtonMini: ImageView


    //Create and resume MediaPlayer Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        playerView = inflater.inflate(R.layout.fragment_media_player, container, false)
        miniPlayer = playerView.findViewById<CardView>(R.id.miniPlayer)
        return playerView
    }

    override fun onResume() {
        super.onResume()
        init()
        setMiniPlayer()
        setUpPlayer()
        setUpClickListeners()
    }
    //End create and resume MediaPlayer Fragment

    //Initialize the variables
    private fun init() {
        //Main Player item init
        collapsePlayer = playerView.findViewById(R.id.downArrow)
        trackImageView = playerView.findViewById(R.id.trackArt)
        seekBar = playerView.findViewById(R.id.seekBar)
        currentTime = playerView.findViewById(R.id.currentTime)
        remainingTime = playerView.findViewById(R.id.timeLeft)
        trackTitleView = playerView.findViewById(R.id.trackTitle)
        artistNameView = playerView.findViewById(R.id.artist_name_view)
        volumeBar = playerView.findViewById(R.id.volumeSlider)
        shuffleButton = playerView.findViewById(R.id.shuffle)
        prevButton = playerView.findViewById(R.id.previous)
        playPause = playerView.findViewById(R.id.playPause)
        loadingBuffering = playerView.findViewById(R.id.loading)
        nextButton = playerView.findViewById(R.id.next)
        loopButton = playerView.findViewById(R.id.repeat)
        favButton = playerView.findViewById(R.id.fav)
        lyricsButton = playerView.findViewById(R.id.lyrics)
        queueButton = playerView.findViewById(R.id.queue)

        //Mini Player item init
        progressBarMini = playerView.findViewById(R.id.progressBar)
        trackImageViewMini = playerView.findViewById(R.id.miniTrackArt)
        trackTitleMini = playerView.findViewById(R.id.miniTrackTitle)
        artistNameMini = playerView.findViewById(R.id.miniTrackArtist)
        prevButtonMini = playerView.findViewById(R.id.miniPrev)
        playPauseMini = playerView.findViewById(R.id.miniPlayPause)
        loadingMini = playerView.findViewById(R.id.miniLoading)
        nextButtonMini = playerView.findViewById(R.id.miniNext)
    }
    //end initialize the variables

    //setup main player
    private fun setUpPlayer() {
        trackTitleView.isSelected = true
        setUpVolumeBar()
    }
    //end setup main player

    //setup mini player
    private fun setMiniPlayer() {
        trackTitleMini.isSelected = true
    }
    //end setup mini player

    //Setup functions
    private fun setUpVolumeBar() {
        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeBar.progress = volumeLevel
    }

    private fun updateVolumeBar() {
        val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeBar.progress = volumeLevel
    }

    fun setVolume(keyCode: Int) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            updateVolumeBar()
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            updateVolumeBar()
        }
    }

    //End Setup functions

    //Set Listeners for click events
    private fun setUpClickListeners() {
        miniPlayer.setOnClickListener {
            toggleSheet()
        }
        collapsePlayer.setOnClickListener {
            toggleSheet()
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        volumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    progress,
                    0
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }
    //End Set Listeners for click events

    //Player Handling functions
    private fun seekTo(position: Int) {
        TODO("Do something when user seek to a position")
    }

    private fun changeVolume(volumeLevel: Int) {
        //TODO("Do something when user change volume")
    }

    private fun playPause() {
        TODO("Do something when user hit playPause button")
    }

    private fun previousTrack() {
        TODO("Do something when user hit prevButton")
    }

    private fun nextTrack() {
        TODO("Do something when user hit nextButton")
    }

    private fun toggleShuffle() {
        TODO("Do something when user hit Shuffle")
    }

    private fun setLoopStatus() {
        TODO("Do something when user hit loop")
    }

    private fun toggleFav() {
        TODO("Do something when user hit favorite button")
    }

    private fun showLyrics() {
        TODO("Do something when user hit lyrics button")
    }

    private fun showQueue() {
        TODO("Do something when user hit queue button")
    }
    //End Player Handling functions

    //Player layout functions
    private fun toggleSheet() {
        try {
            (activity as HomeActivity).toggleBottomSheet()
        } catch (e: Exception) {
        }
        try {
            (activity as AlbumActivity).toggleBottomSheet()
        } catch (e: Exception) {
        }
    }

    fun scroll(alpha: Float) {
        miniPlayer.visibility = View.VISIBLE
        miniPlayer.alpha = alpha
    }

    fun collapsed() {
        miniPlayer.visibility = View.VISIBLE
        isPlayerExpanded = false
    }

    fun expanded() {
        miniPlayer.visibility = View.GONE
        isPlayerExpanded = true
    }
    //End Player layout functions
}