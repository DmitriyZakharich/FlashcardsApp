package com.example.flashcardsapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView


class CustomDrawView(context: Context?, attrs: AttributeSet?) :
    AppCompatImageView(context!!, attrs) {
    private fun getNewPaintPen(color: Int): Paint {
        val mPaintPen = Paint()
        //Gives the width to the stroke.
        mPaintPen.strokeWidth = 19F//lineWidth
        //To give the smoothness to the outer side of the paint.
        mPaintPen.isAntiAlias = true
        //The colors that are higher precision than the device are   down-sampled y this flag.
        mPaintPen.isDither = true
        //It defines the style of how you want the paint to work, other is fill style which fills the area within the s
        mPaintPen.style = Paint.Style.STROKE
        //It defines how the end of the line should look.
        mPaintPen.strokeCap = Paint.Cap.ROUND
        //Set the color to the line.
        mPaintPen.color = color
        return mPaintPen
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (event.action == MotionEvent.ACTION_DOWN) {
        } else if (event.action == MotionEvent.ACTION_MOVE) {
        } else if (event.action == MotionEvent.ACTION_UP) {
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}