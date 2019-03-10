package com.bapspatil.rake.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bapspatil.rake.R
import com.bapspatil.rake.databinding.ActivityPickerBinding
import com.bapspatil.rake.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import kotlin.coroutines.CoroutineContext

class PickerActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityPickerBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var firebaseUser: FirebaseUser? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private var userUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_picker)
        job = Job()
        firestoreDb = FirebaseFirestore.getInstance()
        setupAppBar()

        // Saving the user's UID in SharedPreferences
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userUid = firebaseUser?.uid
        if (defaultSharedPreferences.getString(Constants.KEY_CURRENT_USER_UID, "") == "") {
            defaultSharedPreferences.edit().putString(Constants.KEY_CURRENT_USER_UID, userUid).apply()
        }

        addUserToFirestore()

        setClickListeners()
    }

    private fun addUserToFirestore() {
        val user = hashMapOf(Constants.KEY_FIRESTORE_USER_FULL_NAME to firebaseUser?.displayName)
        firestoreDb.collection(Constants.KEY_FIRESTORE_USERS)
                .document(userUid!!)
                .set(user, SetOptions.merge())
                .addOnSuccessListener {
                    // longToast("Successfully added new user!")
                }
    }

    private fun setClickListeners() {
        binding.apply {
            recognizeTextCardView.setOnClickListener {
                startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_RECOGNIZE_TEXT)
            }
            scanBarcodeCardView.setOnClickListener {
                startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_SCAN_BARCODE)
            }
            labelImageCardView.setOnClickListener {
                startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_LABEL_IMAGE)
            }
        }
    }

    private fun setupAppBar() {
        binding.apply {
            collapsingToolbar.apply {
                // TODO: Change this to transparent when you add the header image, doofus.
                setExpandedTitleColor(ContextCompat.getColor(this@PickerActivity, android.R.color.white))
                setCollapsedTitleTextColor(ContextCompat.getColor(this@PickerActivity, R.color.white))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
