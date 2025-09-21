package com.example.projectwork_1.view.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.example.projectwork_1.R

class RatingDonutView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    private val oval = RectF()
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var stroke = 10f
    private var progress = 50
    private var scaleSize = 60f

    private lateinit var bitMap: Bitmap
    private var isStaticPictureDrawn = false
    private lateinit var staticCanvas: Canvas

    private lateinit var strokePaint: Paint
    private lateinit var digitPaint: Paint
    private lateinit var circlePaint: Paint

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.RatingDonutView, 0, 0)
        try {
            progress = attributes.getInt(R.styleable.RatingDonutView_progress, progress)
            stroke = attributes.getFloat(R.styleable.RatingDonutView_stroke, stroke)
        } finally {
            attributes.recycle()
        }

        initPaints()
    }

    private fun initPaints() {
        strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = stroke
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        digitPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            //Задает тень для фигуры
            setShadowLayer(5f, 0f, 0f, Color.DKGRAY)
            textSize = scaleSize
            //Задает шрифт
            typeface = Typeface.SANS_SERIF
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        circlePaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.DKGRAY
        }
    }

    private fun getPaintColor(progress: Int): Int = when (progress) {
        in 0..25 -> "#BD0000".toColorInt()
        in 26..50 -> "#AD3A00".toColorInt()
        in 51..75 -> "#AD9A00".toColorInt()
        else -> "#28B400".toColorInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (width > height) {
            radius = height.div(2f)
        } else {
            radius = width.div(2f)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)

        val minSide = Math.min(chosenWidth, chosenHeight)
        centerX = minSide.div(2f)
        centerY = minSide.div(2f)

        setMeasuredDimension(minSide, minSide)
    }

    private fun chooseDimension(mode: Int, size: Int) = when (mode) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
            else -> 300
    }

    override fun onDraw(canvas: Canvas) {
        if (!isStaticPictureDrawn) {
            drawStaticPicture()
        }

        bitMap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        drawRating(canvas)
        drawText(canvas)
    }

    private fun drawStaticPicture() {
        //Создаем битмап
        bitMap = createBitmap((centerX * 2).toInt(), (centerY * 2).toInt())
        //Создаем канвас для этого битмапа, теперь что мы нарисуем на канвасе, будет отображено в битмап.
        staticCanvas = Canvas(bitMap)
        drawCircleBackground(staticCanvas)

        isStaticPictureDrawn = true
    }

    private fun drawCircleBackground(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    private fun drawRating(canvas: Canvas) {
        val scale = radius * 0.8

        canvas.save()
        canvas.translate(centerX, centerY)

        oval.set((0f - scale).toFloat(), (0f - scale).toFloat(), scale.toFloat(), scale.toFloat())

        canvas.drawArc(oval, -90f, convertProgressToDegrees(progress), false, strokePaint)
        canvas.restore()
    }

    private fun convertProgressToDegrees(progress: Int): Float = progress * 3.6f

    private fun drawText(canvas: Canvas) {
        val message = String.format("%.1f", progress / 10f)

        val widths = FloatArray(message.length)
        digitPaint.getTextWidths(message, widths)
        var advance = 0f
        for (width in widths) advance += width
        canvas.drawText(message, centerX - advance / 2, centerY  + advance / 4, digitPaint)
    }

//    fun setProgress(pr: Int) {
//        progress = pr
//        initPaints()
//        invalidate()
//    }

    fun setProgressAnimated(targetProgress: Int, duration: Long = 1200) {
        val startProgress = 0
        val animator = ValueAnimator.ofInt(startProgress, targetProgress)
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()

        //Вызывается на каждом изменении значения, то есть ValueAnimator меняет значения прогресса от startProgress до targetProgress
        //и каждое изменение вызывает код внутри него. Далее записываем значение каждого кадра анимации и вызываем перерисовку.
        animator.addUpdateListener { animation ->
            progress = animation.animatedValue as Int
            initPaints()
            invalidate()
        }
        animator.start()
    }
}