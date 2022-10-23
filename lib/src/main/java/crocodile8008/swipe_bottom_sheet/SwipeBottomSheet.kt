package crocodile8008.swipe_bottom_sheet

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
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import crocodile8008.R
import kotlin.math.abs

/**
 * BottomSheet that works well with [NestedScrollView] and [RecyclerView].
 *
 * You can place [NestedScrollView] inside in xml or create from code with [SwipeBottomSheet.wrapNested] function.
 *
 * Note: this is only translucent wrapper, actual background should be set in the content view.
 */
@Suppress("Unused", "MemberVisibilityCanBePrivate")
class SwipeBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    var isSwipeLocked = false

    var clip: Clip? = null

    var swipeListener: SwipeListener? = null

    var backFactor = 0.5f
        set(@FloatRange(from = 0.0, to = 1.0) backFactor) {
            field = backFactor.coerceIn(0.0f, 1.0f)
        }

    var bgAlpha = 125
        set(@IntRange(from = 0, to = 255) bgAlpha) {
            field = bgAlpha.coerceIn(0, 255)
        }

    var animationDuration = 150L

    private val thresholdToInterceptDrag = 6.toPx()

    private val swipeFinishListeners = mutableListOf<() -> Unit>()

    private val clipPath = Path()

    private val velocity: VelocityTracker = VelocityTracker.obtain()

    private var startYTouch = 0

    private var nestedScrollStarted = 0

    private var slideAnimation: ValueAnimator? = null

    private lateinit var innerContentView: View

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

    fun animateToBottom() {
        animateToEnd(shouldClose = true)
    }

    fun animateAppearance(parentView: View? = parent as? View) {
        scrollY = -(parentView?.measuredHeight ?: 0)
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
            innerContentView = getChildAt(0)
            defaultMeasuredWidth = innerContentView.measuredWidth
            defaultMeasuredHeight = innerContentView.measuredHeight
        }
        val measuredWidth = View.resolveSize(defaultMeasuredWidth, widthMeasureSpec) + paddingLeft + paddingRight
        val measuredHeight = View.resolveSize(defaultMeasuredHeight, heightMeasureSpec) + paddingTop + paddingBottom

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }
        val left = paddingLeft
        val top = paddingTop
        val right = left + innerContentView.measuredWidth
        val bottom = top + innerContentView.measuredHeight
        innerContentView.layout(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var bgAlphaFinal = bgAlpha
        if (height > 0) {
            val fraction = (1f - abs(scrollY.toFloat()) / height.toFloat()).coerceIn(0f, 1f)
            bgAlphaFinal = (bgAlpha * fraction).toInt()
        }
        canvas.drawARGB(bgAlphaFinal, 0, 0, 0)
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
        slideAnimation = ValueAnimator.ofFloat(scrollY.toFloat(), endY).apply {
            addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                scrollY = value.toInt()
                if (value >= 0 && !shouldClose) {
                    swipeListener?.onSwipeFinished(this@SwipeBottomSheet, isBottom = false)
                } else if (value <= -height && shouldClose) {
                    swipeFinishListeners.forEach { it.invoke() }
                    swipeListener?.onSwipeFinished(this@SwipeBottomSheet, isBottom = true)
                }
            }
            interpolator = AccelerateDecelerateInterpolator()
            duration = animationDuration
            start()
        }
    }

    private fun createDefaultSwipeBackListener() = object : SwipeListener {
        override fun onSwipeFinished(view: View, isBottom: Boolean) {
            //Empty
        }
    }

    private fun cancelPreviousAnimation() {
        slideAnimation?.apply {
            cancel()
            slideAnimation = null
        }
    }

    private fun preventOverScrolling() {
        if (scrollY > 0) {
            scrollY = 0
        }
    }

    companion object {

        /**
         * Wraps given [View] with [NestedScrollView] and puts it into [SwipeBottomSheet].
         */
        fun wrapNested(view: View): SwipeBottomSheet {
            val bs = SwipeBottomSheet(view.context)
            bs.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
            )
            val nestedScrollView = NestedScrollView(view.context)
            // onMeasure() isn't happening inside NestedScrollView without isFillViewport (see source code)
            nestedScrollView.isFillViewport = true
            nestedScrollView.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
            )
            nestedScrollView.addView(view)
            bs.addView(nestedScrollView)
            return bs
        }
    }

    data class Clip(
        val paddingTop: Float,
        val cornersRadiusTop: Float
    )

    interface SwipeListener {

        fun onSwipeFinished(view: View, isBottom: Boolean) {}
    }
}
