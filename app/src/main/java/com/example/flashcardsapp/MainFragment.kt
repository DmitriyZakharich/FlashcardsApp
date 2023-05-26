package com.example.flashcardsapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButtonToggleGroup


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawingView = DrawingView(requireActivity())
        drawingView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        val l = view.findViewById<LinearLayout>(R.id.ll)
        l.addView(drawingView)

//        view.findViewById<Button>(R.id.button1).setOnClickListener {
//            drawingView.toCheckMode()
//        }

        view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup).addOnButtonCheckedListener{
            toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.rect -> drawingView.currentShape = Shapes.RECTANGLE
                    R.id.circle -> drawingView.currentShape = Shapes.CIRCLE
                    R.id.line -> drawingView.currentShape = Shapes.LINE
                    R.id.pen ->  drawingView.currentShape = Shapes.SMOOTH_LINE
                    R.id.eraser -> drawingView.currentShape = Shapes.ERASER
                }
            } else
                Log.d("23333333", "else")
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment().apply { }
    }
}