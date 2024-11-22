package com.example.diseasesdetection

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.database.FirebaseDatabase

class StatisticFragment : Fragment() {
    private lateinit var totalImage: TextView
    private lateinit var barChart: BarChart
    private lateinit var recyclerView: RecyclerView

    private lateinit var uid: String
    private lateinit var database: FirebaseDatabase

    private lateinit var userData: List<RetrieveData>
    private lateinit var dataset: Map<String, Map<String, Int>>

    data class RetrieveData(
        val date: String = "",
        val uri: String = "",
        val result: Result = Result()
    )

    data class Result(
        val image: Image = Image(),
        val predictions: List<Prediction>? = null // Optional field
    )

    data class Prediction(
        val x: Double = 0.0,
        val y: Double = 0.0,
        val width: Double = 0.0,
        val height: Double = 0.0,
        val `class`: String = "NONE",
        val confidence: Double = 0.0
    )

    data class Image(
        val width: Int = 0,
        val height: Int = 0
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_statistic_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : StatisticsCallback {
            override fun onStatisticsResult(data: Map<String, Map<String, Int>>) {
                val (dates, barData) = prepareGeneralBarChartData(data)
                setupBarChart(barChart, dates, barData)

                recyclerView.adapter = CommonClassesAdapter(getTopThreeClasses(data))
            }

            override fun onError(error: String) {
                Log.e("Error", error)
            }
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        totalImage = view.findViewById(R.id.totalImagesNumber)
        barChart = view.findViewById(R.id.barChartTrends)

        recyclerView = view.findViewById(R.id.recyclerCommonClasses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        uid = sharedPreferences.getString("uid", "") ?: ""
        database = FirebaseDatabase.getInstance()

        userData = emptyList()
        dataset = emptyMap()

        getData(callback)
    }

    fun getTopThreeClasses(data: Map<String, Map<String, Int>>): List<Pair<String, Int>> {
        val classCounts = mutableMapOf<String, Int>()

        for (dayData in data.values) {
            for ((className, count) in dayData) {
                classCounts[className] = classCounts.getOrDefault(className, 0) + count
            }
        }

        return classCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key to it.value }
    }

    private fun getDateCount(retrieveData: List<RetrieveData>) : Map<String, Map<String, Int>> {
        val dateClassCounts = mutableMapOf<String, Map<String, Int>>()

        for (data in retrieveData) {
            val date = data.date
            var predictions = data.result.predictions

            if (!dateClassCounts.containsKey(date)) {
                dateClassCounts[date] = mutableMapOf()
            }

            if (predictions == null) {
                predictions = emptyList()
            }

            dateClassCounts[date] = getClassCounts(predictions, dateClassCounts[date])

            Log.d("DATA!", "Date: $dateClassCounts")
        }

        return dateClassCounts
    }

    private fun getClassCounts(predictions: List<Prediction>, map: Map<String, Int>?): Map<String, Int> {
        val classCounts: MutableMap<String, Int> = if (map != null) {
            if (map.isEmpty()) {
                mutableMapOf()
            } else {
                map.toMutableMap()
            }
        } else {
            mutableMapOf()
        }

        if (predictions.isEmpty()) {
            classCounts["NONE"] = classCounts.getOrDefault("NONE", 0) + 1
            return classCounts
        }

        for (prediction in predictions) {
            classCounts[prediction.`class`] = classCounts.getOrDefault(prediction.`class`, 1) + 1
        }

        return classCounts
    }

    private fun prepareGeneralBarChartData(data: Map<String, Map<String, Int>>): Pair<List<String>, BarData> {
        val dates = data.keys.sorted() // Sort dates to maintain chronological order
        val classNames = mutableSetOf<String>()

        // Collect all unique classes
        data.values.forEach { counts -> classNames.addAll(counts.keys) }

        val barDataSets = mutableListOf<BarDataSet>()
        val entriesByClass = mutableMapOf<String, MutableList<BarEntry>>()

        // Initialize entries for each class
        classNames.forEach { className ->
            entriesByClass[className] = mutableListOf()
        }

        // Populate entries for each date
        dates.forEachIndexed { index, date ->
            val counts = data[date] ?: emptyMap()
            classNames.forEach { className ->
                val count = counts[className] ?: 0
                entriesByClass[className]?.add(BarEntry(index.toFloat(), count.toFloat()))
            }
        }

        // Create BarDataSet for each class
        entriesByClass.forEach { (className, entries) ->
            val dataSet = BarDataSet(entries, className)
            dataSet.color = when (className) {
                "Mosaic" -> android.graphics.Color.RED
                "Downy_mildew" -> android.graphics.Color.BLUE
                "NONE" -> android.graphics.Color.GRAY
                else -> android.graphics.Color.GREEN
            }
            barDataSets.add(dataSet)
        }

        val barData = BarData(barDataSets as List<IBarDataSet>?)
        barData.barWidth = 0.2f // Adjust bar width for clarity
        return Pair(dates, barData)
    }

    private fun setupBarChart(barChart: BarChart, dates: List<String>, barData: BarData) {
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(dates) // Convert index to date labels
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.setFitBars(true)
        barChart.invalidate() // Refresh the chart
    }

    private fun getData(callback: StatisticsCallback) {
        database.getReference("History").child(uid).get()
            .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                userData = snapshot.children.mapNotNull { it.getValue(RetrieveData::class.java) }
                totalImage.text = userData.size.toString()

                dataset = getDateCount(userData)
                callback.onStatisticsResult(dataset)
            } else {
                Log.d("Firebase", "No data found")
            }
        }
            .addOnFailureListener { error ->
            Log.e("Firebase", "Error retrieving data: ${error.message}")
        }
    }
}