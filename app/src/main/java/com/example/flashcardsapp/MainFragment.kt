package com.example.flashcardsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
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

        val linearLayout = view.findViewById<LinearLayout>(R.id.ll)
        linearLayout.addView(drawingView)

        view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup).addOnButtonCheckedListener{
            toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.rect -> drawingView.currentTool = Tools.RECTANGLE
                    R.id.circle -> drawingView.currentTool = Tools.CIRCLE
                    R.id.line -> drawingView.currentTool = Tools.LINE
                    R.id.pen ->  drawingView.currentTool = Tools.PEN
                    R.id.eraser -> drawingView.currentTool = Tools.ERASER
                    R.id.move -> drawingView.currentTool = Tools.MOVE
                    R.id.select -> drawingView.currentTool = Tools.SELECT
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment().apply { }
    }
}