package com.plant.bidirectionalseekbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

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
    private var thumbStrokeEnableColor = Color.BLUE
    private var thumbStrokeDisableColor = Color.DKGRAY
    private var trackBgEnableColor = Color.GRAY
    private var trackBgDisableColor = Color.DKGRAY
    private var trackFgEnableColor = Color.BLUE
    private var trackFgDisableColor = Color.BLACK
    private var thumbSize = 50
    private var thumbStrokeSize = 2
    private var trackHeight = 10
    private val leftThumbRect = RectF()
    private val rightThumbRect = RectF()
    private val leftThumbStrokeRect = RectF()
    private val rightThumbStrokeRect = RectF()
    private val trackBgRect = RectF()
    private val trackFgRect = RectF()


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
        thumbPaint.style = Paint.Style.FILL
        thumbPaint.strokeJoin = Paint.Join.ROUND
        thumbPaint.strokeCap = Paint.Cap.ROUND
        thumbPaint.isAntiAlias = true
        thumbPaint.isDither = true
        if (isEnabled) {
            thumbPaint.color = thumbEnableColor
        } else {
            thumbPaint.color = thumbDisableColor
        }
    }

    /**
     * 初始化滑块画笔
     */
    private fun initThumbStrokePaint() {
        thumbStrokePaint.style = Paint.Style.FILL
        thumbStrokePaint.strokeJoin = Paint.Join.ROUND
        thumbStrokePaint.strokeCap = Paint.Cap.ROUND
        thumbStrokePaint.isAntiAlias = true
        thumbStrokePaint.isDither = true
        if (isEnabled) {
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
        trackBgPaint.isAntiAlias = true
        trackBgPaint.isDither = true
        if (isEnabled) {
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
        if (isEnabled) {
            trackFgPaint.color = trackFgEnableColor
        } else {
            trackFgPaint.color = trackFgEnableColor
        }
    }


    /**
     * 测量控件高度
     * @param widthMeasureSpec Int
     * @param heightMeasureSpec Int
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureSize(widthMeasureSpec, 300)
        val height = measureSize(heightMeasureSpec, 60)
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
                size or MEASURED_STATE_TOO_SMALL
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
    }


    /**
     * 绘制
     * @param canvas Canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        onDrawLeftThumb(canvas)
        onDrawRightThumb(canvas)
    }


    /**
     * 绘制左侧滑块
     */
    private fun onDrawLeftThumb(canvas: Canvas) {
        val radius = thumbSize / 2f
        val cx = leftThumbRect.left + radius
        val cy = leftThumbRect.top + radius
        canvas.drawCircle(cx, cy, radius, thumbPaint)
    }

    /**
     * 绘制右侧滑块
     */
    private fun onDrawRightThumb(canvas: Canvas) {
        val radius = thumbSize / 2f
        val cx = rightThumbRect.left + radius
        val cy = rightThumbRect.top + radius
        canvas.drawCircle(cx, cy, radius, thumbPaint)
    }

}