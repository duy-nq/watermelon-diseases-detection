package com.example.diseasesdetection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

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
        val seekBarOverlap = view.findViewById<SeekBar>(R.id.seekBarOverlap)
        val textOverlap = view.findViewById<TextView>(R.id.textOverlap)
        val seekBarConfidence = view.findViewById<SeekBar>(R.id.seekBarConfidence)
        val textConfidence = view.findViewById<TextView>(R.id.textConfidence)
        val seekBarStroke = view.findViewById<SeekBar>(R.id.seekBarStroke)
        val textStroke = view.findViewById<TextView>(R.id.textStroke)

        saveButton = view.findViewById(R.id.button)

        super.onViewCreated(view, savedInstanceState)

        importSetting()

        "Overlap: $overlap".also { textOverlap.text = it }
        "Confidence: $confidence".also { textConfidence.text = it }
        "Stroke: $stroke".also { textStroke.text = it }

        seekBarOverlap.progress = (overlap * 100).toInt()
        seekBarConfidence.progress = (confidence * 100).toInt()
        seekBarStroke.progress = stroke

        seekBarOverlap.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                overlap = progress / 100f
                "Overlap: $overlap".also { textOverlap.text = it }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle Confidence SeekBar changes
        seekBarConfidence.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                confidence = progress / 100f
                "Confidence: $confidence".also { textConfidence.text = it }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarStroke.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                stroke = progress
                "Stroke: $progress".also { textStroke.text = it }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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