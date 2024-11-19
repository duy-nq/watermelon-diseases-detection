package com.example.diseasesdetection

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AdvancedFragment : Fragment() {
    private lateinit var imageUris: MutableList<Uri>
    private lateinit var recyclerViewImages: RecyclerView
    private lateinit var placeHolderText: TextView
    private lateinit var imagesAdapter: ImageAdapter
    private lateinit var jsonData: MutableList<ImageAdapter.JsonData>

    private val multipleImagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        uris?.let {
            val previousSize = imageUris.size
            imageUris.clear()
            imagesAdapter.notifyItemRangeRemoved(0, previousSize)
            imageUris.addAll(it)
            imagesAdapter.notifyItemRangeInserted(0, imageUris.size)
            updateUIAfterSelection()
        }
    }

    private fun uriToBase64(imageUri: Uri): String {
        val encodedFile: String
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            encodedFile = Base64.encodeToString(bytes, Base64.NO_WRAP)

            return encodedFile
        }
        else {
            throw Exception("Fail!")
        }
    }

    private fun makePrediction(encodedFile: String, callback: PredictionCallback) {
        val uploadURL = "https://detect.roboflow.com/deeper-diseases/1?api_key=LfVTZkyLWVh9dR2GzXjc&confidence=${Setting.confidence*100}&overlap=${Setting.overlap*100}&format=json&stroke=${Setting.stroke}&labels=true"

        CoroutineScope(Dispatchers.IO).launch {
            var connection: HttpURLConnection? = null

            try {
                // Configure connection to URL
                val url = URL(uploadURL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded")
                connection.setRequestProperty("Content-Length",
                    encodedFile.toByteArray().size.toString()
                )
                connection.setRequestProperty("Content-Language", "en-US")
                connection.useCaches = false
                connection.doOutput = true

                val wr = DataOutputStream(
                    connection.outputStream)
                wr.writeBytes(encodedFile)
                wr.close()

                val stream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(stream))
                var line: String?
                val response = StringBuilder()
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = Gson().fromJson(response.toString(), ImageAdapter.JsonData::class.java)
                withContext(Dispatchers.Main) {
                    callback.onPredictionResult(jsonResponse)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Process is now done!", Toast.LENGTH_SHORT).show()
                }
                connection?.disconnect()
            }
        }
    }

    private fun updateUIAfterSelection() {
        if (imageUris.isNotEmpty()) {
            placeHolderText.visibility = View.GONE
            recyclerViewImages.visibility = View.VISIBLE
        } else {
            placeHolderText.visibility = View.VISIBLE
            recyclerViewImages.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.multi_image_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : PredictionCallback {
            override fun onPredictionResult(result: ImageAdapter.JsonData) {
                jsonData.add(result)
                imagesAdapter.notifyItemChanged(jsonData.size-1)
            }

            override fun onError(error: String) {
                Log.e("Error", error)
            }
        }

        recyclerViewImages = view.findViewById(R.id.recyclerViewImages)
        placeHolderText = view.findViewById(R.id.placeholder_text)
        imageUris = mutableListOf()
        jsonData = mutableListOf()
        imagesAdapter = ImageAdapter(imageUris, jsonData)

        recyclerViewImages.apply {
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = imagesAdapter
        }

        view.findViewById<Button>(R.id.button_select_images).setOnClickListener {
            multipleImagePickerLauncher.launch("image/*")
        }

        view.findViewById<Button>(R.id.button_predict).setOnClickListener {
            for (uri in imageUris) {
                val encodedFile = uriToBase64(uri)
                makePrediction(encodedFile, callback)
            }
        }
    }
}