package com.plant.seekbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * <pre>
 *   @author : leo
 *    time   : 2021/11/17
 *    desc   :
 * </pre>
 */
class RangeSeekBar : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
        initPaint()
    }


    private val thumbPaint by lazy { Paint() }
    private val thumbStrokePaint by lazy { Paint() }
    private val trackBgPaint by lazy { Paint() }
    private val trackFgPaint by lazy { Paint() }
    private var thumbEnableColor = Color.WHITE
    private var thumbDisableColor = Color.WHITE
    private var thumbStrokeEnableColor = Color.RED
    private var thumbStrokeDisableColor = Color.RED
    private var trackBgEnableColor = Color.GRAY
    private var trackBgDisableColor = Color.DKGRAY
    private var trackFgEnableColor = Color.BLUE
    private var trackFgDisableColor = Color.BLACK
    private var thumbSize = 50
    private var thumbStrokeSize = 60
    private var trackHeight = 10
    private var minThumbX = 0f
    private var maxThumbX = 0f
    private var centerY = 0f;
    private var minBorderX = 0f
    private var maxBorderX = 0f;
    private var thumbRadius = 0f
    private var thumbStrokeRadius = 0f
    private var distanceY: Int = 0
    private var distanceX: Int = 0
    private var firstY: Int = 0
    private var firstX: Int = 0
    private var slidingMaxThumbX = 0f
    private var slidingMinThumbX = 0f
    private var slidingStarY = 0f
    private val touchSlop: Int by lazy {
        ViewConfiguration.get(context).scaledTouchSlop
    }
    private var seekBarListener: OnSeekBarChangeLister? = null

    /** @see minValue 对应位置百分比 **/
    private var minProgressPercent: Float = 0f

    /** @see maxValue 对应位置百分比 **/
    private var maxProgressPercent: Float = 0f

    /** @see minValue 最小拖动值 **/
    private var minValue = 0f

    /** @see maxValue 最大拖动值 **/
    private var maxValue = 100f

    /** @see rangeMinValue 当前选择的最小值 **/
    private var rangeMinValue = minValue

    /** @see rangeMaxValue 当前选择的最大值 **/
    private var rangeMaxValue = maxValue

    /** @see **/
    private var isEnable = true

    /**
     * 自定义属性初始化
     * @param attrs AttributeSet?
     */
    private fun initAttributes(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar)
        thumbEnableColor =
            array.getColor(R.styleable.RangeSeekBar_thumbEnableColor, thumbEnableColor)
        thumbDisableColor =
            array.getColor(R.styleable.RangeSeekBar_thumbDisableColor, thumbDisableColor)
        thumbStrokeEnableColor = array.getColor(
            R.styleable.RangeSeekBar_thumbStrokeEnableColor,
            thumbStrokeEnableColor
        )
        thumbStrokeDisableColor = array.getColor(
            R.styleable.RangeSeekBar_thumbStrokeDisableColor,
            thumbStrokeDisableColor
        )
        trackBgEnableColor =
            array.getColor(R.styleable.RangeSeekBar_trackBgEnableColor, trackBgEnableColor)
        trackBgDisableColor = array.getColor(
            R.styleable.RangeSeekBar_trackBgDisableColor,
            trackBgDisableColor
        )
        trackFgEnableColor =
            array.getColor(R.styleable.RangeSeekBar_trackFgEnableColor, trackFgEnableColor)
        trackFgDisableColor = array.getColor(
            R.styleable.RangeSeekBar_trackFgDisableColor,
            trackFgDisableColor
        )
        thumbSize =
            array.getDimensionPixelSize(R.styleable.RangeSeekBar_thumbSize, thumbSize)
        thumbStrokeSize = array.getDimensionPixelSize(
            R.styleable.RangeSeekBar_thumbStrokeSize,
            thumbSize
        )
        trackHeight =
            array.getDimensionPixelSize(R.styleable.RangeSeekBar_trackHeight, trackHeight)
        thumbRadius = thumbSize / 2f
        thumbStrokeRadius = thumbStrokeSize / 2f
        array.recycle()
    }


    /**
     * 画笔初始化
     */
    private fun initPaint() {
        initThumbPaint()
        initThumbStrokePaint()
        initTrackBgPaint()
        initTrackFgPaint()
    }


    /**
     * 初始化滑块画笔
     */
    private fun initThumbPaint() {
        thumbPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        thumbPaint.style = Paint.Style.FILL
        thumbPaint.strokeJoin = Paint.Join.ROUND
        thumbPaint.strokeCap = Paint.Cap.ROUND
        thumbPaint.isAntiAlias = true
        thumbPaint.isDither = true
        if (isEnable) {
            thumbPaint.color = thumbEnableColor
        } else {
            thumbPaint.color = thumbDisableColor
        }
    }

    /**
     * 初始化滑块画笔
     */
    private fun initThumbStrokePaint() {
        thumbStrokePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        thumbStrokePaint.style = Paint.Style.FILL
        thumbStrokePaint.strokeJoin = Paint.Join.ROUND
        thumbStrokePaint.strokeCap = Paint.Cap.ROUND
        thumbStrokePaint.isAntiAlias = true
        thumbStrokePaint.isDither = true
        if (isEnable) {
            thumbStrokePaint.color = thumbStrokeEnableColor
        } else {
            thumbStrokePaint.color = thumbStrokeDisableColor
        }
    }


    /**
     * 进度条背景
     */
    private fun initTrackBgPaint() {
        trackBgPaint.style = Paint.Style.FILL
        trackBgPaint.strokeJoin = Paint.Join.ROUND
        trackBgPaint.strokeCap = Paint.Cap.ROUND
        trackBgPaint.strokeWidth = trackHeight.toFloat()
        trackBgPaint.isAntiAlias = true
        trackBgPaint.isDither = true
        if (isEnable) {
            trackBgPaint.color = trackBgEnableColor
        } else {
            trackBgPaint.color = trackBgDisableColor
        }
    }


    /**
     * 进度条前景
     */
    private fun initTrackFgPaint() {
        trackFgPaint.style = Paint.Style.FILL
        trackFgPaint.strokeJoin = Paint.Join.ROUND
        trackFgPaint.strokeCap = Paint.Cap.ROUND
        trackFgPaint.isAntiAlias = true
        trackFgPaint.isDither = true
        trackFgPaint.strokeWidth = trackHeight.toFloat()
        if (isEnable) {
            trackFgPaint.color = trackFgEnableColor
        } else {
            trackFgPaint.color = trackFgDisableColor
        }
    }


    /**
     * 测量控件高度
     * @param widthMeasureSpec Int
     * @param heightMeasureSpec Int
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = measureSize(widthMeasureSpec, 300)
        var height = measureSize(heightMeasureSpec, 60)
        val withMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (withMode != MeasureSpec.EXACTLY) {
            width += paddingLeft + paddingRight
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height += paddingTop + paddingBottom
        }
        setMeasuredDimension(width, height)
    }


    /**
     * 测量确定View大小
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private fun measureSize(measureSpec: Int, defaultSize: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.AT_MOST -> {
                defaultSize or MEASURED_STATE_TOO_SMALL
            }

            MeasureSpec.EXACTLY -> {
                size
            }

            else -> {
                defaultSize
            }
        }
    }


    /**
     * 窗口尺寸变化回调
     * @param w Int
     * @param h Int
     * @param oldw Int
     * @param oldh Int
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initPosition()
    }


    /**
     * 初始化滑块绘制位置
     */
    private fun initPosition() {
        minThumbX = thumbStrokeRadius + paddingLeft
        maxThumbX = width - thumbStrokeRadius - paddingRight
        minBorderX = minThumbX
        maxBorderX = maxThumbX
        centerY = height / 2f
    }


    /**
     * 绘制
     * @param canvas Canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val total = getSeekBarTotalValue()
        minProgressPercent = (this.rangeMinValue - this.minValue) / total
        maxProgressPercent = (this.rangeMaxValue - this.minValue) / total
        minThumbX = minProgressPercent * getSeekBarWith() + thumbStrokeRadius + paddingLeft
        maxThumbX = maxProgressPercent * getSeekBarWith() + thumbStrokeRadius + paddingLeft
        slidingMinThumbX = minThumbX
        slidingMaxThumbX = maxThumbX
        slidingStarY = centerY
        canvas.drawLine(minBorderX, centerY, maxBorderX, centerY, trackBgPaint)
        canvas.drawLine(minThumbX, centerY, maxThumbX, centerY, trackFgPaint)
        canvas.drawCircle(minThumbX, centerY, thumbStrokeRadius, thumbStrokePaint)
        canvas.drawCircle(minThumbX, centerY, thumbRadius, thumbPaint)
        canvas.drawCircle(maxThumbX, centerY, thumbStrokeRadius, thumbStrokePaint)
        canvas.drawCircle(maxThumbX, centerY, thumbRadius, thumbPaint)
    }


    /**
     * 手指是否在左侧滑块按钮上
     * @return Boolean
     */
    private fun isInMinThumbRange(x: Float, y: Float): Boolean {
        val rect = RectF(
            minThumbX - thumbStrokeSize,
            0f,
            minThumbX + thumbStrokeSize,
            height.toFloat()
        )
        return rect.contains(x, y)
    }

    /**
     * 手指是否在左侧滑块按钮上
     * @return Boolean
     */
    private fun isInMaxThumbRange(x: Float, y: Float): Boolean {
        var rect = RectF(
            maxThumbX - thumbStrokeSize,
            0f,
            maxThumbX + thumbStrokeSize,
            height.toFloat()
        )
        return rect.contains(x, y)
    }

    private var onDragMinThumb = false
    private var onDragMaxThumb = false

    /**
     * 滑动事件拦截，处理滑动效果
     * @param event MotionEvent
     * @return Boolean
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onDragMinThumb = isInMinThumbRange(event.x, event.y)
                onDragMaxThumb = isInMaxThumbRange(event.x, event.y)
                if (onDragMinThumb) {
                    slidingMinThumbX = event.x
                } else if (onDragMaxThumb) {
                    slidingMaxThumbX = event.x
                }
                slidingStarY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                if (isEnable) {
                    handlerPosition(event)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                slidingStarY = 0f
                if (!isEnable) {
                    seekBarListener?.onDisableClick(this)
                } else {
                    setUpdateProgress(true)
                }
                onDragMaxThumb = false
                onDragMinThumb = false
            }
        }
        return true
    }

    /**
     * 处理滑动位置
     * @param event MotionEvent
     */
    private fun handlerPosition(event: MotionEvent) {
        if (onDragMaxThumb) {
            val distance = event.x - slidingMaxThumbX
            val targetX = maxThumbX + distance
            maxThumbX = if (event.x < slidingMinThumbX) {
                minThumbX
            } else {
                targetX
            }
            if (maxThumbX < minBorderX) {
                maxThumbX = minBorderX
            }
            if (maxThumbX > maxBorderX) {
                maxThumbX = maxBorderX
            }
            setUpdateProgress(false)
            postInvalidate()
        } else if (onDragMinThumb) {
            val distance = event.x - slidingMinThumbX
            val targetX = minThumbX + distance
            minThumbX = if (event.x > slidingMaxThumbX) {
                maxThumbX
            } else {
                targetX
            }
            if (minThumbX < minBorderX) {
                minThumbX = minBorderX
            }
            if (minThumbX > maxBorderX) {
                minThumbX = maxBorderX
            }
            setUpdateProgress(false)
            postInvalidate()
        }
    }


    /**
     * 处理滑动事件冲突
     * @param event MotionEvent
     * @return Boolean
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                firstX = x
                firstY = y
            }

            MotionEvent.ACTION_MOVE -> {
                distanceX = abs(x - firstX)
                distanceY = abs(y - firstY)
                if (distanceX > touchSlop && distanceX > distanceY) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }

            MotionEvent.ACTION_UP -> {
                firstX = 0
                firstY = 0
            }
        }
        return super.dispatchTouchEvent(event)
    }


    /**
     * 更新当前进度
     * @param dragEnd Boolean 是否拖动结束
     */
    private fun setUpdateProgress(dragEnd: Boolean) {
        val progress = getCurrentProgress()
        minProgressPercent = progress.first
        maxProgressPercent = progress.second
        val progressValue = getCurrentProgressValue()
        rangeMinValue = progressValue.first
        rangeMaxValue = progressValue.second
        if (dragEnd) {
            seekBarListener?.onRangeSeekBarDragEnd(this, rangeMinValue, rangeMaxValue)
        } else {
            seekBarListener?.onRangeSeekBarDrag(this, rangeMinValue, rangeMaxValue)
        }
    }


    /**
     * 当前进度，值范围[0~1f]
     * @return Pair<Float, Float>
     */
    private fun getCurrentProgress(): Pair<Float, Float> {
        val width = getSeekBarWith()
        val start = (minThumbX - paddingLeft - thumbStrokeRadius) / width
        val end = (maxThumbX - paddingLeft - thumbStrokeRadius) / width
        val startProgress = min(start, end);
        val endProgress = max(start, end)
        return Pair(startProgress, endProgress)
    }

    /**
     * 当前进度值，值范围为[setRange]设置的范围
     * @return Float
     */
    private fun getCurrentProgressValue(): Pair<Float, Float> {
        val progress = getCurrentProgress();
        val rangeMin = getSeekBarTotalValue() * progress.first + this.minValue
        val rangeMax = getSeekBarTotalValue() * progress.second + this.minValue
        return Pair(rangeMin, rangeMax)
    }


    /**
     * SeekBar长度（进度部分）
     * @return Float
     */
    private fun getSeekBarWith(): Float {
        return (maxBorderX - minBorderX)
    }


    /**
     * SeekBar参数总大小
     * @return Float
     */
    private fun getSeekBarTotalValue(): Float {
        return this.maxValue - this.minValue
    }


    /**
     * 设置控件区间
     * @param min Float 区间最小值
     * @param max Float 区间最大值
     */
    fun setRange(
        min: Float,
        max: Float,
    ) {
        this.minValue = min
        this.maxValue = max
        postInvalidate()
    }

    /**
     * 设置控件，当前选中区间
     * @param rangeMaxValue Float 区间最小值
     * @param rangeMinValue Float 区间最大值
     */
    fun setCurrentRange(
        rangeMinValue: Float,
        rangeMaxValue: Float
    ) {
        this.rangeMinValue = rangeMinValue
        this.rangeMaxValue = rangeMaxValue
        postInvalidate()
    }


    /**
     * 进度回调
     * @param listener OnSeekBarChangeLister
     */
    fun setSeekBarChangeListener(listener: OnSeekBarChangeLister) {
        this.seekBarListener = listener
    }


    /**
     * 是否可滑动
     * @param enabled Boolean
     */
    fun setEnable(enabled: Boolean) {
        this.isEnable = enabled
        initPaint()
        postInvalidate()
    }


    /**
     * 滑动回调接口
     */
    interface OnSeekBarChangeLister {

        /**
         * 进度条拖动中
         * @param rangeMinValue Float 区间最新范围
         * @param rangeMaxValue Float 区间最大范围
         */
        fun onRangeSeekBarDrag(
            view: RangeSeekBar,
            rangeMinValue: Float,
            rangeMaxValue: Float
        ) {

        }

        /**
         * 进度条拖动结束
         * @param rangeMinValue Float 区间最新范围
         * @param rangeMaxValue Float 区间最大范围
         */
        fun onRangeSeekBarDragEnd(
            view: RangeSeekBar,
            rangeMinValue: Float,
            rangeMaxValue: Float
        ) {

        }

        /**
         * 禁用状态，点击回调
         * @param view
         */
        fun onDisableClick(view: RangeSeekBar) {

        }
    }
}