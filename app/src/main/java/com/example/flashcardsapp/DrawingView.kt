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
import java.util.LinkedList

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
    private var startX: Float = 0f
    private var startY: Float = 0f
    var currentTool = Tools.PEN
    private var isDrawing = false
    private var isDrawingEnded = false
    private val TOUCH_TOLERANCE = 4f
    private val pathsArray = LinkedList<PathData>()
    private val selectedPaths = mutableSetOf<PathData>()

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

    private fun resetPath() {
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
        when (currentTool) {
            Tools.RECTANGLE -> onTouchEventRectangle(event)
            Tools.CIRCLE -> onTouchEventCircle(event)
            Tools.LINE -> onTouchEventLine(event)
            Tools.PEN -> onTouchEventSmoothLine(event)
            Tools.ERASER -> onTouchEventEraser()
            Tools.MOVE -> onTouchEventMove(event)
            Tools.SELECT -> onTouchEventSelect(event)
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pathsArray.forEach {
            canvas.drawPath(it.path, mPaintFinal)
        }

        //Preliminary drawing of SmoothLine
        if (isDrawing)      //TODO Добавить условие CurrentTool == Tools.PEN?
            canvas.drawPath(mPath, mPaint)

        if (isDrawing) {
            when (currentTool) {
                Tools.RECTANGLE -> onDrawRectangle(canvas)
                Tools.CIRCLE -> onDrawCircle(canvas)
                Tools.LINE -> onDrawLine(canvas)
                Tools.PEN -> {}
                Tools.ERASER -> {}
                Tools.MOVE -> {}
                Tools.SELECT -> onDrawSelectedField(canvas)
            }
        }
    }

    /**Rectangle*/
    private fun onTouchEventRectangle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = leftIndentation
                startY = topIndentation
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> invalidate()
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawShape(mCanvas!!, mPaintFinal)
                invalidate()
            }
        }
    }

    private fun onDrawRectangle(canvas: Canvas) {
        drawShape(canvas, mPaint)
    }

    private fun drawShape(canvas: Canvas, paint: Paint) {
        val top = if (startY > topIndentation) topIndentation else startY
        val left = if (startX > leftIndentation) leftIndentation else startX
        val bottom = if (startY > topIndentation) startY else topIndentation
        val right = if (startX > leftIndentation) startX else leftIndentation

        val path = Path()
        when (currentTool) {
            Tools.RECTANGLE -> path.addRect(left, top, right, bottom, Path.Direction.CW)
            Tools.CIRCLE -> path.addOval(left, top, right, bottom, Path.Direction.CW)
            else -> {}
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
        }

        canvas.drawPath(path, paint)
    }

    /**Line*/
    private fun onDrawLine(canvas: Canvas) {
        val dx = Math.abs(leftIndentation - startX)
        val dy = Math.abs(topIndentation - startY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawLine(canvas, mPaint)
        }
    }

    private fun onTouchEventLine(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = leftIndentation
                startY = topIndentation
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> invalidate()
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawLine(mCanvas!!, mPaintFinal)
                invalidate()
            }
        }
    }

    private fun drawLine(canvas: Canvas, paint: Paint) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(leftIndentation, topIndentation)
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
        }
        canvas.drawPath(path, paint)
    }

    /**Circle*/
    private fun onDrawCircle(canvas: Canvas) {
        drawShape(canvas, mPaint)
    }

    private fun onTouchEventCircle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = leftIndentation
                startY = topIndentation
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> invalidate()
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawShape(mCanvas!!, mPaintFinal)
                invalidate()
            }
        }
    }

    /**SmoothLine*/
    private fun onTouchEventSmoothLine(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = leftIndentation
                startY = topIndentation
                resetPath()
                mPath.moveTo(leftIndentation, topIndentation)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(leftIndentation - startX)
                val dy = Math.abs(topIndentation - startY)
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(startX, startY, (leftIndentation + startX) / 2, (topIndentation + startY) / 2)
                    startX = leftIndentation
                    startY = topIndentation
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
                val points = ArrayList<FloatPoint>()
                val pathMeasure = PathMeasure(mPath, false)
                val distance: Float = pathMeasure.length

                val ignore = 10
                for (i in 0..distance.toInt() step ignore) {
                    val pos = FloatArray(2)
                    pathMeasure.getPosTan(i.toFloat(), pos, null)
                    val floatPoint = FloatPoint(pos[1], pos[0]) //№1 - from top, №0 - from left
                    points.add(floatPoint)
                }
                pathsArray.add(PathData(mPath, points))
                resetPath()
                invalidate()
            }
        }
    }

    /**Eraser*/
    private fun onTouchEventEraser() {
        val removePaths = isMatch(leftIndentation, topIndentation)
        pathsArray.removeAll(removePaths)
        invalidate()
    }

    /**Move*/
    private fun onTouchEventMove(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedPaths.addAll(isMatch(leftIndentation, topIndentation))
                startX = leftIndentation
                startY = topIndentation
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(leftIndentation - startX)
                val dy = Math.abs(topIndentation - startY)
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    selectedPaths.forEach {
                        it.path.offset(leftIndentation - startX, topIndentation - startY)
                    }
                    startX = leftIndentation
                    startY = topIndentation
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedPaths.forEach {
                    it.points.clear()
                    val pathMeasure = PathMeasure(it.path, false)
                    val distance: Float = pathMeasure.length
                    val ignore = 10
                    for (i in 0..distance.toInt() step ignore) {
                        val pos = FloatArray(2)
                        pathMeasure.getPosTan(i.toFloat(), pos, null)
                        val floatPoint = FloatPoint(pos[1], pos[0]) //№1 - top, №0 - left
                        it.points.add(floatPoint)
                    }
                }
                selectedPaths.clear()
            }
        }
    }

    /**Selected*/
    private fun onTouchEventSelect(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = leftIndentation
                startY = topIndentation
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> invalidate()
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawSelectedField(mCanvas!!, mPaint)

//                selectedPaths.forEach {
//                    it.points.clear()
//                    val pathMeasure = PathMeasure(it.path, false)
//                    val distance: Float = pathMeasure.length
//                    val ignore = 10
//                    for (i in 0..distance.toInt() step ignore) {
//                        val pos = FloatArray(2)
//                        pathMeasure.getPosTan(i.toFloat(), pos, null)
//                        val floatPoint = FloatPoint(pos[1], pos[0]) //№1 - top, №0 - left
//                        it.points.add(floatPoint)
//                    }
//                }

//                selectedPaths.clear()   //TODO Сделать отмену выделения
            }
        }
    }

    private fun onDrawSelectedField(canvas: Canvas) {
        drawSelectedField(canvas, mPaint)
    }

    private fun drawSelectedField(canvas: Canvas, paint: Paint) {
        val top = if (startY > topIndentation) topIndentation else startY
        val left = if (startX > leftIndentation) leftIndentation else startX
        val bottom = if (startY > topIndentation) startY else topIndentation
        val right = if (startX > leftIndentation) startX else leftIndentation

        val path = Path()
        path.addRect(left, top, right, bottom, Path.Direction.CW)

        if (!isDrawing){
            pathsArray.forEach {
                it.points.forEach { point ->
                    if ((top..bottom).contains(point.topIndentation) &&
                        (left..right).contains(leftIndentation)) {
                        selectedPaths.add(it)
                    }
                }
            }

            var maxTop = 0f
            var maxLeft = 0f
            var maxBottom = 0f
            var maxRight = 0f

            if (selectedPaths.size > 0) {0
                maxTop = selectedPaths.first().points.first().topIndentation
                maxBottom = maxTop
                maxLeft = selectedPaths.first().points.first().leftIndentation
                maxRight = maxLeft


                selectedPaths.forEach {
                    it.points.forEach { point ->
                        if (point.topIndentation < maxTop) maxTop = point.topIndentation
                        if (point.topIndentation > maxBottom) maxBottom = point.topIndentation
                        if (point.leftIndentation < maxLeft) maxLeft = point.leftIndentation
                        if (point.leftIndentation > maxRight) maxRight = point.leftIndentation
                    }
                }

                val p = Path()
                p.addRect(maxLeft, maxTop, maxRight, maxBottom, Path.Direction.CW)
                pathsArray.add(PathData(p, ArrayList()))
                invalidate()
            }

            Log.d("2222deTAG", "size: ${selectedPaths.size}")
            Log.d("2222deTAG", "maxTop: $maxTop")
            Log.d("2222deTAG", "maxLeft: $maxLeft")
            Log.d("2222deTAG", "maxRight: $maxRight")
            Log.d("2222deTAG", "maxBottom: $maxBottom")

//            selectedPaths.forEach{ logPath ->
//                Log.d("2222deTAG", "selectedPaths: ${logPath.points}")
//            }

        }

        else
            canvas.drawPath(path, paint)
    }

    private fun isMatch(left: Float, top: Float): Set<PathData> {
        val delta = 50
        val leftInt = left.toInt()
        val topInt = top.toInt()
        val selectedPaths = mutableSetOf<PathData>()

        pathsArray.forEach {
            it.points.forEach breaking@ { point ->
                if ((leftInt - delta..leftInt + delta).contains(point.leftIndentation.toInt())
                    && (topInt - delta..topInt + delta).contains(point.topIndentation.toInt())
                ) {
                    selectedPaths.add(it)
                    return@breaking
                }
            }
        }
        return selectedPaths
    }
}