package com.matamapp.matam.mediaPlayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.matamapp.matam.CommonData
import com.matamapp.matam.fragments.MediaPlayerFragment
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_END
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_START
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_UPDATE
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.NEXT_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PAUSE_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAYER_PREPARED
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAY_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PREVIOUS_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.RESET_PLAYER
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_TO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_UPDATE
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MediaPlayerService : Service() {
    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    private lateinit var executors: ExecutorService

    private lateinit var mediaPlayer: MediaPlayer

    private var trackURL = ""
    private var resumePosition = 0
    private val mAudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        trackURL = formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)

        if (trackURL != "") {
            initMediaPlayer()
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        CommonData.serviceRunning = true

        //Register Broadcast Receivers
        registerPlayAudioListener()
        registerPauseAudioListener()
        registerSeekToListener()
        registerNextTrackListener()
        registerPreviousTrackListener()
    }

    override fun onDestroy() {
        stopMedia()
        CommonData.serviceRunning = false

        QueueManagement.currentQueue.clear()
        QueueManagement.currentPosition = -1

        //Unregister Broadcast Receivers
        localBroadcastManager.unregisterReceiver(playAudioListener)
        localBroadcastManager.unregisterReceiver(pauseAudioListener)
        localBroadcastManager.unregisterReceiver(seekToListener)
        localBroadcastManager.unregisterReceiver(nextTrackListener)
        localBroadcastManager.unregisterReceiver(previousTrackListener)

        localBroadcastManager.sendBroadcast(Intent(RESET_PLAYER))

        super.onDestroy()
    }

    //Init Player

    private fun initMediaPlayer() {
        setUpUI()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(mAudioAttributes)
        try {
            mediaPlayer.setDataSource(trackURL)
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer.setOnPreparedListener {
            MediaPlayerFragment.duration = mediaPlayer.duration
            playMedia()
            localBroadcastManager.sendBroadcast(Intent(PLAYER_PREPARED))
        }
        mediaPlayer.setOnCompletionListener {
            //Track Completed
            if (QueueManagement.currentPosition < QueueManagement.currentQueue.size - 1) {
                if (MediaPlayerFragment.loopStatus == 2) {
                    mediaPlayer.seekTo(0)
                    playMedia()
                } else {
                    QueueManagement.currentPosition++
                    trackURL =
                        formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                    stopMedia()
                    initMediaPlayer()
                }
            } else {
                when (MediaPlayerFragment.loopStatus) {
                    0 -> {
                        resetPlayer()
                    }
                    1 -> {
                        QueueManagement.currentPosition = 0
                        trackURL =
                            formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                        stopMedia()
                        initMediaPlayer()
                    }
                    2 -> {
                        mediaPlayer.seekTo(0)
                        playMedia()
                    }
                }
            }
        }
        mediaPlayer.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    //Buffering Starts
                    MediaPlayerFragment.isLoading = true
                    MediaPlayerFragment.isPlaying = false
                    localBroadcastManager.sendBroadcast(Intent(BUFFERING_START))
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    //Buffering ENDs
                    MediaPlayerFragment.isLoading = false
                    MediaPlayerFragment.isPlaying = true
                    localBroadcastManager.sendBroadcast(Intent(BUFFERING_END))
                }
            }
            false
        }
        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
            //Buffering status
            val intent = Intent(BUFFERING_UPDATE)
            intent.putExtra("buffering_update", percent)
            localBroadcastManager.sendBroadcast(intent)
            //println("Buffering Update in Service $percent")
        }
    }

    //Init Player END

    //Player Control Functions
    private fun playMedia() {

        mediaPlayer.start()
        MediaPlayerFragment.isLoading = false
        MediaPlayerFragment.isPlaying = true
        seekUpdate()
    }

    private fun pauseMedia() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            resumePosition = mediaPlayer.currentPosition
            MediaPlayerFragment.isPlaying = false
            executors.shutdown()
        }
    }

    private fun resumeMedia() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(resumePosition)
            mediaPlayer.start()
            MediaPlayerFragment.isPlaying = true
            seekUpdate()
        }
    }

    private fun seekTO(seekPosition: Int) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(seekPosition)
        }
    }

    private fun stopMedia() {
        mediaPlayer.stop()
        mediaPlayer.release()
        executors.shutdown()
    }

    //Player Control Functions END

    //Broadcast Receivers

    private val playAudioListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!mediaPlayer.isPlaying) {
                resumeMedia()
            }
        }
    }

    private fun registerPlayAudioListener() {
        localBroadcastManager.registerReceiver(playAudioListener, IntentFilter(PLAY_AUDIO))
    }

    private val pauseAudioListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (mediaPlayer.isPlaying) {
                pauseMedia()
            }
        }
    }

    private fun registerPauseAudioListener() {
        localBroadcastManager.registerReceiver(pauseAudioListener, IntentFilter(PAUSE_AUDIO))
    }

    private val seekToListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val seekPosition = intent.getIntExtra("seek_position", 0)
                seekTO(seekPosition)
            }
        }
    }

    private fun registerSeekToListener() {
        localBroadcastManager.registerReceiver(seekToListener, IntentFilter(SEEK_TO))
    }

    private val nextTrackListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (QueueManagement.currentPosition < QueueManagement.currentQueue.size - 1) {
                QueueManagement.currentPosition++
                trackURL =
                    formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                stopMedia()
                initMediaPlayer()
            }
        }
    }

    private fun registerNextTrackListener() {
        localBroadcastManager.registerReceiver(nextTrackListener, IntentFilter(NEXT_TRACK))
    }

    private val previousTrackListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (QueueManagement.currentPosition > 0) {
                QueueManagement.currentPosition--
                trackURL =
                    formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                stopMedia()
                initMediaPlayer()
            }
        }
    }

    private fun registerPreviousTrackListener() {
        localBroadcastManager.registerReceiver(previousTrackListener, IntentFilter(PREVIOUS_TRACK))
    }

    //Broadcast Receivers END

    //Seek Update Function

    private fun seekUpdate() {
        val runnable = Runnable {
            if (mediaPlayer.isPlaying) {
                val intent = Intent(SEEK_UPDATE)
                intent.putExtra("current_position", mediaPlayer.currentPosition)
                localBroadcastManager.sendBroadcast(intent)
            }
        }
        executors = Executors.newSingleThreadScheduledExecutor()
        (executors as ScheduledExecutorService?)?.scheduleAtFixedRate(
            runnable,
            0,
            1,
            TimeUnit.SECONDS
        )
    }

    //Seek Update Function END

    //Other Helping Functions
    private fun formatURL(rawURL: String): String {
        val formattedURL = rawURL.replace(" ", "%20")
        return "https://$formattedURL"
    }

    private fun setUpUI() {
        MediaPlayerFragment.imageURL =
            if (QueueManagement.currentQueue[QueueManagement.currentPosition].trackImage != "null") {
                QueueManagement.currentQueue[QueueManagement.currentPosition].trackImage
            } else {
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.image
            }
        MediaPlayerFragment.trackTitle =
            QueueManagement.currentQueue[QueueManagement.currentPosition].title
        MediaPlayerFragment.trackArtist =
            QueueManagement.currentQueue[QueueManagement.currentPosition].artist.name
        MediaPlayerFragment.isLoading = true
        MediaPlayerFragment.isPlaying = false

        localBroadcastManager.sendBroadcast(Intent(BroadcastConstants.NEW_AUDIO))
    }

    private fun resetPlayer() {
        MediaPlayerFragment.imageURL = ""
        MediaPlayerFragment.progress = 0
        MediaPlayerFragment.currentTimeString = "00:00"
        MediaPlayerFragment.remainingTimeString = "-00:00"
        MediaPlayerFragment.trackTitle = "Track Title"
        MediaPlayerFragment.trackArtist = "Nauha Khuwan"
        MediaPlayerFragment.FAV = false
        MediaPlayerFragment.loopStatus = 0
        MediaPlayerFragment.isLoading = true
        MediaPlayerFragment.isPlaying = false
        MediaPlayerFragment.isShuffleEnabled = false
        MediaPlayerFragment.duration = 0
        onDestroy()
    }

    //Other Helping Functions END
}
