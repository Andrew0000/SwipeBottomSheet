package com.crocodile8008

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import com.crocodile8008.swipe_bottom_sheet.SwipeBottomSheet
import com.crocodile8008.swipe_bottom_sheet.toPx

class MainActivity : AppCompatActivity() {

    private lateinit var layoutRootView: ViewGroup
    private lateinit var bottomSheet1: SwipeBottomSheet
    private var bottomSheet2: SwipeBottomSheet? = null

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (bottomSheet1.isVisible) {
                bottomSheet1.animateToBottom()
                return
            }
            if (bottomSheet2 != null) {
                bottomSheet2?.animateToBottom()
                return
            }
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    private fun setupView() {
        layoutRootView = findViewById(R.id.rootView)
        bottomSheet1 = findViewById(R.id.swipeBottomSheet)
        val showButton1 = findViewById<Button>(R.id.showButton1)
        val showButton2 = findViewById<Button>(R.id.showButton2)

        bottomSheet1.apply {
            clip = SwipeBottomSheet.Clip(
                paddingTop = 40f.toPx(),
                cornersRadiusTop = 16f.toPx(),
            )
            swipeListener = object : SwipeBottomSheet.SwipeListener {
                override fun onSwipeFinished(view: View, isBottom: Boolean) {
                    if (isBottom) {
                        visibility = View.INVISIBLE
                    }
                }
            }
        }

        showButton1.setOnClickListener {
            bottomSheet1.visibility = View.VISIBLE
            bottomSheet1.animateToTop()
        }

        showButton2.setOnClickListener {
            if (bottomSheet2 == null) {
                showBottomSheet2()
            } else {
                removeBottomSheet2(bottomSheet2)
            }
        }
    }

    private fun showBottomSheet2() {
        bottomSheet2 = SwipeBottomSheet
            .wrapNested(LayoutInflater.from(this).inflate(R.layout.content_2, layoutRootView, false))
            .apply {
                elevation = 6.toPx()
                clip = SwipeBottomSheet.Clip(
                    paddingTop = 80f.toPx(),
                    cornersRadiusTop = 40f.toPx(),
                )
                addSwipeFinishListener { removeBottomSheet2(this) }
                animateAppearance(parentView = layoutRootView)
            }
        layoutRootView.addView(bottomSheet2)
    }

    private fun removeBottomSheet2(bs: SwipeBottomSheet?) {
        layoutRootView.removeView(bs)
        bottomSheet2 = null
    }
}