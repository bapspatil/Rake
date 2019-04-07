package com.bapspatil.rake.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.bapspatil.rake.adapter.PickerAdapter
import com.bapspatil.rake.databinding.ActivityPickerBinding
import com.bapspatil.rake.model.PickerItem
import com.bapspatil.rake.util.Constants
import com.firebase.ui.auth.AuthUI
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
            defaultSharedPreferences.edit(commit = true) { putString(Constants.KEY_CURRENT_USER_UID, userUid) }
        }

        addUserToFirestore()

        setupRecyclerView()
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

    private fun setupRecyclerView() {
        binding.apply {
            pickerRecyclerView.layoutManager = LinearLayoutManager(this@PickerActivity, RecyclerView.HORIZONTAL, false)

            val pickerOptions = arrayListOf(
                    PickerItem("TEXT RECOGNITION", R.drawable.text_recognition, "Take a pic, and let Rake extract the text for you in that pic!", "RECOGNIZE TEXT"),
                    PickerItem("BARCODE SCANNING", R.drawable.barcode_scanning, "Scan a barcode or a QR code and get the information from that code now!", "SCAN CODE"),
                    PickerItem("IMAGE LABELING", R.drawable.image_labelling, "Let Rake label your images for you, so you can look up related info later on the web portal!", "LABEL IMAGES")
            )
            pickerRecyclerView.adapter = PickerAdapter(pickerOptions)

            LinearSnapHelper().attachToRecyclerView(pickerRecyclerView)
            pageIndicator.attachTo(pickerRecyclerView)
        }
    }

    private fun setupAppBar() {
        binding.apply {
            collapsingToolbar.apply {
                setExpandedTitleColor(ContextCompat.getColor(this@PickerActivity, R.color.colorPrimary))
                setCollapsedTitleTextColor(ContextCompat.getColor(this@PickerActivity, R.color.colorPrimary))
            }
            logoutButton.setOnClickListener {
                AuthUI.getInstance()
                        .signOut(this@PickerActivity)
                        .addOnSuccessListener {
                            startActivity<AuthActivity>()
                        }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
