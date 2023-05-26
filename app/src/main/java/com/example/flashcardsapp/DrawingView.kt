package com.example.flashcardsapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Drawing shapes with fingers
 * http://gmariotti.blogspot.com/2014/01/drawing-shapes-with-fingers.html
 * */

class DrawingView(context: Context) : View(context) {
    private var mPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var mPaintFinal: Paint = Paint(Paint.DITHER_FLAG)
    private var mPath = Path()

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var leftIndentation: Float = 0f
    private var topIndentation: Float = 0f
    private var mStartX: Float = 0f
    private var mStartY: Float = 0f
    var currentShape = Shapes.CIRCLE
    private var isDrawing = false
    private var isDrawingEnded = false

    var find = false

    private val pathsArray = ArrayList<PathData>()

    init {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = getContext().resources.getColor(android.R.color.holo_blue_dark)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f

        mPaintFinal.isAntiAlias = true
        mPaintFinal.isDither = true
        mPaintFinal.color = getContext().resources.getColor(android.R.color.holo_orange_dark)
        mPaintFinal.style = Paint.Style.STROKE
        mPaintFinal.strokeJoin = Paint.Join.ROUND
        mPaintFinal.strokeCap = Paint.Cap.ROUND
        mPaintFinal.strokeWidth = 10f
    }

    private fun reset() {
        mPath = Path()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = Canvas(mBitmap!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        leftIndentation = event.x
        topIndentation = event.y

        if (find) {
            val removePaths = isMatch(leftIndentation, topIndentation)
            pathsArray.removeAll(removePaths)
            invalidate()
            return true
        }

        when (currentShape) {
            Shapes.RECTANGLE -> onTouchEventRectangle(event)
            Shapes.CIRCLE -> onTouchEventCircle(event)
            Shapes.LINE ->  {}//onTouchEventLine(event)
            Shapes.SMOOTH_LINE -> {}//onTouchEventSmoothLine(event)
            Shapes.ERASER -> {}//onTouchEventSmoothLine(event)
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pathsArray.forEach {
            canvas.drawPath(it.path, mPaint)
        }

        if (isDrawing) {
            when (currentShape) {
                Shapes.RECTANGLE -> onDrawRectangle(canvas)
                Shapes.CIRCLE -> onDrawCircle(canvas)
                Shapes.LINE -> {}//onDrawLine(canvas)
                Shapes.SMOOTH_LINE -> {}
                Shapes.ERASER -> {}
            }
        }
    }

    /**Rectangle*/
    private fun onTouchEventRectangle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mStartX = leftIndentation
                mStartY = topIndentation
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> invalidate()
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawRectangle(mCanvas!!, mPaintFinal)
                invalidate()
            }
        }
    }

    private fun onDrawRectangle(canvas: Canvas) {
        drawRectangle(canvas, mPaint)
    }

    private fun drawRectangle(canvas: Canvas, paint: Paint) {
        val top = if (mStartY > topIndentation) topIndentation else mStartY
        val left = if (mStartX > leftIndentation) leftIndentation else mStartX
        val bottom = if (mStartY > topIndentation) mStartY else topIndentation
        val right = if (mStartX > leftIndentation) mStartX else leftIndentation

        val path = Path().apply {
            addRect(left, top, right, bottom, Path.Direction.CW)
        }
        val points = ArrayList<FloatPoint>()

        if (!isDrawing) {
            val pathMeasure = PathMeasure(path, false)
            val distance: Float = pathMeasure.length

            val ignore = 10
            for (i in 0..distance.toInt() step ignore) {
                val pos = FloatArray(2)
                pathMeasure.getPosTan(i.toFloat(), pos, null)


                val floatPoint = FloatPoint(pos[1], pos[0]) //№1 - top, №0 - left

                points.add(floatPoint)
            }

            pathsArray.add(PathData(path, points))

            canvas.drawPath(path, paint)
        }
    }


//
//    /**Line*/
//    private fun onDrawLine(canvas: Canvas) {
//        val dx = Math.abs(mx - mStartX)
//        val dy = Math.abs(my - mStartY)
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            canvas.drawLine(mStartX, mStartY, mx, my, mPaint)
//        }
//    }
//
//    private fun onTouchEventLine(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                isDrawing = true
//                mStartX = mx
//                mStartY = my
//                invalidate()
//            }
//
//            MotionEvent.ACTION_MOVE -> invalidate()
//            MotionEvent.ACTION_UP -> {
//                isDrawing = false
//                mCanvas!!.drawLine(mStartX, mStartY, mx, my, mPaintFinal)
//                invalidate()
//            }
//        }
//    }
//
    /**Circle*/
    private fun onDrawCircle(canvas: Canvas) {
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, leftIndentation, topIndentation), mPaint)
    }

    private fun onTouchEventCircle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mStartX = leftIndentation
                mStartY = topIndentation
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> invalidate()
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                mCanvas!!.drawCircle(
                    mStartX, mStartY,
                    calculateRadius(mStartX, mStartY, leftIndentation, topIndentation), mPaintFinal
                )
                invalidate()
            }
        }
    }

    private fun calculateRadius(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.sqrt(
            Math.pow((x1 - x2).toDouble(), 2.0) +
                    Math.pow((y1 - y2).toDouble(), 2.0)
        ).toFloat()
    }


//
//
//    /**SmoothLine*/
//    private fun onTouchEventSmoothLine(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                isDrawing = true
//                mStartX = mx
//                mStartY = my
//                mPath.reset()
//                mPath.moveTo(mx, my)
//                invalidate()
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//                val dx = abs(mx - mStartX)
//                val dy = abs(my - mStartY)
//                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//                    mPath.quadTo(mStartX, mStartY, (mx + mStartX) / 2, (my + mStartY) / 2)
//                    mStartX = mx
//                    mStartY = my
//                }
//                mCanvas!!.drawPath(mPath, mPaint)
//                invalidate()
//            }
//
//            MotionEvent.ACTION_UP -> {
//                isDrawing = false
//                mPath.lineTo(mStartX, mStartY)
//                mCanvas!!.drawPath(mPath, mPaintFinal)
//                mPath.reset()
//                invalidate()
//            }
//        }
//    }


    fun toCheckMode() {
        find = true

        pathsArray.forEachIndexed { index, it ->
            Log.d("fffffffffffffffrr1", "index: = $index")
            it.points.forEach { point ->
                Log.d("fffffffffffffffrr1", "point: = ${point.toString()}")
            }

        }
    }

    private fun isMatch(left: Float, top: Float): Set<PathData> {
        val delta = 50
        val leftInt = left.toInt()
        val topInt = top.toInt()
        val removePaths = mutableSetOf<PathData>()

        pathsArray.forEach {
            it.points.forEach{ point ->

                if ((leftInt - delta..leftInt + delta).contains(point.leftIndentation.toInt())
                    && (topInt - delta..topInt + delta).contains(point.topIndentation.toInt())
                ) {
                    removePaths.add(it)
                }
            }
        }

        return removePaths
    }
}