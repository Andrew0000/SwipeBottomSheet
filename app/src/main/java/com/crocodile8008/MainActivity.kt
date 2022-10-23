package com.crocodile8008

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.crocodile8008.swipe_bottom_sheet.SwipeBottomSheet
import com.crocodile8008.swipe_bottom_sheet.toPx

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun setupView() {
        val swipeBottomSheet = findViewById<SwipeBottomSheet>(R.id.swipeBottomSheet)
        val showButton = findViewById<Button>(R.id.showButton)

        swipeBottomSheet.apply {
            clip = SwipeBottomSheet.Clip(
                paddingTop = 40f.toPx(),
                cornersRadiusTop = 16f.toPx(),
            )
            swipeListener = object : SwipeBottomSheet.SwipeListener {
                override fun onSwipeFinished(view: View?, isBottom: Boolean) {
                    if (isBottom) {
                        visibility = View.INVISIBLE
                    }
                }
            }
        }

        showButton.setOnClickListener {
            swipeBottomSheet.visibility = View.VISIBLE
            swipeBottomSheet.animateToTop()
        }
    }
}