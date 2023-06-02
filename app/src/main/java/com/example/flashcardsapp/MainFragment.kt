package com.example.flashcardsapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.shawnlin.numberpicker.NumberPicker
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
                    R.id.text -> drawingView.currentTool = Tools.TEXT
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

        val values = arrayOf(
            Pair("1", 5f),
            Pair("2", 10f),
            Pair("3", 15f),
            Pair("4", 20f),
            Pair("5", 25f),
            Pair("6", 30f),
            Pair("7", 35f),
            Pair("8", 40f),
            Pair("9", 45f),
            Pair("10", 50f),
            Pair("11", 55f),
            Pair("12", 60f),
            Pair("13", 65f),
            Pair("14", 70f),
            Pair("15", 75f)
        )

        val numberPicker = view.findViewById<NumberPicker>(R.id.number_picker)
        numberPicker.maxValue = values.size
        numberPicker.minValue = 1
        numberPicker.displayedValues = values.map {it.first }.toTypedArray()
        numberPicker.value = values.size/2
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            drawingView.setWidthSize(values[newVal-1].second)
        }


        view.findViewById<ImageButton>(R.id.image_button_undo).setOnClickListener {
            drawingView.undo()
        }

        view.findViewById<ImageButton>(R.id.image_button_redo).setOnClickListener {
            drawingView.redo()
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