package com.matamapp.matam

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SearchActivity : AppCompatActivity() {

    var hasText = false
    val context = this

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
                //TODO("Not yet implemented")
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
                //TODO("Not yet implemented")
            }

        })
    }
}