package com.example.flashcardsapp

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

class PaintView(context: Context) : View(context) {

    val ll = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    private val path = Path()
    private val brush = Paint()


    init {
        brush.isAntiAlias = true
        brush.color = Color.MAGENTA
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeWidth = 8f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
            MotionEvent.ACTION_UP -> path.lineTo(x, y)
            else -> return false
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPath(path, brush)
    }
}


class PaintView2(context: Context) : View(context) {

    val ll = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    private val path = Path()
    private val brush = Paint()

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f


    init {
        brush.isAntiAlias = true
        brush.color = Color.MAGENTA
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeWidth = 8f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val x = event.x
//        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                // Set the end to prevent initial jump (like on the demo recording)
                endX = event.x
                endY = event.y
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                endX = event.x
                endY = event.y
                invalidate()
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(startX, startY, endX, endY, brush)
    }
}