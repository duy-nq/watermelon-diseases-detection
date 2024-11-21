package com.example.diseasesdetection

interface StatisticsCallback {
    fun onStatisticsResult(data: Map<String, Map<String, Int>>)
    fun onError(error: String)
}