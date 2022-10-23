package com.crocodile8008.swipe_bottom_sheet

import android.content.res.Resources
import android.util.TypedValue

fun Int.toPx() =
    toFloat().toPx()

fun Float.toPx() =
    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)).toFloat()