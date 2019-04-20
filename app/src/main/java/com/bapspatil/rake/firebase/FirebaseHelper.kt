package com.bapspatil.rake.firebase

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bapspatil.rake.util.CommonUtils.toByteArray
import com.bapspatil.rake.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.longToast
import java.util.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class FirebaseHelper constructor(private var bitmap: Bitmap, private val userUid: String?, private val globalStorageRef: StorageReference, private val firestoreDb: FirebaseFirestore, private val context: Context, private val progressDialog: ProgressDialog) {

    fun saveLabelledImageToFirestore(labels: ArrayList<String>) {
        val timestamp = Calendar.getInstance().time.toString()
        val imageFileName = timestamp.replace(" ", "")
        val imageByteArrayToUpload = bitmap.toByteArray()
        var imageFileUrl: String?
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width

        val imageStorageRef = userUid?.let { globalStorageRef.child("$it/labelledImages/$imageFileName") }
        val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
        val uploadTask = imageStorageRef?.putBytes(imageByteArrayToUpload, metadata)
        uploadTask?.addOnFailureListener { e ->
            Log.e("UPLOAD_ERROR", e.message)
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageStorageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                    imageFileUrl = downloadTask.result.toString()
                    Log.d("IMAGE_URL", imageFileUrl)

                    val labelledImageMap = hashMapOf(
                            Constants.KEY_FIRESTORE_LI_TIMESTAMP to timestamp,
                            Constants.KEY_FIRESTORE_LI_IMAGE_FILE to imageFileUrl,
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
                                context.longToast(e.message.toString())
                                progressDialog.hide()
                            }
                            .addOnSuccessListener {
                                context.longToast("Success!")
                                progressDialog.hide()
                            }
                }
            }
        }
    }

    fun saveScannedBarcodeToFirestore(info: String?) {
        val timestamp = Calendar.getInstance().time.toString()
        val imageFileName = timestamp.replace(" ", "")
        val imageByteArrayToUpload = bitmap.toByteArray()
        var imageFileUrl: String?
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width

        val imageStorageRef = userUid?.let { globalStorageRef.child("$it/scannedBarcodes/$imageFileName") }
        val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
        val uploadTask = imageStorageRef?.putBytes(imageByteArrayToUpload, metadata)
        uploadTask?.addOnFailureListener { e ->
            Log.e("UPLOAD_ERROR", e.message)
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageStorageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                    imageFileUrl = downloadTask.result.toString()
                    Log.d("IMAGE_URL", imageFileUrl)

                    val scannedBarcodeMap = hashMapOf(
                            Constants.KEY_FIRESTORE_SB_TIMESTAMP to timestamp,
                            Constants.KEY_FIRESTORE_SB_IMAGE_FILE to imageFileUrl,
                            Constants.KEY_FIRESTORE_SB_IMAGE_HEIGHT to imageHeight,
                            Constants.KEY_FIRESTORE_SB_IMAGE_WIDTH to imageWidth,
                            Constants.KEY_FIRESTORE_SB_INFO to info
                    )

                    val scannedBarcodeDoc = firestoreDb.collection(Constants.KEY_FIRESTORE_USERS)
                            .document(userUid!!)
                            .collection(Constants.KEY_FIRESTORE_USER_SCANNED_BARCODES)
                            .document()
                    firestoreDb.runTransaction { transaction ->
                        transaction.set(scannedBarcodeDoc, scannedBarcodeMap)
                    }
                            .addOnFailureListener { e ->
                                context.longToast(e.message.toString())
                                progressDialog.hide()
                            }
                            .addOnSuccessListener {
                                context.longToast("Success!")
                                progressDialog.hide()
                            }
                }
            }
        }
    }

    fun saveRecognizedTextToFirestore(blocks: ArrayList<String>) {
        val timestamp = Calendar.getInstance().time.toString()
        val imageFileName = timestamp.replace(" ", "")
        val imageByteArrayToUpload = bitmap.toByteArray()
        var imageFileUrl: String?
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width

        val imageStorageRef = userUid?.let { globalStorageRef.child("$it/recognizedText/$imageFileName") }
        val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
        val uploadTask = imageStorageRef?.putBytes(imageByteArrayToUpload, metadata)
        uploadTask?.addOnFailureListener { e ->
            Log.e("UPLOAD_ERROR", e.message)
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageStorageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                    imageFileUrl = downloadTask.result.toString()
                    Log.d("IMAGE_URL", imageFileUrl)

                    val recognizedTextMap = hashMapOf(
                            Constants.KEY_FIRESTORE_RT_TIMESTAMP to timestamp,
                            Constants.KEY_FIRESTORE_RT_IMAGE_FILE to imageFileUrl,
                            Constants.KEY_FIRESTORE_RT_IMAGE_HEIGHT to imageHeight,
                            Constants.KEY_FIRESTORE_RT_IMAGE_WIDTH to imageWidth,
                            Constants.KEY_FIRESTORE_RT_BLOCKS to blocks
                    )

                    val recognizedTextDoc = firestoreDb.collection(Constants.KEY_FIRESTORE_USERS)
                            .document(userUid!!)
                            .collection(Constants.KEY_FIRESTORE_USER_RECOGNIZED_TEXT)
                            .document()
                    firestoreDb.runTransaction { transaction ->
                        transaction.set(recognizedTextDoc, recognizedTextMap)
                    }
                            .addOnFailureListener { e ->
                                context.longToast(e.message.toString())
                                progressDialog.hide()
                            }
                            .addOnSuccessListener {
                                context.longToast("Success!")
                                progressDialog.hide()
                            }
                }
            }
        }
    }
}