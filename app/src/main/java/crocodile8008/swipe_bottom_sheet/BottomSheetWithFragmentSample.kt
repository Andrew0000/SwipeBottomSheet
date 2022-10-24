package crocodile8008.swipe_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class BottomSheetWithFragmentSample(
    private val activity: FragmentActivity,
    private val layoutRootView: ViewGroup,
) {

    var bottomSheet: SwipeBottomSheet? = null

    fun showBottomSheet() {
        if (bottomSheet != null) {
            return
        }
        bottomSheet = SwipeBottomSheet
            .wrapNested(
                FrameLayout(layoutRootView.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    id = R.id.fragment_frame
                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(id, BSFragment())
                        .commit()
                }
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

class BSFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_3, container, false)
    }
}