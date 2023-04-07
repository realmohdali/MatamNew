package com.matamapp.matam.mediaPlayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.*
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.matamapp.matam.CommonData
import com.matamapp.matam.CommonData.Companion.NOTIFICATION_CHANNEL_ID
import com.matamapp.matam.CommonData.Companion.PREFERENCES
import com.matamapp.matam.R
import com.matamapp.matam.fragments.MediaPlayerFragment
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_END
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_START
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.BUFFERING_UPDATE
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.CHANGE_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.MEDIA_VOLUME_UPDATE
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.NEXT_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PAUSE_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAYER_PREPARED
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAY_AUDIO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PLAY_PAUSE_STATUS_UPDATE
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.PREVIOUS_TRACK
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.RESET_PLAYER
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_TO
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SEEK_UPDATE
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.SHUFFLE_DISABLED
import com.matamapp.matam.mediaPlayer.BroadcastConstants.Companion.STOP_AUDIO
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MediaPlayerService : Service() {
    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    private var executors: ExecutorService? = null

    private var shuffledPositions: MutableList<Int> = mutableListOf()
    private var lastPlayed = -1

    private lateinit var mediaPlayer: MediaPlayer
    private var trackURL = ""
    private var resumePosition = 0
    private val mAudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()

    private var hasAudioFocus = false
    private var wasPlaying = false


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
        registerShuffleListener()
        registerChangeTrackListener()
        registerUpdateVolumeListener()
        registerStopServiceListener()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
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
        localBroadcastManager.unregisterReceiver(shuffleListener)
        localBroadcastManager.unregisterReceiver(changeTrackListener)
        localBroadcastManager.unregisterReceiver(updateVolumeListener)
        localBroadcastManager.unregisterReceiver(stopServiceListener)

        localBroadcastManager.sendBroadcast(Intent(RESET_PLAYER))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        super.onDestroy()
    }

    //Init Player

    private fun initMediaPlayer() {
        setUpUI()
        if (!hasAudioFocus) {
            requestAudioFocus()
        }
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
            lastPlayed = QueueManagement.currentPosition
            updateVolume()
            playMedia()
            localBroadcastManager.sendBroadcast(Intent(PLAYER_PREPARED))
            getBitmap()
        }
        mediaPlayer.setOnCompletionListener {
            //Track Completed

            if (MediaPlayerFragment.loopStatus == BroadcastConstants.LOOP_ONE) {
                mediaPlayer.seekTo(0)
                playMedia()
            } else {
                if (MediaPlayerFragment.isShuffleEnabled) {
                    shuffledPositions.add(QueueManagement.currentPosition)
                    if (shuffledPositions.size == QueueManagement.currentQueue.size) {
                        //All tracks are played
                        if (MediaPlayerFragment.loopStatus == 1) {
                            shuffledPositions.clear()
                            var randomTrackPosition: Int
                            while (true) {
                                randomTrackPosition =
                                    Random.nextInt(0, QueueManagement.currentQueue.size - 1)
                                if (randomTrackPosition != lastPlayed) {
                                    break
                                }
                            }
                            QueueManagement.currentPosition = randomTrackPosition
                            trackURL =
                                formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                            stopMedia()
                            initMediaPlayer()
                        } else {
                            resetPlayer()
                        }
                    } else {
                        val randomTrackPosition = getRandomTrackPosition()
                        QueueManagement.currentPosition = randomTrackPosition
                        trackURL =
                            formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                        stopMedia()
                        initMediaPlayer()
                    }
                } else {
                    if (QueueManagement.currentPosition < QueueManagement.currentQueue.size - 1) {

                        QueueManagement.currentPosition++
                        trackURL =
                            formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                        stopMedia()
                        initMediaPlayer()

                    } else {
                        when (MediaPlayerFragment.loopStatus) {
                            BroadcastConstants.NO_LOOP -> {
                                resetPlayer()
                            }
                            BroadcastConstants.LOOP_ALL -> {
                                QueueManagement.currentPosition = 0
                                trackURL =
                                    formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
                                stopMedia()
                                initMediaPlayer()
                            }
                        }
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
            true
        }
        mediaPlayer.setOnBufferingUpdateListener { _, percent ->
            //Buffering status
            val intent = Intent(BUFFERING_UPDATE)
            intent.putExtra("buffering_update", percent)
            localBroadcastManager.sendBroadcast(intent)
        }

        mediaPlayer.setOnErrorListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                    println("MEDIA_ERROR_UNKNOWN")
                }
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                    println("MEDIA_ERROR_SERVER_DIED")
                }
                MediaPlayer.MEDIA_ERROR_IO -> {
                    println("MEDIA_ERROR_IO")
                }
                MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                    println("MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK")
                }
                MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                    println("MEDIA_ERROR_MALFORMED")
                }
                MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                    println("MEDIA_ERROR_TIMED_OUT")
                }
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                    println("MEDIA_ERROR_UNSUPPORTED")
                }
            }
            false
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
            executors?.shutdown()
            getBitmap()
            localBroadcastManager.sendBroadcast(Intent(PLAY_PAUSE_STATUS_UPDATE))
        }
    }

    private fun resumeMedia() {
        if (!mediaPlayer.isPlaying) {
            if (!hasAudioFocus) {
                requestAudioFocus()
            }
            mediaPlayer.seekTo(resumePosition)
            mediaPlayer.start()
            MediaPlayerFragment.isPlaying = true
            seekUpdate()
            getBitmap()
            localBroadcastManager.sendBroadcast(Intent(PLAY_PAUSE_STATUS_UPDATE))
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
        if (executors != null) {
            executors?.shutdown()
        }
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
                resumePosition = seekPosition
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

    private val shuffleListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            shuffledPositions.clear()
        }
    }

    private fun registerShuffleListener() {
        localBroadcastManager.registerReceiver(shuffleListener, IntentFilter(SHUFFLE_DISABLED))
    }

    private val changeTrackListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            trackURL =
                formatURL(QueueManagement.currentQueue[QueueManagement.currentPosition].trackURl)
            stopMedia()
            initMediaPlayer()
        }
    }

    private fun registerChangeTrackListener() {
        localBroadcastManager.registerReceiver(changeTrackListener, IntentFilter(CHANGE_TRACK))
    }

    private val updateVolumeListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateVolume()
        }
    }

    private fun registerUpdateVolumeListener() {
        localBroadcastManager.registerReceiver(
            updateVolumeListener,
            IntentFilter(MEDIA_VOLUME_UPDATE)
        )
    }

    private val stopServiceListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            resetPlayer()
        }
    }

    private fun registerStopServiceListener() {
        localBroadcastManager.registerReceiver(stopServiceListener, IntentFilter(STOP_AUDIO))
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
        (executors!! as ScheduledExecutorService?)?.scheduleAtFixedRate(
            runnable, 0, 1, TimeUnit.SECONDS
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

    private fun getRandomTrackPosition(): Int {
        while (true) {
            val randomTrackPosition = Random.nextInt(0, QueueManagement.currentQueue.size - 1)
            var foundInList = false
            for (i in shuffledPositions) {
                if (i == randomTrackPosition) {
                    foundInList = true
                }
            }
            if (!foundInList) {
                return randomTrackPosition
            }
        }
    }

    private fun requestAudioFocus() {
        val audioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AUDIOFOCUS_GAIN -> {
                    hasAudioFocus = true
                    mediaPlayer.setVolume(1f, 1f)
                    if (wasPlaying) {
                        resumeMedia()
                    }
                }
                AUDIOFOCUS_LOSS -> {
                    hasAudioFocus = false
                    wasPlaying = if (mediaPlayer.isPlaying) {
                        pauseMedia()
                        true
                    } else {
                        false
                    }
                }
                AUDIOFOCUS_LOSS_TRANSIENT -> {
                    hasAudioFocus = false
                    wasPlaying = if (mediaPlayer.isPlaying) {
                        pauseMedia()
                        true
                    } else {
                        false
                    }
                }
                AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.setVolume(0.3f, 0.3f)
                    }
                }
            }
        }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest.Builder(AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setOnAudioFocusChangeListener(audioFocusChangeListener)
                build()
            }
            val res = audioManager.requestAudioFocus(focusRequest)
            synchronized(Any()) {
                hasAudioFocus = when (res) {
                    AUDIOFOCUS_REQUEST_FAILED -> false
                    AUDIOFOCUS_REQUEST_GRANTED -> true
                    else -> false
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                STREAM_MUSIC,
                AUDIOFOCUS_GAIN
            )
            hasAudioFocus = result == AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun updateVolume() {
        val sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val volume = sharedPreferences.getFloat("volumeLevel", 0.8f)
        mediaPlayer.setVolume(volume, volume)
    }

    //Other Helping Functions END

    //Media Notification

    @SuppressLint("NewApi")
    private fun setUpNotification(bitmap: Bitmap?) {
        //Check Notification Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enable Notification")
                .setMessage("Notifications are required for proper functioning of the application")
                .setPositiveButton("Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
            builder.show()
        }

        // Create PendingIntent for the actions
        val pauseIntent = Intent(this, NotificationActionReceiver::class.java)
        pauseIntent.action = PAUSE_AUDIO
        val pausePendingIntent = PendingIntent.getBroadcast(
            this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val previousIntent = Intent(this, NotificationActionReceiver::class.java)
        previousIntent.action = PREVIOUS_TRACK
        val previousPendingIntent = PendingIntent.getBroadcast(
            this, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = Intent(this, NotificationActionReceiver::class.java)
        nextIntent.action = NEXT_TRACK
        val nextPendingIntent = PendingIntent.getBroadcast(
            this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val playIntent = Intent(this, NotificationActionReceiver::class.java)
        playIntent.action = PLAY_AUDIO
        val playPendingIntent = PendingIntent.getBroadcast(
            this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, NotificationActionReceiver::class.java)
        stopIntent.action = STOP_AUDIO
        val stopPendingIntent = PendingIntent.getBroadcast(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )


        val mediaSession = MediaSessionCompat(this, "Matam Media Session")
        val mediaMetadata = MediaMetadataCompat.Builder()
            .putString(
                MediaMetadata.METADATA_KEY_TITLE,
                QueueManagement.currentQueue[QueueManagement.currentPosition].title
            )
            .putString(
                MediaMetadata.METADATA_KEY_ARTIST,
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.name
            )
            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
            .build()


        mediaSession.setMetadata(mediaMetadata)

        // Build the notification with the pause action
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(QueueManagement.currentQueue[QueueManagement.currentPosition].title)
            .setContentText(QueueManagement.currentQueue[QueueManagement.currentPosition].artist.name)
            .setSmallIcon(R.drawable.ic_baseline_queue_music_24)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(android.R.drawable.ic_media_previous, "Previous", previousPendingIntent)
        if (mediaPlayer.isPlaying) {
            notification
                .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent)
        } else {
            notification
                .addAction(android.R.drawable.ic_media_play, "Play", playPendingIntent)
        }
        notification.addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
        notification.addAction(R.drawable.baseline_stop_24, "Stop", stopPendingIntent)

        startForeground(1001, notification.build())
    }

    private fun getBitmap() {
        var bitmap: Bitmap?
        val image =
            if (QueueManagement.currentQueue[QueueManagement.currentPosition].trackImage != "null") {
                QueueManagement.currentQueue[QueueManagement.currentPosition].trackImage
            } else {
                QueueManagement.currentQueue[QueueManagement.currentPosition].artist.image
            }

        try {
            Thread {
                // Do network action in this function
                val url = URL(image)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
                setUpNotification(bitmap)
            }.start()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    //Media Notification END
}

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            PAUSE_AUDIO -> {
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(PAUSE_AUDIO))
            }
            NEXT_TRACK -> {
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(NEXT_TRACK))
            }
            PREVIOUS_TRACK -> {
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(PREVIOUS_TRACK))
            }
            PLAY_AUDIO -> {
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(PLAY_AUDIO))
            }
            STOP_AUDIO -> {
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(STOP_AUDIO))
            }
        }
    }
}