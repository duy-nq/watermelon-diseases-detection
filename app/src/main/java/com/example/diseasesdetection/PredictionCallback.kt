package com.example.diseasesdetection

interface PredictionCallback {
    fun onPredictionResult(result: ImageAdapter.JsonData)
    fun onError(error: String)
}