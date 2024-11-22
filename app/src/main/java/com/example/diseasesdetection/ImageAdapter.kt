package com.example.diseasesdetection

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(
    private val imageUris: List<Uri>,
    private val jsonData: List<JsonData>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewCard)
        val predictionText: TextView = itemView.findViewById(R.id.textPredictionResult)
        val confidenceText: TextView = itemView.findViewById(R.id.textConfidenceScore)
    }

    data class JsonData (
        val predictions: List<Prediction>,
        val image: Image
    )

    data class Prediction (
        val x: Double = 0.0,
        val y: Double = 0.0,
        val width: Double = 0.0,
        val height: Double = 0.0,
        val `class`: String = "NONE",
        val confidence: Double = 0.0,
    )

    data class Image (
        val width: Int = 0,
        val height: Int = 0
    )

    // Find the position of highest confidence in predictions
    private fun findHighestConfidence(predictions: List<Prediction>): Int {

        val highestConfidencePrediction = predictions
            .filter { it.`class` != "Healthy" }
            .maxByOrNull { it.confidence } ?: predictions.maxByOrNull { it.confidence }

        val highestConfidenceIndex = predictions.indexOf(highestConfidencePrediction)

        return highestConfidenceIndex
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_card, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = imageUris[position]
        holder.imageView.setImageURI(uri)

        if (position < jsonData.size) {
            val data = jsonData[position]
            if (data.predictions.isNotEmpty()) {
                val highestConfidenceIndex = findHighestConfidence(data.predictions)
                holder.predictionText.text = data.predictions[highestConfidenceIndex].`class`
                holder.confidenceText.text = buildString {
                    append("%.2f".format(data.predictions[highestConfidenceIndex].confidence * 100))
                    append("%")
                }
            }
            else {
                holder.predictionText.text = "N/A"
                holder.confidenceText.text = holder.itemView.context.getString(R.string.no_prediction)
            }
        }
    }

    override fun getItemCount(): Int = imageUris.size
}