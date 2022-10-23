package com.crocodile8008

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crocodile8008.swipe_bottom_sheet.SwipeBottomSheet
import com.crocodile8008.swipe_bottom_sheet.toPx

class BottomSheetWithRecyclerFromCodeSample(
    private val layoutRootView: ViewGroup,
) {

    var bottomSheet: SwipeBottomSheet? = null

    fun showBottomSheet() {
        if (bottomSheet != null) {
            return
        }
        val recycler = RecyclerView(layoutRootView.context).apply {
            layoutManager = LinearLayoutManager(layoutRootView.context)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setPadding(0, 80f.toPx().toInt(), 0, 0)
        }
        val adapter = createAdapter(listOf(1, 2, 3, 4, 5))
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

    private fun createAdapter(items: List<Int>) =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(
                    TextView(parent.context).apply {
                        textSize = 24f
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

    private fun removeBottomSheet() {
        if (bottomSheet == null) {
            return
        }
        layoutRootView.removeView(bottomSheet)
        bottomSheet = null
    }
}