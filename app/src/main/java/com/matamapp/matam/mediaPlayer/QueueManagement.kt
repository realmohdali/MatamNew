package com.matamapp.matam.mediaPlayer

import com.matamapp.matam.data.TrackData

class QueueManagement {
    companion object{
        var currentQueue: MutableList<TrackData> = mutableListOf()
        var currentPosition = -1

        fun addToQueue(track: TrackData) {
            val id = track.id
            var exists = false
            for (queueTrack in currentQueue) {
                if(id == queueTrack.id) {
                    exists = true
                    break
                }
            }
            if(!exists) {
                currentQueue.add(track)
            }
        }

        fun existsInQueue(track: TrackData) : Boolean {
            val id = track.id
            var exists = false
            for (queueTrack in currentQueue) {
                if(id == queueTrack.id) {
                    exists = true
                    break
                }
            }
            return exists
        }
    }
}