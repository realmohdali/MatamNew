package com.matamapp.matam.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.matamapp.matam.*
import com.matamapp.matam.db.FavoriteData
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_END
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_START
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_UPDATE
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.NEW_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.NEXT_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PAUSE_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAYER_PREPARED
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAY_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PREVIOUS_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.RESET_PLAYER
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_TO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_UPDATE
import com.matamapp.matam.mediaPlayer.QueueManagement

class MediaPlayerFragment : Fragment() {
    //Static Variables
    companion object {
        var imageURL = ""
        var progress = 0
        var currentTimeString = "00:00"
        var remainingTimeString = "-00:00"
        var trackTitle = "Track Title"
        var trackArtist = "Nauha Khuwan"
        var FAV = false
        var loopStatus = 0
        var isLoading = true
        var isPlaying = false
        var isShuffleEnabled = false
        var duration = 0
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

    //Broadcast Manager variable
    private lateinit var localBroadcastManager: LocalBroadcastManager

    //Create and resume MediaPlayer Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        registerNewAudioListener()
        registerLoadingCompleteListener()
        registerBufferingStartListener()
        registerBufferingEndListener()
        registerSeekUpdateListener()
        registerBufferingUpdateListener()
        registerPlayerResetListener()

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

    override fun onDestroy() {
        super.onDestroy()
        localBroadcastManager.unregisterReceiver(newAudioListener)
        localBroadcastManager.unregisterReceiver(playerPreparedListener)
        localBroadcastManager.unregisterReceiver(bufferingStartListener)
        localBroadcastManager.unregisterReceiver(bufferingEndListener)
        localBroadcastManager.unregisterReceiver(seekUpdateListener)
        localBroadcastManager.unregisterReceiver(bufferingUpdateListener)
        localBroadcastManager.unregisterReceiver(playerResetListener)
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

        //Add functions on click
        playPause.setOnClickListener {
            if (CommonData.serviceRunning) {
                playPause()
            }
        }
        playPauseMini.setOnClickListener {
            if (CommonData.serviceRunning) {
                playPause()
            }
        }

        prevButton.setOnClickListener {
            if (CommonData.serviceRunning) {
                previousTrack()
            }
        }
        prevButtonMini.setOnClickListener {
            if (CommonData.serviceRunning) {
                previousTrack()
            }
        }

        nextButton.setOnClickListener {
            if (CommonData.serviceRunning) {
                nextTrack()
            }
        }
        nextButtonMini.setOnClickListener {
            if (CommonData.serviceRunning) {
                nextTrack()
            }
        }

        loopButton.setOnClickListener {
            if (CommonData.serviceRunning) {
                setLoopStatus()
            }
        }

        shuffleButton.setOnClickListener {
            if (CommonData.serviceRunning) {
                toggleShuffle()
            }
        }

        favButton.setOnClickListener {
            if (CommonData.serviceRunning) {
                toggleFav()
            }
        }
    }
    //end initialize the variables

    //setup main player
    private fun setUpPlayer() {
        if (imageURL == "") {
            trackImageView.setImageResource(R.drawable.splash_image)
        } else {
            Glide.with(requireContext()).load(imageURL).into(trackImageView)
        }
        trackTitleView.text = trackTitle
        artistNameView.text = trackArtist

        if (isLoading) {
            playPause.visibility = View.GONE
            loadingBuffering.visibility = View.VISIBLE
        } else {
            playPause.visibility = View.VISIBLE
            loadingBuffering.visibility = View.GONE
        }

        if (isPlaying) {
            playPause.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_pause_24, null
                )
            )
        } else if (!isLoading) {
            playPause.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_play_arrow_24, null
                )
            )
        }

        if (!CommonData.serviceRunning) {
            playPause.visibility = View.VISIBLE
            loadingBuffering.visibility = View.GONE
            playPause.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_play_arrow_24, null
                )
            )
        }

        when (loopStatus) {
            0 -> {
                loopButton.setImageResource(R.drawable.ic_baseline_repeat_24)
            }
            1 -> {
                loopButton.setImageResource(R.drawable.ic_baseline_repeat_24_green)
            }
            2 -> {
                loopButton.setImageResource(R.drawable.ic_baseline_repeat_one_24_green)
            }
        }

        if (isShuffleEnabled) {
            shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_24_green)
        } else {
            shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_24)
        }

        if (CommonData.serviceRunning) {
            FAV = if (CommonData.existsInFavorite(
                    requireContext(),
                    QueueManagement.currentQueue[QueueManagement.currentPosition].id
                )
            ) {
                favButton.setImageResource(R.drawable.ic_baseline_favorite_24_green)
                true
            } else {
                favButton.setImageResource(R.drawable.ic_baseline_favorite_24)
                false
            }
        }


        seekBar.max = duration
        seekBar.progress = progress

        currentTime.text = currentTimeString
        remainingTime.text = remainingTimeString

        trackTitleView.isSelected = true
        setUpVolumeBar()

    }
    //end setup main player

    //setup mini player
    private fun setMiniPlayer() {
        if (imageURL == "") {
            trackImageViewMini.setImageResource(R.drawable.splash_image)
        } else {
            Glide.with(requireContext()).load(imageURL).into(trackImageViewMini)
        }
        trackTitleMini.text = trackTitle
        artistNameMini.text = trackArtist

        if (isLoading) {
            playPauseMini.visibility = View.GONE
            loadingMini.visibility = View.VISIBLE
        } else {
            playPauseMini.visibility = View.VISIBLE
            loadingMini.visibility = View.GONE
        }

        if (isPlaying) {
            playPauseMini.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_pause_24, null
                )
            )
        } else if (!isLoading) {
            playPauseMini.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_play_arrow_24, null
                )
            )
        }

        if (!CommonData.serviceRunning) {
            playPauseMini.visibility = View.VISIBLE
            loadingMini.visibility = View.GONE
            playPauseMini.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_play_arrow_24, null
                )
            )
        }
        progressBarMini.max = duration
        progressBarMini.progress = progress

        trackTitleMini.isSelected = true

    }
    //end setup mini player

    //Setup functions

    private fun setUpCurrentTimeString(currentPosition: Int) {
        currentTimeString = ""
        remainingTimeString = ""
        var seconds = (currentPosition / 1000) % 60
        var minutes = (currentPosition / 1000 - seconds) / 60

        if (minutes < 10) {
            currentTimeString += "0"
        }

        currentTimeString += "$minutes:"

        if (seconds < 10) {
            currentTimeString += "0"
        }

        currentTimeString += seconds

        currentTime.text = currentTimeString

        remainingTimeString = "-"
        val timeRemaining = duration - currentPosition

        seconds = (timeRemaining / 1000) % 60
        minutes = (timeRemaining / 1000 - seconds) / 60

        if (minutes < 10) {
            remainingTimeString += "0"
        }

        remainingTimeString += "$minutes:"

        if (seconds < 10) {
            remainingTimeString += "0"
        }

        remainingTimeString += seconds
        remainingTime.text = remainingTimeString
    }

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
                if (fromUser) {
                    seekTo(progress)
                    setUpCurrentTimeString(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        volumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, progress, 0
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        queueButton.setOnClickListener {
            if (CommonData.serviceRunning) {
                showQueue()
            }
        }

    }
    //End Set Listeners for click events

    //Player Handling functions
    private fun seekTo(position: Int) {
        val intent = Intent(SEEK_TO)
        intent.putExtra("seek_position", position)
        localBroadcastManager.sendBroadcast(intent)
    }

    private fun playPause() {

        if (isPlaying) {
            playPauseMini.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_play_arrow_24, null
                )
            )
            playPause.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_play_arrow_24, null
                )
            )
            localBroadcastManager.sendBroadcast(Intent(PAUSE_AUDIO))
        } else {
            playPause.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_pause_24, null
                )
            )
            playPauseMini.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_baseline_pause_24, null
                )
            )
            localBroadcastManager.sendBroadcast(Intent(PLAY_AUDIO))
        }
    }

    private fun previousTrack() {
        localBroadcastManager.sendBroadcast(Intent(PREVIOUS_TRACK))
    }

    private fun nextTrack() {
        localBroadcastManager.sendBroadcast(Intent(NEXT_TRACK))
    }

    private fun toggleShuffle() {
        if (isShuffleEnabled) {
            isShuffleEnabled = false
            shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_24)
            //localBroadcastManager.sendBroadcast(Intent(SHUFFLE_DISABLED))
        } else {
            isShuffleEnabled = true
            shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_24_green)
        }
    }

    private fun setLoopStatus() {
        when (loopStatus) {
            0 -> {
                loopStatus = 1
                loopButton.setImageResource(R.drawable.ic_baseline_repeat_24_green)
            }
            1 -> {
                loopStatus = 2
                loopButton.setImageResource(R.drawable.ic_baseline_repeat_one_24_green)
            }
            2 -> {
                loopStatus = 0
                loopButton.setImageResource(R.drawable.ic_baseline_repeat_24)
            }
        }
    }

    private fun toggleFav() {
        if (FAV) {
            CommonData.removeFavorite(
                requireContext(),
                QueueManagement.currentQueue[QueueManagement.currentPosition].id
            )
            FAV = false
            favButton.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {

            val trackImage =
                if (QueueManagement.currentQueue[QueueManagement.currentPosition].trackImage != "null") {
                    QueueManagement.currentQueue[QueueManagement.currentPosition].trackImage
                } else {
                    QueueManagement.currentQueue[QueueManagement.currentPosition].artist.image
                }

            val favoriteData = FavoriteData(
                QueueManagement.currentQueue[QueueManagement.currentPosition].id,
                QueueManagement.currentQueue[QueueManagement.currentPosition].title,
                QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl,
                trackImage,
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.id,
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.name,
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.image,
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.nationality,
                QueueManagement.currentQueue[QueueManagement.currentPosition].year.yearAD,
                QueueManagement.currentQueue[QueueManagement.currentPosition].year.yearHijri
            )

            CommonData.addFavorite(requireContext(), favoriteData)

            FAV = true
            favButton.setImageResource(R.drawable.ic_baseline_favorite_24_green)
        }
    }

    private fun showLyrics() {
        TODO("Do something when user hit lyrics button")
    }

    private fun showQueue() {
        startActivity(Intent(context, QueueActivity::class.java))
    }
    //End Player Handling functions

    //Broadcast Receiver functions

    private val newAudioListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setUpPlayer()
            setMiniPlayer()
        }
    }

    private fun registerNewAudioListener() {
        val filter = IntentFilter(NEW_AUDIO)
        localBroadcastManager.registerReceiver(newAudioListener, filter)
    }

    private val playerPreparedListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setUpPlayer()
            setMiniPlayer()
        }
    }

    private fun registerLoadingCompleteListener() {
        val filter = IntentFilter(PLAYER_PREPARED)
        localBroadcastManager.registerReceiver(playerPreparedListener, filter)
    }

    private val bufferingStartListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadingBuffering.visibility = View.VISIBLE
            playPause.visibility = View.GONE

            loadingMini.visibility = View.VISIBLE
            playPauseMini.visibility = View.GONE
        }
    }

    private fun registerBufferingStartListener() {
        val filter = IntentFilter(BUFFERING_START)
        localBroadcastManager.registerReceiver(bufferingStartListener, filter)
    }

    private val bufferingEndListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadingBuffering.visibility = View.GONE
            playPause.visibility = View.VISIBLE

            loadingMini.visibility = View.GONE
            playPauseMini.visibility = View.VISIBLE
        }
    }

    private fun registerBufferingEndListener() {
        val filter = IntentFilter(BUFFERING_END)
        localBroadcastManager.registerReceiver(bufferingEndListener, filter)
    }

    private val seekUpdateListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            progress = intent?.getIntExtra("current_position", 0)!!
            seekBar.progress = progress
            progressBarMini.progress = progress
            setUpCurrentTimeString(progress)
        }
    }

    private fun registerSeekUpdateListener() {
        val filter = IntentFilter(SEEK_UPDATE)
        localBroadcastManager.registerReceiver(seekUpdateListener, filter)
    }

    private val bufferingUpdateListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bufferingStatus = intent?.getIntExtra("buffering_update", 0)
            val ratio = bufferingStatus?.div(100.0)
            val currentBufferingLevel: Int = (seekBar.max * ratio!!).toInt()
            seekBar.secondaryProgress = currentBufferingLevel
        }
    }

    private fun registerBufferingUpdateListener() {
        localBroadcastManager.registerReceiver(
            bufferingUpdateListener, IntentFilter(BUFFERING_UPDATE)
        )
    }

    private val playerResetListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setUpPlayer()
            setMiniPlayer()
        }
    }

    private fun registerPlayerResetListener() {
        localBroadcastManager.registerReceiver(playerResetListener, IntentFilter(RESET_PLAYER))
    }

    //Broadcast Receiver functions END

    //Player layout functions
    private fun toggleSheet() {
        try {
            (activity as HomeActivity).toggleBottomSheet()
        } catch (_: Exception) {
        }
        try {
            (activity as AlbumActivity).toggleBottomSheet()
        } catch (_: Exception) {
        }
        try {
            (activity as AlbumListActivity).toggleBottomSheet()
        } catch (_: Exception) {
        }
        try {
            (activity as ProfileActivity).toggleBottomSheet()
        } catch (_: Exception) {
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