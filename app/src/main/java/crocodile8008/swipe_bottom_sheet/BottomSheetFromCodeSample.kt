package crocodile8008.swipe_bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup

class BottomSheetFromCodeSample(
    private val layoutRootView: ViewGroup,
) {

    var bottomSheet: SwipeBottomSheet? = null

    fun showBottomSheet() {
        if (bottomSheet != null) {
            return
        }
        bottomSheet = SwipeBottomSheet
            .wrapNested(
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