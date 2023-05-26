package com.example.flashcardsapp

import android.graphics.Path

data class PathData(val path: Path, val points: ArrayList<FloatPoint>)

data class FloatPoint(val topIndentation: Float, val leftIndentation: Float)