package com.bapspatil.rake.firebase

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bapspatil.rake.util.CommonUtils.resize
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
        val imageFileName = Calendar.getInstance().time.toString().replace(" ", "")
        val imageByteArrayToUpload = bitmap.resize(bitmap.width / 3, bitmap.height / 3).toByteArray()
        var imageFileUrl: String?
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width

        val imageStorageRef = userUid?.let { globalStorageRef.child("$it/$imageFileName") }
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

    fun saveRecognizedTextToFirestore() {
    }

    fun saveScannedBarcodeToFirestore() {
    }
}