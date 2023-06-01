package com.example.flashcardsapp

import android.graphics.Paint
import android.graphics.Path

data class PathData(val path: Path, val points: ArrayList<FloatPoint>, val paint: Paint)

data class FloatPoint(val topIndentation: Float, val leftIndentation: Float)