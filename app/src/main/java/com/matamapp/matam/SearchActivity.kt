package com.matamapp.matam

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matamapp.matam.adapters.AlbumAdapter
import com.matamapp.matam.adapters.NauhaKhuwanAdapter

class SearchActivity : AppCompatActivity() {

    private var hasText = false
    private val context = this
    private lateinit var nauhaKhuwanRecyclerView: RecyclerView
    private lateinit var tracksRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchBox: EditText = findViewById(R.id.search_box)
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            if (hasText) {
                searchBox.setText("")
            } else {
                finish()
            }
        }

        searchBox.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    hasText = true
                    backButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_clear_24
                        )
                    )
                } else {
                    hasText = false
                    backButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_arrow_back_24
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        nauhaKhuwanRecyclerView = findViewById(R.id.nauha_khuwan_recyclerview)
        tracksRecyclerView = findViewById(R.id.tracks_recyclerview)

        nauhaKhuwanRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tracksRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //val nauhaKhuwanAdapter = NauhaKhuwanAdapter(this)
        //val albumAdapter = AlbumAdapter()

        //nauhaKhuwanRecyclerView.adapter = nauhaKhuwanAdapter
        //tracksRecyclerView.adapter = albumAdapter
    }
}