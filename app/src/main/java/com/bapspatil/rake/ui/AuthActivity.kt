package com.bapspatil.rake.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bapspatil.rake.BuildConfig
import com.bapspatil.rake.R
import com.bapspatil.rake.util.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.coroutines.CoroutineContext

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class AuthActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        job = Job()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity<PickerActivity>()
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            AuthUI.IdpConfig.GoogleBuilder().build()
                    ))
                    .setLogo(R.drawable.full_logo)
                    .setTheme(R.style.FirebaseUITheme)
                    .setAlwaysShowSignInMethodScreen(true)
                    .setTosAndPrivacyPolicyUrls("https://github.com/bapspatil/privacy/blob/master/Rake-Terms-Of-Service.md",
                            "https://github.com/bapspatil/privacy/blob/master/Rake-Privacy-Policy.md")
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                    .build(),
                    RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                val isFirstLaunch = defaultSharedPreferences.getBoolean(Constants.KEY_PREFERENCE_FIRST_LAUNCH, true)
                if (isFirstLaunch) {
                    startActivity<IntroActivity>()
                } else {
                    startActivity<PickerActivity>()
                }
                finish()
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    longToast("Sign in canceled.")
                    return
                }

                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    longToast("No internet connection!")
                    return
                }

                longToast("Error unknown.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val RC_SIGN_IN = 1354
    }
}
