package com.example.flashcardsapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
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
    private var mPaintSelected: Paint = Paint(Paint.DITHER_FLAG)
    private var fillPaintPreparingToSelect: Paint = Paint(Paint.DITHER_FLAG)
    private var strokePaintPreparingToSelect: Paint = Paint(Paint.DITHER_FLAG)
    private var mPath = Path()

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var leftIndentation: Float = 0f
    private var topIndentation: Float = 0f
    private var startX: Float = 0f
    private var startY: Float = 0f
    var currentTool = Tools.PEN
    private var isDrawing = false
    private val TOUCH_TOLERANCE = 4f
    private val pathsArray = LinkedList<PathData>()
    private val selectedPaths = mutableSetOf<PathData>()
    private val selectedBorder = Path()
    private var isMoving = false

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

        mPaintSelected.isAntiAlias = true
        mPaintSelected.isDither = true
        mPaintSelected.color = getContext().resources.getColor(android.R.color.darker_gray)
        mPaintSelected.style = Paint.Style.STROKE
        mPaintSelected.strokeJoin = Paint.Join.ROUND
        mPaintSelected.strokeCap = Paint.Cap.ROUND
        mPaintSelected.strokeWidth = 5f
        mPaintSelected.pathEffect = DashPathEffect(floatArrayOf(50F, 10F, 5F, 10F), 0f)

        fillPaintPreparingToSelect.isAntiAlias = true
        fillPaintPreparingToSelect.isDither = true
        fillPaintPreparingToSelect.color = getContext().resources.getColor(R.color.fill_select)
        fillPaintPreparingToSelect.style = Paint.Style.FILL
        fillPaintPreparingToSelect.strokeJoin = Paint.Join.ROUND
        fillPaintPreparingToSelect.strokeCap = Paint.Cap.ROUND
        fillPaintPreparingToSelect.strokeWidth = 5f

        strokePaintPreparingToSelect.isAntiAlias = true
        strokePaintPreparingToSelect.isDither = true
        strokePaintPreparingToSelect.color = getContext().resources.getColor(R.color.stroke_select)
        strokePaintPreparingToSelect.style = Paint.Style.STROKE
        strokePaintPreparingToSelect.strokeJoin = Paint.Join.ROUND
        strokePaintPreparingToSelect.strokeCap = Paint.Cap.ROUND
        strokePaintPreparingToSelect.strokeWidth = 5f
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

        if (currentTool != Tools.SELECT) {
            selectedPaths.clear()
            selectedBorder.reset()
        }

        when (currentTool) {
            Tools.RECTANGLE -> onTouchEventRectangle(event)
            Tools.CIRCLE -> onTouchEventCircle(event)
            Tools.LINE -> onTouchEventLine(event)
            Tools.PEN -> onTouchEventSmoothLine(event)
            Tools.ERASER -> onTouchEventEraser()
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
        if (isDrawing && currentTool == Tools.PEN)
            canvas.drawPath(mPath, mPaint)

        if (selectedPaths.isNotEmpty() && !selectedBorder.isEmpty)
            canvas.drawPath(selectedBorder, mPaintSelected)

        if (isDrawing) {
            when (currentTool) {
                Tools.RECTANGLE -> onDrawRectangle(canvas)
                Tools.CIRCLE -> onDrawCircle(canvas)
                Tools.LINE -> onDrawLine(canvas)
                Tools.PEN -> {}
                Tools.ERASER -> {}
                Tools.SELECT -> drawSelectedField(canvas)
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
        val removePaths = isMatch(leftIndentation, topIndentation, pathsArray)
        pathsArray.removeAll(removePaths)
        invalidate()
    }

    /**Select*/
    private fun onTouchEventSelect(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = leftIndentation
                startY = topIndentation

                if (selectedPaths.isEmpty()) {
                    isMoving = false
                    selectedBorder.reset()
                } else if (isMatch(startX, startY, selectedPaths.toList()).isNotEmpty()){   //есть выделенные фигуры и нажатие на них
                    isMoving = true
                } else if (isMatch(startX, startY, selectedPaths.toList()).isEmpty()){      //есть выделенные фигуры, но нажатие не по ним
                    selectedPaths.clear()
                    selectedBorder.reset()
                    isMoving = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isMoving) {
                    val dx = Math.abs(leftIndentation - startX)
                    val dy = Math.abs(topIndentation - startY)
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        selectedPaths.forEach {
                            it.path.offset(leftIndentation - startX, topIndentation - startY)
                        }
                        selectedBorder.offset(leftIndentation - startX, topIndentation - startY)
                        startX = leftIndentation
                        startY = topIndentation
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                if (isMoving) {
                    recalculationPointsSelectedPaths()  //есть selectedPaths и обновлены points
                    invalidate()
                }
                if (!isMoving) {
                    isMoving = false
                    drawSelectedField(mCanvas!!)
                }
            }
        }
    }

    //Выделение фигур и рисование для них границы
    private fun drawSelectedField(canvas: Canvas) {
        if (isMoving) return

        //Прямоугольник для выделения
        val top = Math.min(startY, topIndentation)
        val left = Math.min(startX, leftIndentation)
        val bottom = Math.max(startY, topIndentation)
        val right = Math.max(startX, leftIndentation)

        //Рисование предварительной области выделения
        if (isDrawing) {
            val path = Path()
            path.addRect(left, top, right, bottom, Path.Direction.CW)
            canvas.drawPath(path, strokePaintPreparingToSelect)
            canvas.drawPath(path, fillPaintPreparingToSelect)
        }

//        ACTION_UP
//        Рисовка области выделения
//        Пересчет итоговых координат фигур
        if (!isDrawing) {
            selectedPaths.clear()
            pathsArray.forEach {
                it.points.forEach inner@{ point ->
                    if ((top..bottom).contains(point.topIndentation) &&
                        (left..right).contains(point.leftIndentation)
                    ) {
                        selectedPaths.add(it)
                        return@inner
                    }
                }
            }

            //Вычисление прямоугольника для выделения фигур
            var maxTop: Float
            var maxLeft: Float
            var maxBottom: Float
            var maxRight: Float

            if (selectedPaths.size > 0) {
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
                val additionalSpace = 15f
                selectedBorder.addRect(
                    maxLeft - additionalSpace,
                    maxTop - additionalSpace,
                    maxRight + additionalSpace,
                    maxBottom + additionalSpace,
                    Path.Direction.CW
                )
                invalidate()
            }
        }
    }

    private fun recalculationPointsSelectedPaths() {
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
    }

    /**Move*/
    private fun onMoveSelected(event: MotionEvent) {
        when (event.action) {
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

//            MotionEvent.ACTION_UP -> {
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
//                selectedPaths.clear()
//            }
        }
    }

//    private fun onTouchEventMove(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                selectedPaths.addAll(isMatch(leftIndentation, topIndentation, pathsArray))
//                startX = leftIndentation
//                startY = topIndentation
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val dx = Math.abs(leftIndentation - startX)
//                val dy = Math.abs(topIndentation - startY)
//                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//                    selectedPaths.forEach {
//                        it.path.offset(leftIndentation - startX, topIndentation - startY)
//                    }
//                    startX = leftIndentation
//                    startY = topIndentation
//                    invalidate()
//                }
//            }
//            MotionEvent.ACTION_UP -> {
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
//                selectedPaths.clear()
//            }
//        }
//    }

    private fun isMatch(left: Float, top: Float, paths: List<PathData>): Set<PathData> {
        val delta = 50
        val leftInt = left.toInt()
        val topInt = top.toInt()
        val selectedPaths = mutableSetOf<PathData>()

        paths.forEach {
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