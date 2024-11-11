package com.example.diseasesdetection

import android.app.Application

class Setting : Application() {
    companion object {
        var overlap: Float = 0.3f
        var confidence: Float = 0.3f
        var stroke: Int = 1
    }
}