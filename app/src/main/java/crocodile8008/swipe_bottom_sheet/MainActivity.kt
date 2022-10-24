package crocodile8008.swipe_bottom_sheet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private lateinit var layoutRootView: ViewGroup
    private lateinit var bottomSheet1: SwipeBottomSheet

    private lateinit var bottomSheetFromCodeSample: BottomSheetFromCodeSample
    private lateinit var bottomSheetWithRecyclerFromCodeSample: BottomSheetWithRecyclerFromCodeSample
    private lateinit var bottomSheetWithFragmentSample: BottomSheetWithFragmentSample

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
            if (bottomSheetWithRecyclerFromCodeSample.bottomSheet != null) {
                bottomSheetWithRecyclerFromCodeSample.bottomSheet?.animateToBottom()
                return
            }
            if (bottomSheetWithFragmentSample.bottomSheet != null) {
                bottomSheetWithFragmentSample.bottomSheet?.animateToBottom()
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
        bottomSheetWithRecyclerFromCodeSample = BottomSheetWithRecyclerFromCodeSample(layoutRootView)
        bottomSheetWithFragmentSample = BottomSheetWithFragmentSample(this, layoutRootView)
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    private fun setupView() {
        layoutRootView = findViewById(R.id.rootView)
        bottomSheet1 = findViewById(R.id.swipeBottomSheet)
        val showButton1 = findViewById<Button>(R.id.showButton1)
        val showButton2 = findViewById<Button>(R.id.showButton2)
        val showButton3 = findViewById<Button>(R.id.showButton3)
        val showButton4 = findViewById<Button>(R.id.showButton4)

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

        showButton3.setOnClickListener {
            bottomSheetWithRecyclerFromCodeSample.showBottomSheet()
        }

        showButton4.setOnClickListener {
            bottomSheetWithFragmentSample.showBottomSheet()
        }
    }

}
