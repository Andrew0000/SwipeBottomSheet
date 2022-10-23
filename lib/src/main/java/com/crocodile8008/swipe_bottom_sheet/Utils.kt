package com.crocodile8008.swipe_bottom_sheet

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

internal object Utils {

    fun findScrollableView(viewGroup: ViewGroup): View? {
        for (i in 0 until viewGroup.childCount) {
            var child: View? = viewGroup.getChildAt(i)
            if (child!!.visibility != View.VISIBLE) {
                continue
            }
            if (isScrollable(child)) {
                return child
            }
            if (child is ViewGroup) {
                child = findScrollableView(child)
                if (child != null) {
                    return child
                }
            }
        }
        return null
    }

    private fun isScrollable(view: View): Boolean {
        return view is ScrollView
                || view is HorizontalScrollView
                || view is NestedScrollView
                || view is AbsListView
                || view is RecyclerView
                || view is ViewPager
                || view is WebView
    }
}

fun Int.toPx() =
    toFloat().toPx()

fun Float.toPx() =
    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)).toFloat()