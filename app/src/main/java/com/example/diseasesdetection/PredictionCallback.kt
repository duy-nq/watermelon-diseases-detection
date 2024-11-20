package com.example.diseasesdetection

import android.net.Uri

interface PredictionCallback {
    fun onPredictionResult(result: ImageAdapter.JsonData)
    fun onError(error: String)

    fun sendData(result: ImageAdapter.JsonData, imageUri: Uri)
}