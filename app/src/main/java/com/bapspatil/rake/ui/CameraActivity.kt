package com.bapspatil.rake.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import com.bapspatil.rake.R
import com.bapspatil.rake.adapter.BarcodeResultAdapter
import com.bapspatil.rake.adapter.ImageResultAdapter
import com.bapspatil.rake.adapter.TextResultAdapter
import com.bapspatil.rake.databinding.ActivityCameraBinding
import com.bapspatil.rake.util.CommonUtils.base64
import com.bapspatil.rake.util.CommonUtils.resize
import com.bapspatil.rake.util.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

@SuppressLint("RestrictedApi")
class CameraActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this)
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private lateinit var bitmap: Bitmap
    private lateinit var textResultAdapter: TextResultAdapter
    private lateinit var barcodeResultAdapter: BarcodeResultAdapter
    private lateinit var imageResultAdapter: ImageResultAdapter
    private lateinit var firestoreDb: FirebaseFirestore
    private var userUid: String? = null
    private lateinit var keyFunction: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        job = Job()
        firestoreDb = FirebaseFirestore.getInstance()
        userUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        keyFunction = intent.getStringExtra(Constants.KEY_FUNCTION)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        binding.apply {
            cameraView.addCameraKitListener(object : CameraKitEventListener {
                override fun onEvent(event: CameraKitEvent?) {
                    // Not implemented
                }

                override fun onError(error: CameraKitError?) {
                    // Not implemented
                }

                override fun onVideo(video: CameraKitVideo?) {
                    // Not implemented
                }

                override fun onImage(image: CameraKitImage?) {
                    showPreview(image)
                    bitmap = image!!.bitmap
                }
            })

            captureFab.setOnClickListener {
                if (previewImageView.visibility == View.GONE) {
                    cameraView.captureImage()
                } else {
                    if (!progressDialog.isShowing) {
                        progressDialog.setCanceledOnTouchOutside(false)
                        progressDialog.setMessage(getString(R.string.scanning_image))
                        progressDialog.setOnCancelListener {
                            NavUtils.navigateUpFromSameTask(this@CameraActivity)
                        }
                        progressDialog.show()
                    }
                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    when (keyFunction) {
                        Constants.VALUE_RECOGNIZE_TEXT -> recognizeText(image)
                        Constants.VALUE_SCAN_BARCODE -> scanBarcode(image)
                        Constants.VALUE_LABEL_IMAGE -> labelImage(image)
                    }
                }
            }

            retryFab.setOnClickListener {
                hidePreview()
            }

            saveToFirestoreFab.setOnClickListener {
                // TODO: Save to Firestore here.
                when (keyFunction) {
                    Constants.VALUE_LABEL_IMAGE -> {
                        saveLabelledImageToFirestore()
                    }
                    Constants.VALUE_SCAN_BARCODE -> {
                        saveScannedBarcodeToFirestore()
                    }
                    Constants.VALUE_RECOGNIZE_TEXT -> {
                        saveRecognizedTextToFirestore()
                    }
                }
            }
        }
    }

    private fun saveRecognizedTextToFirestore() {

    }

    private fun saveScannedBarcodeToFirestore() {

    }

    private fun saveLabelledImageToFirestore() {
        // TODO: Use Firebase Storage to store images instead
        progressDialog.show()
        progressDialog.setMessage("Saving data to the cloud...")
        val base64OfImage = bitmap.resize(bitmap.width / 3, bitmap.height / 3).base64()
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width
        val labels = imageResultAdapter.getLabels()

        val labelledImageMap = hashMapOf(
                Constants.KEY_FIRESTORE_LI_IMAGE_FILE to base64OfImage,
                Constants.KEY_FIRESTORE_LI_IMAGE_HEIGHT to imageHeight,
                Constants.KEY_FIRESTORE_LI_IMAGE_WIDTH to imageWidth,
                Constants.KEY_FIRESTORE_LI_LABELS to labels
        )

        val labelledImageDoc = firestoreDb.collection(Constants.KEY_FIRESTORE_USERS)
                .document(userUid!!)
                .collection(Constants.KEY_FIRESTORE_USER_LABELLED_IMAGES)
                .document()
        firestoreDb.runTransaction { transaction ->
            transaction.set(labelledImageDoc, labelledImageMap)
        }
                .addOnFailureListener { e ->
                    longToast(e.message.toString())
                    progressDialog.hide()
                }
                .addOnSuccessListener {
                    longToast("SUCCESS!")
                    progressDialog.hide()
                }
    }

    private fun labelImage(image: FirebaseVisionImage) {
        val options = FirebaseVisionCloudImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.6f)
                .build()
        val detector = FirebaseVision.getInstance()
                .getCloudImageLabeler(options)
        detector.processImage(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
//                        val text = label.text
//                        val entityId = label.entityId
//                        val confidence = label.confidence
                    }
                    updateUIForImageLabeling(labels)
                }
                .addOnFailureListener { exception ->
                    Log.d("IMAGE_LABELLING", exception.toString())
                }
                .addOnCompleteListener {
                    if (progressDialog.isShowing)
                        progressDialog.hide()
                    binding.saveToFirestoreFab.visibility = View.VISIBLE
                }
    }

    private fun updateUIForImageLabeling(labels: List<FirebaseVisionImageLabel>) {
        imageResultAdapter = ImageResultAdapter(this@CameraActivity, labels)
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
                    if (progressDialog.isShowing)
                        progressDialog.hide()
                    binding.saveToFirestoreFab.visibility = View.VISIBLE
                }
    }

    private fun updateUIForBarcodeScan(barcode: FirebaseVisionBarcode) {
        barcodeResultAdapter = BarcodeResultAdapter(this@CameraActivity, barcode)
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
                .addOnCompleteListener {
                    if (progressDialog.isShowing)
                        progressDialog.hide()
                    binding.saveToFirestoreFab.visibility = View.VISIBLE
                }
    }

    private fun updateUIForTextRecognition(firebaseVisionText: FirebaseVisionText) {
        textResultAdapter = TextResultAdapter(this@CameraActivity, firebaseVisionText.textBlocks)
        binding.resultRecyclerView.adapter = textResultAdapter
        binding.placeholderTextView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hidePreview() {
        binding.apply {
            cameraView.visibility = View.VISIBLE
            previewImageView.apply {
                setImageBitmap(null)
                visibility = View.GONE
            }
            captureFab.setImageResource(R.drawable.ic_camera_white_24dp)
            retryFab.visibility = View.GONE
            saveToFirestoreFab.visibility = View.GONE
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

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
