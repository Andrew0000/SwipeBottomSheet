package com.crocodile8008

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crocodile8008.swipe_bottom_sheet.SwipeBottomSheet
import com.crocodile8008.swipe_bottom_sheet.toPx

class MainActivity : AppCompatActivity() {

    private lateinit var layoutRootView: ViewGroup
    private lateinit var bottomSheet1: SwipeBottomSheet

    private lateinit var bottomSheetFromCodeSample: BottomSheetFromCodeSample
    private lateinit var bottomSheetWithRecyclerFromCodeSample: BottomSheetWithRecyclerFromCodeSample

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
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        bottomSheetFromCodeSample = BottomSheetFromCodeSample(layoutRootView)
        bottomSheetWithRecyclerFromCodeSample = BottomSheetWithRecyclerFromCodeSample(layoutRootView)
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    private fun setupView() {
        layoutRootView = findViewById(R.id.rootView)
        bottomSheet1 = findViewById(R.id.swipeBottomSheet)
        val showButton1 = findViewById<Button>(R.id.showButton1)
        val showButton2 = findViewById<Button>(R.id.showButton2)
        val showButton3 = findViewById<Button>(R.id.showButton3)

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

private class BottomSheetWithRecyclerFromCodeSample(
    private val layoutRootView: ViewGroup,
) {

    var bottomSheet: SwipeBottomSheet? = null

    fun showBottomSheet() {
        if (bottomSheet != null) {
            return
        }
        val items = listOf(1, 2, 3, 4, 5)
        val recycler = RecyclerView(layoutRootView.context).apply {
            layoutManager = LinearLayoutManager(layoutRootView.context)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setPadding(0, 80f.toPx().toInt(), 0, 0)
        }
        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(
                    TextView(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            300.toPx().toInt(),
                        )
                    }
                ) {}

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).apply {
                    val item = items[position]
                    text = "Item: $item"
                    setBackgroundColor(
                        if (item % 2 == 0) {
                            Color.RED
                        } else {
                            Color.GREEN
                        }
                    )
                }
            }

            override fun getItemCount(): Int =
                items.size
        }
        recycler.adapter = adapter
        bottomSheet = SwipeBottomSheet(layoutRootView.context)
            .apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                addView(recycler)
                elevation = 6.toPx()
                clip = SwipeBottomSheet.Clip(
                    paddingTop = 80f.toPx(),
                    cornersRadiusTop = 2f.toPx(),
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