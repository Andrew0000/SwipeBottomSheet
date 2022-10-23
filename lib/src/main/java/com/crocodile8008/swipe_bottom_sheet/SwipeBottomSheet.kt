package com.crocodile8008.swipe_bottom_sheet

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.crocodile8008.R

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class SwipeBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    var thresholdToInterceptDrag = 6.toPx()

    var isSwipeLocked = false

    var clip: Clip? = null

    var swipeListener: SwipeListener? = null

    var backFactor = 0.5f
        set(@FloatRange(from = 0.0, to = 1.0) swipeBackFactor) {
            field = swipeBackFactor.coerceIn(0.0f, 1.0f)
        }
    var bgAlpha = 125
        set(@IntRange(from = 0, to = 255) maskAlpha) {
            field = maskAlpha.coerceIn(0, 255)
        }

    private val swipeFinishListeners = mutableListOf<() -> Unit>()

    private val clipPath = Path()

    private val velocity: VelocityTracker = VelocityTracker.obtain()

    private var innerScrollView: View? = null

    private var innerWidth = 0

    private var innerHeight = 0

    private var swipeBackFraction = 0f

    private var leftOffset = 0

    private var topOffset = 0

    private var startYTouch = 0

    private var nestedScrollStarted = 0

    private var returnAnimation: ValueAnimator? = null

    private lateinit var dragContentView: View

    init {
        setWillNotDraw(false)
        swipeListener = createDefaultSwipeBackListener()
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeBottomSheet)
        backFactor = a.getFloat(R.styleable.SwipeBottomSheet_backFactor, this.backFactor)
        bgAlpha = a.getInteger(R.styleable.SwipeBottomSheet_bgAlpha, this.bgAlpha)
        a.recycle()
    }

    fun addSwipeFinishListener(listener: () -> Unit) {
        swipeFinishListeners.add(listener)
    }

    fun removeSwipeFinishListener(listener: () -> Unit) {
        swipeFinishListeners.remove(listener)
    }

    fun resetNestedPosition() {
        scrollY = 0
    }

    fun animateToTop() {
        animateToEnd(shouldClose = false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        if (childCount > 1) {
            throw IllegalStateException("SwipeBottomSheet must contains only one direct child.")
        }
        var defaultMeasuredWidth = 0
        var defaultMeasuredHeight = 0
        if (childCount > 0) {
            measureChildren(widthMeasureSpec, heightMeasureSpec)
            dragContentView = getChildAt(0)
            defaultMeasuredWidth = dragContentView.measuredWidth
            defaultMeasuredHeight = dragContentView.measuredHeight
        }
        val measuredWidth = View.resolveSize(defaultMeasuredWidth, widthMeasureSpec) + paddingLeft + paddingRight
        val measuredHeight = View.resolveSize(defaultMeasuredHeight, heightMeasureSpec) + paddingTop + paddingBottom

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) return

        val left = paddingLeft + leftOffset
        val top = paddingTop + topOffset
        val right = left + dragContentView.measuredWidth
        val bottom = top + dragContentView.measuredHeight
        dragContentView.layout(left, top, right, bottom)

        if (changed) {
            innerWidth = width
            innerHeight = height
        }
        innerScrollView = Utils.findScrollableView(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawARGB(this.bgAlpha - (this.bgAlpha * swipeBackFraction).toInt(), 0, 0, 0)
    }

    override fun dispatchDraw(canvas: Canvas) {
        clip?.let {
            clipPath.reset()
            clipPath.addRoundRect(
                0f,
                it.paddingTop,
                width.toFloat(),
                height + it.cornersRadiusTop,
                it.cornersRadiusTop,
                it.cornersRadiusTop,
                Path.Direction.CW
            )
            canvas.clipPath(clipPath)
        }
        super.dispatchDraw(canvas)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (isSwipeLocked) {
            return super.onInterceptTouchEvent(event)
        }
        android.util.Log.i("test_ ", "onInterceptTouchEvent nestedScrollStarted: $nestedScrollStarted / ${event.y} / $startYTouch") //TODO del
        if (nestedScrollStarted <= 0 && (event.y - startYTouch) > thresholdToInterceptDrag) {
            return true
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (isSwipeLocked) {
            return super.dispatchTouchEvent(event)
        }
        velocity.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startYTouch = event.y.toInt()
            }
            MotionEvent.ACTION_UP -> run {
                if (scrollY == 0) {
                    return@run
                }

                velocity.computeCurrentVelocity(1)
                val velocityValue = velocity.yVelocity
                velocity.clear()

                val shouldClose = scrollY - (velocityValue * (height / 50)) < - height * backFactor
                animateToEnd(shouldClose)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isSwipeLocked) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val y = event.y.toInt()
                scrollY = startYTouch - y
                preventOverScrolling()
            }
        }
        return true
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        if (isSwipeLocked) {
            return super.onStartNestedScroll(child, target, nestedScrollAxes)
        }
        nestedScrollStarted++
        return true
    }

    override fun onStopNestedScroll(child: View) {
        nestedScrollStarted--
        if (nestedScrollStarted < 0) {
            // Should not happen
            nestedScrollStarted = 0
        }
        super.onStopNestedScroll(child)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (isSwipeLocked) {
            return
        }
        if (scrollY <= 0) {
            if (dyConsumed > 0 && scrollY != 0) {
                scrollY += dyConsumed
                target.scrollBy(0, -dyConsumed)
            } else {
                scrollY += dyUnconsumed
            }

            cancelPreviousAnimation()
            preventOverScrolling()
        } else {
            scrollY = 0
        }
    }

    private fun animateToEnd(shouldClose: Boolean) {
        cancelPreviousAnimation()

        val endY = if (shouldClose) -height.toFloat() else 0f
        returnAnimation = ValueAnimator.ofFloat(scrollY.toFloat(), endY).apply {
            addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                scrollY = value.toInt()
                invalidate()
                if (value >= 0) {
                    swipeListener?.onSwipeFinished(this@SwipeBottomSheet, isBottom = false)
                } else if (value <= -height && shouldClose) {
                    swipeFinishListeners.forEach { it.invoke() }
                    swipeListener?.onSwipeFinished(this@SwipeBottomSheet, isBottom = true)
                }
            }
            interpolator = AccelerateDecelerateInterpolator()
            duration = 120
            start()
        }
    }

    private fun createDefaultSwipeBackListener() = object : SwipeListener {
        override fun onPositionChanged(view: View?, swipeBackFraction: Float, swipeBackFactor: Float) {
            invalidate()
        }

        override fun onSwipeFinished(view: View?, isBottom: Boolean) {
            //Empty
        }
    }

    private fun cancelPreviousAnimation() {
        returnAnimation?.apply {
            cancel()
            returnAnimation = null
        }
    }

    private fun preventOverScrolling() {
        if (scrollY > 0) {
            scrollY = 0
        }
    }

    data class Clip(
        val paddingTop: Float,
        val cornersRadiusTop: Float
    )

    interface SwipeListener {

        fun onPositionChanged(view: View?, swipeBackFraction: Float, swipeBackFactor: Float) {}

        fun onSwipeFinished(view: View?, isBottom: Boolean) {}
    }
}
