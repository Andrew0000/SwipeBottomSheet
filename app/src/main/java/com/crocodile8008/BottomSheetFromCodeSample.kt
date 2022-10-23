package com.crocodile8008

import android.view.LayoutInflater
import android.view.ViewGroup
import crocodile8008.swipe_bottom_sheet.SwipeBottomSheet
import crocodile8008.swipe_bottom_sheet.toPx

class BottomSheetFromCodeSample(
    private val layoutRootView: ViewGroup,
) {

    var bottomSheet: SwipeBottomSheet? = null

    fun showBottomSheet() {
        if (bottomSheet != null) {
            return
        }
        bottomSheet = SwipeBottomSheet.wrapNested(
            LayoutInflater.from(layoutRootView.context).inflate(R.layout.content_2, layoutRootView, false)
        )
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