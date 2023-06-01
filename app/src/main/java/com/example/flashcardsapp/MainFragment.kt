package com.example.flashcardsapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup
import eltos.simpledialogfragment.SimpleDialog
import eltos.simpledialogfragment.color.SimpleColorDialog


class MainFragment : Fragment(), SimpleDialog.OnDialogResultListener {

    private lateinit var drawingView: DrawingView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawingView = DrawingView(requireActivity())
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
                    R.id.select -> drawingView.currentTool = Tools.SELECT
                }
            }
        }

        view.findViewById<Button>(R.id.move).setOnClickListener {
            SimpleColorDialog.build()
                .title("R.string.pick_a_color")
                .colors(intArrayOf(
                    Color.BLACK,
                    Color.DKGRAY,
                    Color.GRAY,
                    Color.LTGRAY,
                    Color.WHITE,
                    Color.RED,
                    Color.GREEN,
                    Color.BLUE,
                    Color.YELLOW,
                    Color.CYAN,
//                    Color.parseColor("#00ff00"),
//                    Color.parseColor("#00ff00"),
//                    Color.parseColor(R.color.stroke_select2.toString()),
//                    getResources().getColor(R.color.black),
//                    getResources().getColor(R.color.stroke_select)
                    ))
//                .choiceMode(CustomListDialog.)
                .colors(requireContext(), SimpleColorDialog.COLORFUL_COLOR_PALLET)
                .colorPreset(0xff009688.toInt())
                .allowCustom(true)
                .showOutline(SimpleColorDialog.AUTO)
                .show(this, "DIALOG_TAG")

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment().apply { }
    }

    override fun onResult(dialogTag: String, which: Int, extras: Bundle): Boolean {
        @ColorInt val color = extras.getInt(SimpleColorDialog.COLOR)
        drawingView.setColor(color)
        return true
    }
}