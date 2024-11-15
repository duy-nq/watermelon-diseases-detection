package com.example.diseasesdetection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.slider.Slider

class SettingFragment : Fragment() {
    private var overlap: Float = 0f
    private var confidence: Float = 0f
    private var stroke: Int = 1

    private var saveButton: Button? = null
    private var resetButton: Button? = null

    private lateinit var seekBarOverlap: Slider
    private lateinit var textOverlap: TextView
    private lateinit var seekBarConfidence: Slider
    private lateinit var textConfidence: TextView
    private lateinit var seekBarStroke: Slider
    private lateinit var textStroke: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        seekBarOverlap = view.findViewById(R.id.sliderOverlap)
        textOverlap = view.findViewById(R.id.titleOverlap)
        seekBarConfidence = view.findViewById(R.id.sliderConfidence)
        textConfidence = view.findViewById(R.id.titleConfidence)
        seekBarStroke = view.findViewById(R.id.sliderStroke)
        textStroke = view.findViewById(R.id.titleStroke)

        saveButton = view.findViewById(R.id.buttonSave)
        resetButton = view.findViewById(R.id.buttonReset)

        super.onViewCreated(view, savedInstanceState)

        importSetting()

        seekBarOverlap.addOnChangeListener { _, value, _ ->
            overlap = value / 100f
            "Overlap: $overlap".also { textOverlap.text = it }
        }

        seekBarConfidence.addOnChangeListener { _, value, _ ->
            confidence = value / 100f
            "Confidence: $confidence".also { textConfidence.text = it }
        }

        seekBarStroke.addOnChangeListener { _, value, _ ->
            stroke = value.toInt()
            "Stroke: $stroke".also { textStroke.text = it }
        }

        saveButton?.setOnClickListener {
            saveSetting()
            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        }

        resetButton?.setOnClickListener {
            resetSetting()
            Toast.makeText(requireContext(), "Reset!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSetting() {
        Setting.overlap = overlap
        Setting.confidence = confidence
        Setting.stroke = stroke
    }

    private fun importSetting() {
        overlap = Setting.overlap
        confidence = Setting.confidence
        stroke = Setting.stroke

        updateSeekBars()
    }

    private fun resetSetting() {
        overlap = 0.3f
        confidence = 0.3f
        stroke = 1

        updateSeekBars()
    }

    private fun updateSeekBars() {
        "Overlap: $overlap".also { textOverlap.text = it }
        "Confidence: $confidence".also { textConfidence.text = it }
        "Stroke: $stroke".also { textStroke.text = it }

        seekBarOverlap.value = (overlap * 100)
        seekBarConfidence.value = (confidence * 100)
        seekBarStroke.value = stroke.toFloat()
    }
}