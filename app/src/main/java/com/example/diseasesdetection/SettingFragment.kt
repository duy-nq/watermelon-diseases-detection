package com.example.diseasesdetection

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat.recreate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.Slider
import java.util.Locale

class SettingFragment : Fragment() {
    private var overlap: Float = 0f
    private var confidence: Float = 0f
    private var stroke: Int = 1

    private var saveButton: Button? = null
    private var resetButton: Button? = null
    private var languageSwitch: SwitchCompat? = null

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
        languageSwitch = view.findViewById(R.id.languageSwitch)

        super.onViewCreated(view, savedInstanceState)

        importSetting()
        languageSwitch?.setChecked(Setting.isEnglish)

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

        languageSwitch?.setOnCheckedChangeListener { _, _ ->
            if (Setting.isEnglish) {
                changeLanguage(requireContext(), "vi")
                Toast.makeText(requireContext(), "Changed to Vietnamese!", Toast.LENGTH_SHORT).show()
                Setting.isEnglish = false
            } else {
                changeLanguage(requireContext(), "en")
                Toast.makeText(requireContext(), "Changed to English!", Toast.LENGTH_SHORT).show()
                Setting.isEnglish = true
            }

            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavView.selectedItemId = R.id.basic

            recreate(requireActivity())
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

    private fun changeLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

}