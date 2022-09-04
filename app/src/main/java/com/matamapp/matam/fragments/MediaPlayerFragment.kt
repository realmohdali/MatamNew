package com.matamapp.matam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.matamapp.matam.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MediaPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MediaPlayerFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MediaPlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MediaPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var value = 0
        var imageURL = ""
        var progress = 0
        var currentTime = ""
        var remainingTime = ""
        var trackTitle = ""
        var trackArtist = ""
        var volume = 0
        var PLAY = false
        var FAV = false
        var LOOP = 0
        var SHUFFLE = 0
    }

    private lateinit var playerView: View
    private lateinit var miniPlayer: View
    private var EXPANDED = false

    //Main player elements
    private lateinit var trackImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var currentTime: TextView
    private lateinit var remainingTime: TextView
    private lateinit var trackTitleView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var volumeBar: ProgressBar
    private lateinit var favButton: ImageView
    private lateinit var prevButton: ImageView
    private lateinit var playPause: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var loopButton: ImageView
    private lateinit var shuffleButton: ImageView
    private lateinit var lyricsButton: ImageButton
    private lateinit var queueButton: ImageView

    //Mini player elements
    private lateinit var trackImageViewMini: ImageView
    private lateinit var trackTitleMini: TextView
    private lateinit var artistNameMini: TextView
    private lateinit var prevButtonMini: ImageView
    private lateinit var playPauseMini: ImageView
    private lateinit var nextButtonMini: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
        setUpPlayer()
    }

    private fun setUpPlayer() {
        setUpClickListeners()
        setMiniPlayer()
    }

    private fun setMiniPlayer() {

    }

    private fun setUpClickListeners() {
        playerView.findViewById<ImageView>(R.id.playPause).setOnClickListener {
            value++
            playerView.findViewById<TextView>(R.id.trackTitle).text = value.toString()
        }
        playerView.findViewById<TextView>(R.id.trackTitle).text = value.toString()

        playerView.findViewById<ImageView>(R.id.miniPlayPause).setOnClickListener {
            Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show()
        }
    }

    fun scroll(alpha: Float) {
        miniPlayer.visibility = View.VISIBLE
        miniPlayer.alpha = alpha
    }

    fun collapsed() {
        miniPlayer.visibility = View.VISIBLE
        EXPANDED = false
    }

    fun expanded() {
        miniPlayer.visibility = View.GONE
        EXPANDED = true
    }
}