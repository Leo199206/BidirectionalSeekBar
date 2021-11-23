package com.plant.bidirectionalseekbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
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
class BidirectionalSeekBar : View {
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
    private var startThumbX = 0f
    private var endThumbX = 0f
    private var centerY = 0f;
    private var startBorderX = 0f
    private var endBorderX = 0f;
    private var thumbRadius = 0f
    private var thumbStrokeRadius = 0f
    private var distanceY: Int = 0
    private var distanceX: Int = 0
    private var firstY: Int = 0
    private var firstX: Int = 0
    private var slidingStarX = 0f
    private var slidingStarY = 0f
    private val touchSlop: Int by lazy {
        ViewConfiguration.get(context).scaledTouchSlop
    }
    private var startProgress: Float = 0f
    private var endProgress: Float = 0f
    private var seekBarListener: OnSeekBarChangeLister? = null
    private var startTotalValue = 0f
    private var endTotalValue = 100f
    private var startValue = 0f
    private var endValue = 100f
    private var isEnable = true

    /**
     * 自定义属性初始化
     * @param attrs AttributeSet?
     */
    private fun initAttributes(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.BidirectionalSeekBar)
        thumbEnableColor =
            array.getColor(R.styleable.BidirectionalSeekBar_thumbEnableColor, thumbEnableColor)
        thumbDisableColor =
            array.getColor(R.styleable.BidirectionalSeekBar_thumbDisableColor, thumbDisableColor)
        thumbStrokeEnableColor = array.getColor(
            R.styleable.BidirectionalSeekBar_thumbStrokeEnableColor,
            thumbStrokeEnableColor
        )
        thumbStrokeDisableColor = array.getColor(
            R.styleable.BidirectionalSeekBar_thumbStrokeDisableColor,
            thumbStrokeDisableColor
        )
        trackBgEnableColor =
            array.getColor(R.styleable.BidirectionalSeekBar_trackBgEnableColor, trackBgEnableColor)
        trackBgDisableColor = array.getColor(
            R.styleable.BidirectionalSeekBar_trackBgDisableColor,
            trackBgDisableColor
        )
        trackFgEnableColor =
            array.getColor(R.styleable.BidirectionalSeekBar_trackFgEnableColor, trackFgEnableColor)
        trackFgDisableColor = array.getColor(
            R.styleable.BidirectionalSeekBar_trackFgDisableColor,
            trackFgDisableColor
        )
        thumbSize =
            array.getDimensionPixelSize(R.styleable.BidirectionalSeekBar_thumbSize, thumbSize)
        thumbStrokeSize = array.getDimensionPixelSize(
            R.styleable.BidirectionalSeekBar_thumbStrokeSize,
            thumbStrokeSize
        )
        trackHeight =
            array.getDimensionPixelSize(R.styleable.BidirectionalSeekBar_trackHeight, trackHeight)
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
        startThumbX = thumbStrokeRadius + paddingLeft
        endThumbX = width - thumbStrokeRadius - paddingRight
        startBorderX = startThumbX
        endBorderX = endThumbX
        centerY = height / 2f
    }


    /**
     * 绘制
     * @param canvas Canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(startBorderX, centerY, endBorderX, centerY, trackBgPaint)
        canvas.drawLine(startThumbX, centerY, endThumbX, centerY, trackFgPaint)
        canvas.drawCircle(startThumbX, centerY, thumbStrokeRadius, thumbStrokePaint)
        canvas.drawCircle(startThumbX, centerY, thumbRadius, thumbPaint)
    }


    /**
     * 绘制前景
     * @param canvas Canvas
     */
    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        canvas.drawCircle(endThumbX, centerY, thumbStrokeRadius, thumbStrokePaint)
        canvas.drawCircle(endThumbX, centerY, thumbRadius, thumbPaint)

    }


    /**
     * 手指是否在左侧滑块按钮上
     * @return Boolean
     */
    private fun isInLeftThumbRange(x: Float, y: Float): Boolean {
        var rect = RectF(
            startThumbX - thumbStrokeSize,
            0f,
            startThumbX + thumbStrokeSize,
            height.toFloat()
        )
        return rect.contains(x, y)
    }

    /**
     * 手指是否在左侧滑块按钮上
     * @return Boolean
     */
    private fun isInRightThumbRange(x: Float, y: Float): Boolean {
        var rect = RectF(
            endThumbX - thumbStrokeSize,
            0f,
            endThumbX + thumbStrokeSize,
            height.toFloat()
        )
        return rect.contains(x, y)
    }


    /**
     * 滑动事件拦截，处理滑动效果
     * @param event MotionEvent
     * @return Boolean
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                slidingStarX = event.x
                slidingStarY = event.y

            }
            MotionEvent.ACTION_MOVE -> {
                if (isEnable) {
                    handlerPosition(event)
                    setUpdateProgress()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                slidingStarX = 0f
                slidingStarY = 0f
                if (!isEnable) {
                    seekBarListener?.onUnEnable(this)
                }
            }
        }
        return true
    }

    /**
     * 处理滑动位置
     * @param event MotionEvent
     */
    private fun handlerPosition(event: MotionEvent) {
        when {
            isInLeftThumbRange(slidingStarX, slidingStarY) -> {
                var distance = event.x - slidingStarX
                startThumbX += distance
                if (startThumbX < startBorderX) {
                    startThumbX = startBorderX
                }
                if (startThumbX > endBorderX) {
                    startThumbX = endBorderX
                }
                slidingStarX = startThumbX
                slidingStarY = event.y
                postInvalidate()
            }
            isInRightThumbRange(slidingStarX, slidingStarY) -> {
                var distance = event.x - slidingStarX
                endThumbX += distance
                if (endThumbX < startBorderX) {
                    endThumbX = startBorderX
                }
                if (endThumbX > endBorderX) {
                    endThumbX = endBorderX
                }
                slidingStarX = endThumbX
                slidingStarY = event.y
                postInvalidate()
            }
            else -> {
                slidingStarX = event.x
                slidingStarY = event.y
            }
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
     */
    private fun setUpdateProgress() {
        val progress = getCurrentProgress()
        startProgress = progress.first
        endProgress = progress.second
        val progressValue = getCurrentProgressValue()
        startValue = progressValue.first
        endValue = progressValue.second
        Log.d(
            "setUpdateProgress",
            "startProgress=${startProgress} " +
                    "endProgress=${endProgress}" +
                    "startProgressValue=${startValue}" +
                    "endProgressValue=${endValue}"
        )
        seekBarListener?.onSeekBarChange(
            this,
            startProgress,
            endProgress,
            startValue,
            endValue
        )
    }


    /**
     * 当前进度，值范围[0~1f]
     * @return Pair<Float, Float>
     */
    open fun getCurrentProgress(): Pair<Float, Float> {
        var width = getSeekBarWith()
        var start = (startThumbX - paddingLeft - thumbStrokeRadius) / width
        var end = (endThumbX - paddingLeft - thumbStrokeRadius) / width
        val startProgress = min(start, end);
        val endProgress = max(start, end)
        return Pair(startProgress, endProgress)
    }

    /**
     * 当前进度值，值范围为[init]设置的范围
     * @return Float
     */
    open fun getCurrentProgressValue(): Pair<Float, Float> {
        val progress = getCurrentProgress();
        val startValue = getSeekBarTotalValue() * progress.first + this.startTotalValue
        val endValue = getSeekBarTotalValue() * progress.second + this.startTotalValue
        return Pair(startValue, endValue)
    }


    /**
     * SeekBar长度（进度部分）
     * @return Float
     */
    private fun getSeekBarWith(): Float {
        return (endBorderX - startBorderX)
    }


    /**
     * SeekBar参数总大小
     * @return Float
     */
    private fun getSeekBarTotalValue(): Float {
        return this.endTotalValue - this.startTotalValue
    }


    /**
     * 设置初始化值
     * @param startTotalValue Float
     * @param endTotalValue Float
     * @param startValue Float
     * @param endValue Float
     */
    fun init(
        startTotalValue: Float,
        endTotalValue: Float,
        startValue: Float,
        endValue: Float,
        listener: OnSeekBarChangeLister
    ) {
        this.seekBarListener = listener
        this.startTotalValue = startTotalValue
        this.endTotalValue = endTotalValue
        this.endValue = endValue
        this.startValue = startValue
        val total = getSeekBarTotalValue()
        startProgress = (this.startValue - this.startTotalValue) / total
        endProgress = (this.endValue - this.startTotalValue) / total
        post {
            startThumbX = startProgress * getSeekBarWith() + thumbStrokeRadius + paddingLeft
            endThumbX = endProgress * getSeekBarWith() + thumbStrokeRadius + paddingLeft
            this.seekBarListener?.onSeekBarChange(
                this,
                startProgress,
                endProgress,
                startValue,
                endValue
            )
            postInvalidate()
        }
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
         * 滑动回调方法
         * @param startProgress Float
         * @param endProgress Float
         */
        fun onSeekBarChange(
            view: BidirectionalSeekBar,
            startProgress: Float,
            endProgress: Float,
            startValue: Float,
            endValue: Float
        )

        /**
         * 未开启滑动回调
         */
        fun onUnEnable(view: BidirectionalSeekBar)
    }
}