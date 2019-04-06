package com.bapspatil.rake.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import com.bapspatil.rake.R
import com.bapspatil.rake.adapter.BarcodeResultAdapter
import com.bapspatil.rake.adapter.ImageResultAdapter
import com.bapspatil.rake.adapter.TextResultAdapter
import com.bapspatil.rake.databinding.ActivityCameraBinding
import com.bapspatil.rake.firebase.FirebaseHelper
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var bitmap: Bitmap
    private lateinit var textResultAdapter: TextResultAdapter
    private lateinit var barcodeResultAdapter: BarcodeResultAdapter
    private lateinit var imageResultAdapter: ImageResultAdapter
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var globalStorageRef: StorageReference
    private var userUid: String? = null
    private lateinit var keyFunction: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        job = Job()

        userUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        keyFunction = intent.getStringExtra(Constants.KEY_FUNCTION)

        firebaseStorage = FirebaseStorage.getInstance()
        globalStorageRef = firebaseStorage.reference

        firestoreDb = FirebaseFirestore.getInstance()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        binding.apply {
            cameraView.requestPermissions(this@CameraActivity)

            captureFab.setOnClickListener {
                if (previewImageView.visibility == View.INVISIBLE) {
                    cameraView.captureImage { cameraKitView, bytes ->
                        showPreview(bytes!!)
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
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
                progressDialog.show()
                progressDialog.setMessage("Saving data to the cloud...")

                val firebaseHelper = FirebaseHelper(bitmap, userUid, globalStorageRef, firestoreDb, this@CameraActivity, progressDialog)

                when (keyFunction) {
                    Constants.VALUE_LABEL_IMAGE -> {
                        firebaseHelper.saveLabelledImageToFirestore(imageResultAdapter.getLabels())
                    }
                    Constants.VALUE_SCAN_BARCODE -> {
                        firebaseHelper.saveScannedBarcodeToFirestore(barcodeResultAdapter.getInfo())
                    }
                    Constants.VALUE_RECOGNIZE_TEXT -> {
                        firebaseHelper.saveRecognizedTextToFirestore(textResultAdapter.getBlocksOfText())
                    }
                }
            }
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

    private fun scanBarcode(image: FirebaseVisionImage) {
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                .build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        detector.detectInImage(image)
                .addOnSuccessListener {
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
        val textRecognizer = FirebaseVision.getInstance().cloudTextRecognizer
        textRecognizer.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
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
                visibility = View.INVISIBLE
            }
            captureFab.setImageResource(R.drawable.ic_camera_white_24dp)
            retryFab.visibility = View.GONE
            saveToFirestoreFab.visibility = View.GONE
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showPreview(byteArray: ByteArray) {
        binding.apply {
            previewImageView.apply {
                setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                visibility = View.VISIBLE
            }
            cameraView.visibility = View.GONE
            captureFab.setImageResource(R.drawable.ic_done_white_24dp)
            retryFab.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        binding.cameraView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.cameraView.onResume()
    }

    override fun onPause() {
        binding.cameraView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.cameraView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
