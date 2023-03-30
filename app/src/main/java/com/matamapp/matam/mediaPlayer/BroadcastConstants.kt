package com.matamapp.matam.mediaPlayer

class BroadcastConstants {
    companion object{
        const val NEW_AUDIO = "com.matamapp.matam.mediaPlayer.NEW_AUDIO"
        const val PLAYER_PREPARED = "com.matamapp.matam.mediaPlayer.LOADING_COMPLETE"
        const val BUFFERING_START = "com.matamapp.matam.mediaPlayer.BUFFERING_START"
        const val BUFFERING_END = "com.matamapp.matam.mediaPlayer.BUFFERING_END"
        const val BUFFERING_UPDATE = "com.matamapp.matam.mediaPlayer.BUFFERING_UPDATE"
        const val PLAY_AUDIO = "com.matamapp.matam.mediaPlayer.PLAY_AUDIO"
        const val PAUSE_AUDIO = "com.matamapp.matam.mediaPlayer.PAUSE_AUDIO"
        const val STOP_AUDIO = "com.matamapp.matam.mediaPlayer.STOP_AUDIO"
        const val SEEK_UPDATE = "com.matamapp.matam.mediaPlayer.SEEK_UPDATE"
        const val SEEK_TO = "com.matamapp.matam.mediaPlayer.SEEK_TO"
        const val NEXT_TRACK = "com.matamapp.matam.mediaPlayer.NEXT_TRACK"
        const val PREVIOUS_TRACK = "com.matamapp.matam.mediaPlayer.PREVIOUS_TRACK"
        const val RESET_PLAYER = "com.matamapp.matam.mediaPlayer.RESET_PLAYER"
        const val SHUFFLE_DISABLED = "com.matamapp.matam.mediaPlayer.RESET_PLAYER"
        const val CHANGE_TRACK = "com.matamapp.matam.mediaPlayer.RESET_PLAYER"
        const val PLAY_PAUSE_STATUS_UPDATE = "com.matamapp.matam.mediaPlayer.PLAY_PAUSE_STATUS_UPDATE"
        const val MEDIA_VOLUME_UPDATE = "com.matamapp.matam.mediaPlayer.MEDIA_VOLUME_UPDATE"

        const val NO_LOOP = 0
        const val LOOP_ALL = 1
        const val LOOP_ONE = 2

    }
}