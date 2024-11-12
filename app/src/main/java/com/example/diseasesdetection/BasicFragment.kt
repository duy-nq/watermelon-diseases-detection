package com.example.diseasesdetection

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class BasicFragment : Fragment() {
    private lateinit var buttonChooseFromGallery: Button
    private lateinit var buttonSendImage: Button
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar

    private var imageName: String = ""
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.basic_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonChooseFromGallery = view.findViewById(R.id.button_choose_from_gallery)
        buttonSendImage = view.findViewById(R.id.button_send_image)
        imageView = view.findViewById(R.id.image_view)
        progressBar = view.findViewById(R.id.progressBar)

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    imageUri = result.data?.data
                    if (imageUri != null) {
                        imageView.setImageURI(imageUri)
                        val imgArray = imageUri.toString().split("/").toTypedArray()
                        imageName = imgArray[imgArray.size-1]
                        Toast.makeText(requireContext(), imageUri.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception) {
                    println("Caught an InsufficientFundsException: ${e.message}")
                }
            }
        }

        imageView.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
            intent.putExtra("imageUri", imageUri)
            startActivity(intent)
        }

        buttonChooseFromGallery.setOnClickListener {
            val getPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(getPictureIntent)
        }

        buttonSendImage.setOnClickListener {
            // Base 64 Encode
            val encodedFile: String
            val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                encodedFile = Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
            else {
                throw Exception("Fail!")
            }

            val uploadURL = "https://detect.roboflow.com/deeper-diseases/1?api_key=LfVTZkyLWVh9dR2GzXjc&confidence=${Setting.confidence*100}&overlap=${Setting.overlap*100}&format=image&stroke=${Setting.stroke}&labels=true"

            CoroutineScope(Dispatchers.IO).launch {
                var connection: HttpURLConnection? = null

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.VISIBLE
                }

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

                    //Send request
                    val wr = DataOutputStream(
                        connection.outputStream)
                    wr.writeBytes(encodedFile)
                    wr.close()

                    // Get Response
//                    val stream = connection.inputStream
//                    val reader = BufferedReader(InputStreamReader(stream))
//                    var line: String?
//                    while (reader.readLine().also { line = it } != null) {
//                        line?.let { it1 -> Log.d("LINE", it1) }
//                    }
//                    reader.close()

                    // Get Image
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val contentType = connection.getHeaderField("Content-Type")
                        if (contentType == "image/jpeg") {
                            // Get InputStream and decode to Bitmap
                            val resImg = connection.inputStream
                            val bitmap = BitmapFactory.decodeStream(resImg)

                            withContext(Dispatchers.Main) {
                                imageView.setImageBitmap(bitmap) // Display the image in your ImageView
                            }

                            inputStream.close()
                        } else {
                            Log.e("Error", "Unexpected content type: $contentType")
                        }
                    } else {
                        Log.e("Error", "HTTP error: ${connection.responseCode}")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Process is now done!", Toast.LENGTH_SHORT).show()
                    }
                    connection?.disconnect()
                }
            }
        }
    }
}