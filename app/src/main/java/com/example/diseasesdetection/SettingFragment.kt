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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val seekBarOverlap = view.findViewById<Slider>(R.id.sliderOverlap)
        val textOverlap = view.findViewById<TextView>(R.id.textOverlap)
        val seekBarConfidence = view.findViewById<Slider>(R.id.sliderConfidence)
        val textConfidence = view.findViewById<TextView>(R.id.textConfidence)
        val seekBarStroke = view.findViewById<Slider>(R.id.sliderStroke)
        val textStroke = view.findViewById<TextView>(R.id.textStroke)

        saveButton = view.findViewById(R.id.button)

        super.onViewCreated(view, savedInstanceState)

        importSetting()

        "Overlap: $overlap".also { textOverlap.text = it }
        "Confidence: $confidence".also { textConfidence.text = it }
        "Stroke: $stroke".also { textStroke.text = it }

        seekBarOverlap.value = (overlap * 100)
        seekBarConfidence.value = (confidence * 100)
        seekBarStroke.value = stroke.toFloat()

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
    }

    private fun saveSetting() {
        Setting.overlap = overlap
        Setting.confidence = confidence
        Setting.stroke = stroke
    }

    fun importSetting() {
        overlap = Setting.overlap
        confidence = Setting.confidence
        stroke = Setting.stroke
    }
}