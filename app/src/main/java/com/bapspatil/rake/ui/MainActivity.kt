package com.bapspatil.rake.ui

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.bapspatil.rake.R
import com.bapspatil.rake.databinding.ActivityMainBinding
import com.bapspatil.rake.util.Constants
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.wonderkiln.camerakit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import kotlin.coroutines.CoroutineContext



class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityMainBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        job = Job()

        binding.apply {
            cameraView.addCameraKitListener(object : CameraKitEventListener {
                override fun onEvent(p0: CameraKitEvent?) {
                    // Not implemented
                }

                override fun onError(p0: CameraKitError?) {
                    // Not implemented
                }

                override fun onVideo(p0: CameraKitVideo?) {
                    // Not implemented
                }

                override fun onImage(p0: CameraKitImage?) {
                    longToast("Pic taken!")
                    showPreview(p0)
                    bitmap = p0!!.bitmap
                }

            })

            captureFab.setOnClickListener {
                if (previewImageView.visibility == View.GONE) {
                    cameraView.captureImage()
                } else {
                    // TODO: Handle the Analyse functionality here
                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
                    textRecognizer.processImage(image)
                            .addOnSuccessListener { firebaseVisionText ->
                                val resultText = firebaseVisionText.text
                                for (block in firebaseVisionText.textBlocks) {
                                    val blockText = block.text
                                    val blockConfidence = block.confidence
                                    val blockLanguages = block.recognizedLanguages
                                    val blockCornerPoints = block.cornerPoints
                                    val blockFrame = block.boundingBox
                                    for (line in block.lines) {
                                        val lineText = line.text
                                        val lineConfidence = line.confidence
                                        val lineLanguages = line.recognizedLanguages
                                        val lineCornerPoints = line.cornerPoints
                                        val lineFrame = line.boundingBox
                                        for (element in line.elements) {
                                            val elementText = element.text
                                            val elementConfidence = element.confidence
                                            val elementLanguages = element.recognizedLanguages
                                            val elementCornerPoints = element.cornerPoints
                                            val elementFrame = element.boundingBox
                                        }
                                    }
                                }
                                startActivity<ResultActivity>(Constants.KEY_TEXT_RESULT to resultText)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TEXT_RECOGNITION", exception.toString())
                            }
                }
            }

            retryFab.setOnClickListener {
                hidePreview()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun hidePreview() {
        binding.apply {
            cameraView.visibility = View.VISIBLE
            previewImageView.apply {
                setImageBitmap(null)
                visibility = View.GONE
            }
            captureFab.setImageResource(R.drawable.ic_camera_white_24dp)
            retryFab.visibility = View.GONE
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showPreview(cameraKitImage: CameraKitImage?) {
        binding.apply {
            cameraView.visibility = View.GONE
            previewImageView.apply {
                setImageBitmap(cameraKitImage?.bitmap)
                visibility = View.VISIBLE
            }
            captureFab.setImageResource(R.drawable.ic_chevron_right_white_24dp)
            retryFab.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cameraView.start()
    }

    override fun onPause() {
        binding.cameraView.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
