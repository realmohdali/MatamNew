package com.matamapp.matam.mediaPlayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.matamapp.matam.CommonData
import com.matamapp.matam.fragments.MediaPlayerFragment
import com.matamapp.matam.fragments.MediaPlayerFragment.Companion.isPlaying
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_END
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_START
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAYER_PREPARED
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.NEW_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PAUSE_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAY_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_TO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_UPDATE
import java.io.IOException

class PlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private val broadcastManager = LocalBroadcastManager.getInstance(this)

    private lateinit var audioManager: AudioManager
    var mediaPlayer: MediaPlayer? = null

    private var trackURL = ""
    private var resumePosition = 0

    private val mAudioAttributes =
        AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA).build()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //Service LifeCycle Functions

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (intent != null) {
                trackURL = formatURL(intent.getStringExtra("track_url").toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        trackURL = formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)

        if (!requestAudioFocus()) {
            stopSelf()
        }

        if (trackURL != "") {
            initMediaPlayer()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        CommonData.serviceRunning = true

        //Register Listeners
        registerPlayAudioListener()
        registerPauseAudioListener()
        registerSeekToListener()
    }

    override fun onDestroy() {
        stopMedia()
        mediaPlayer?.release()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            removeAudioFocus()
        }

        QueueManagement.currentQueue.clear()
        QueueManagement.currentPosition = -1
        CommonData.serviceRunning = false

        //Unregister Listeners
        broadcastManager.unregisterReceiver(playAudioListener)
        broadcastManager.unregisterReceiver(pauseAudioListener)
        broadcastManager.unregisterReceiver(seekToListener)


        super.onDestroy()
    }

    //Service LifeCycle Functions End

    //Media Player Overridden Functions
    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        stopSelf()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        playMedia()
        MediaPlayerFragment.duration = mp!!.duration
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d("Media Player Error", "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK")
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                Log.d("Media Player Error", "MEDIA_ERROR_SERVER_DIED")
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Log.d("Media Player Error", "MEDIA_ERROR_SERVER_DIED")
            }
        }
        return false
    }

    override fun onSeekComplete(mp: MediaPlayer?) {

    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                MediaPlayerFragment.isLoading = true
                isPlaying = false
                broadcastManager.sendBroadcast(Intent(BUFFERING_START))
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                MediaPlayerFragment.isLoading = false
                isPlaying = true
                broadcastManager.sendBroadcast(Intent(BUFFERING_END))
            }
        }
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {

    }
    //Media Player Overridden Functions End


    //Media Player Init and Controller Functions

    private fun initMediaPlayer() {
        setUI()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnBufferingUpdateListener(this)
        mediaPlayer?.setOnSeekCompleteListener(this)
        mediaPlayer?.setOnInfoListener(this)

        mediaPlayer?.reset()

        mediaPlayer?.apply { setAudioAttributes(mAudioAttributes) }

        try {
            mediaPlayer?.setDataSource(trackURL)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }

        mediaPlayer?.prepareAsync()
    }

    private fun playMedia() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
            isPlaying = true
            MediaPlayerFragment.isLoading = false
            MediaPlayerFragment.duration = mediaPlayer!!.duration
            broadcastManager.sendBroadcast(Intent(PLAYER_PREPARED))
            sendSeekUpdate()
        }
    }

    private fun stopMedia() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer?.stop()
            isPlaying = false
        }
    }

    private fun resumeMedia() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.seekTo(resumePosition)
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun pauseMedia() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
            resumePosition = mediaPlayer!!.currentPosition
            isPlaying = false
        }
    }

    //Media Player Init and Controller Functions End

    //Audio Focus Related Functions
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mediaPlayer == null) {
                    initMediaPlayer()
                } else if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer?.start()
                }
                mediaPlayer?.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if ((mediaPlayer != null)) {
                    if (mediaPlayer!!.isPlaying) {
                        mediaPlayer?.stop()
                        mediaPlayer?.reset()
                        mediaPlayer = null
                    }
                }

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.setVolume(0.3f, 0.3f)
                }
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val focusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mAudioAttributes).setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this).build()
        } else {
            null
        }

        @Suppress("DEPRECATION") val audioFocusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            audioManager.requestAudioFocus(
                this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
        }

        return audioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocusRequest(
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build()
        )
    }

    //Audio Focus Related Functions End

    //Broadcasts Related Functions

    private val playAudioListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            resumeMedia()
            print("Intent broadcast received PLAY_AUDIO")
        }
    }

    private fun registerPlayAudioListener() {
        val filter = IntentFilter(PLAY_AUDIO)
        broadcastManager.registerReceiver(playAudioListener, filter)
    }

    private val pauseAudioListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pauseMedia()
            print("Intent broadcast received PAUSE_AUDIO")
            stopMedia()
        }
    }

    private fun registerPauseAudioListener() {
        val filter = IntentFilter(PAUSE_AUDIO)
        broadcastManager.registerReceiver(pauseAudioListener, filter)
    }

    private val seekToListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val seekPosition = intent?.getIntExtra("seek_position", 0)
            mediaPlayer?.seekTo(seekPosition!!)
        }
    }

    private fun registerSeekToListener() {
        val filter = IntentFilter(SEEK_TO)
        broadcastManager.registerReceiver(seekToListener, filter)
    }

    //Broadcasts Related Functions End


    //Set Player UI Variables Function

    private fun setUI() {
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

        broadcastManager.sendBroadcast(Intent(NEW_AUDIO))
    }

    //Set Player UI Variables Function END

    //Seek Update
    private fun sendSeekUpdate() {
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if (isPlaying) {
                val intent = Intent(SEEK_UPDATE)
                intent.putExtra("current_position", mediaPlayer?.currentPosition)
                broadcastManager.sendBroadcast(intent)
            }
        }, 1000)
    }
    //Seek Update END

    private fun formatURL(rawURL: String): String {
        val formattedURL = rawURL.replace(" ", "%20")
        return "https://$formattedURL"
    }

}
