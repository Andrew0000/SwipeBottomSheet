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

    private lateinit var bottomSheetFromCodeSample: BottomSheetFromCodeSample

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (bottomSheet1.isVisible) {
                bottomSheet1.animateToBottom()
                return
            }
            if (bottomSheetFromCodeSample.bottomSheet != null) {
                bottomSheetFromCodeSample.bottomSheet?.animateToBottom()
                return
            }
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        bottomSheetFromCodeSample = BottomSheetFromCodeSample(layoutRootView)
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
            bottomSheetFromCodeSample.showBottomSheet()
        }
    }

}

private class BottomSheetFromCodeSample(
    private val layoutRootView: ViewGroup,
) {

    var bottomSheet: SwipeBottomSheet? = null

    fun showBottomSheet() {
        if (bottomSheet != null) {
            return
        }
        bottomSheet = SwipeBottomSheet
            .wrapNested(LayoutInflater.from(layoutRootView.context).inflate(R.layout.content_2, layoutRootView, false))
            .apply {
                elevation = 6.toPx()
                clip = SwipeBottomSheet.Clip(
                    paddingTop = 80f.toPx(),
                    cornersRadiusTop = 40f.toPx(),
                )
                addSwipeFinishListener { removeBottomSheet() }
                animateAppearance(parentView = layoutRootView)
            }
        layoutRootView.addView(bottomSheet)
    }

    private fun removeBottomSheet() {
        if (bottomSheet == null) {
            return
        }
        layoutRootView.removeView(bottomSheet)
        bottomSheet = null
    }
}