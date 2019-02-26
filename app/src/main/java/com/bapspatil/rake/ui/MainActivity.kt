package com.bapspatil.rake.ui

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.bapspatil.rake.R
import com.bapspatil.rake.adapter.BarcodeResultAdapter
import com.bapspatil.rake.adapter.ImageResultAdapter
import com.bapspatil.rake.adapter.TextResultAdapter
import com.bapspatil.rake.databinding.ActivityMainBinding
import com.bapspatil.rake.util.Constants
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.wonderkiln.camerakit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.longToast
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityMainBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var bitmap: Bitmap
    private lateinit var textResultAdapter: TextResultAdapter
    private lateinit var barcodeResultAdapter: BarcodeResultAdapter
    private lateinit var imageResultAdapter: ImageResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        job = Job()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

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
                    showPreview(p0)
                    bitmap = p0!!.bitmap
                }
            })

            captureFab.setOnClickListener {
                if (previewImageView.visibility == View.GONE) {
                    cameraView.captureImage()
                } else {
                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    when (intent.getStringExtra(Constants.KEY_FUNCTION)) {
                        Constants.VALUE_RECOGNIZE_TEXT -> recognizeText(image)
                        Constants.VALUE_SCAN_BARCODE -> scanBarcode(image)
                        Constants.VALUE_LABEL_IMAGE -> labelImage(image)
                    }
                }
            }

            retryFab.setOnClickListener {
                hidePreview()
            }
        }
    }

    private fun labelImage(image: FirebaseVisionImage) {
//        val options = FirebaseVisionLabelDetectorOptions.Builder()
//                .setConfidenceThreshold(0.5f)
//                .build()
//        val detector = FirebaseVision.getInstance()
//                .getVisionLabelDetector(options)
//        detector.detectInImage(image)
//                .addOnSuccessListener { labels ->
//                    for (label in labels) {
//                        val text = label.label
//                        val entityId = label.entityId
//                        val confidence = label.confidence
//                    }
//                    updateUIForImageLabeling(labels)
//                }
//                .addOnFailureListener{ exception ->
//                    Log.d("BARCODE_SCAN", exception.toString())
//                }
//                .addOnCompleteListener {
//                    longToast("Image labeling done!")
//                }
        val options = FirebaseVisionCloudImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.6f)
                .build()
        val detector = FirebaseVision.getInstance()
                .getCloudImageLabeler(options)
        detector.processImage(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val entityId = label.entityId
                        val confidence = label.confidence
                    }
                    updateUIForImageLabeling(labels)
                }
                .addOnFailureListener { exception ->
                    Log.d("BARCODE_SCAN", exception.toString())
                }
                .addOnCompleteListener {
                    longToast("Image labeling done!")
                }
    }

    private fun updateUIForImageLabeling(labels: List<FirebaseVisionImageLabel>) {
        imageResultAdapter = ImageResultAdapter(this@MainActivity, labels)
        binding.resultRecyclerView.adapter = imageResultAdapter
        binding.placeholderTextView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // TODO: Make barcode scanning better.
    private fun scanBarcode(image: FirebaseVisionImage) {
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                .build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        detector.detectInImage(image)
                .addOnSuccessListener {
                    //                    for (barcode in barcodes) {
//                        val bounds = barcode.boundingBox
//                        val corners = barcode.cornerPoints
//
//                        val rawValue = barcode.rawValue
//
//                        val valueType = barcode.valueType
//                        // See API reference for complete list of supported types
//                        when (valueType) {
//                            FirebaseVisionBarcode.TYPE_WIFI -> {
//                                val ssid = barcode.wifi!!.ssid
//                                val password = barcode.wifi!!.password
//                                val type = barcode.wifi!!.encryptionType
//                            }
//                            FirebaseVisionBarcode.TYPE_URL -> {
//                                val title = barcode.url!!.title
//                                val url = barcode.url!!.url
//                            }
//                        }
//                    }
                    for (barcode in it) {
                        updateUIForBarcodeScan(barcode)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("BARCODE_SCAN", exception.toString())
                }
                .addOnCompleteListener {
                    longToast("Barcode scan done!")
                }
    }

    private fun updateUIForBarcodeScan(barcode: FirebaseVisionBarcode) {
        barcodeResultAdapter = BarcodeResultAdapter(this@MainActivity, barcode)
        binding.resultRecyclerView.adapter = barcodeResultAdapter
        binding.placeholderTextView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun recognizeText(image: FirebaseVisionImage) {
        val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        textRecognizer.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    //                    val resultText = firebaseVisionText.text
//                    for (block in firebaseVisionText.textBlocks) {
//                        val blockText = block.text
//                        val blockConfidence = block.confidence
//                        val blockLanguages = block.recognizedLanguages
//                        val blockCornerPoints = block.cornerPoints
//                        val blockFrame = block.boundingBox
//                        for (line in block.lines) {
//                            val lineText = line.text
//                            val lineConfidence = line.confidence
//                            val lineLanguages = line.recognizedLanguages
//                            val lineCornerPoints = line.cornerPoints
//                            val lineFrame = line.boundingBox
//                            for (element in line.elements) {
//                                val elementText = element.text
//                                val elementConfidence = element.confidence
//                                val elementLanguages = element.recognizedLanguages
//                                val elementCornerPoints = element.cornerPoints
//                                val elementFrame = element.boundingBox
//                            }
//                        }
//                    }
                    updateUIForTextRecognition(firebaseVisionText)
                }
                .addOnFailureListener { exception ->
                    Log.d("TEXT_RECOGNITION", exception.toString())
                }
    }

    private fun updateUIForTextRecognition(firebaseVisionText: FirebaseVisionText) {
        textResultAdapter = TextResultAdapter(this@MainActivity, firebaseVisionText.textBlocks)
        binding.resultRecyclerView.adapter = textResultAdapter
        binding.placeholderTextView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @SuppressLint("RestrictedApi")
    private fun showPreview(cameraKitImage: CameraKitImage?) {
        binding.apply {
            cameraView.visibility = View.GONE
            previewImageView.apply {
                setImageBitmap(cameraKitImage?.bitmap)
                visibility = View.VISIBLE
            }
            captureFab.setImageResource(R.drawable.ic_done_white_24dp)
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
