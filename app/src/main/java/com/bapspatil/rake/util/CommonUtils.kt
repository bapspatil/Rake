package com.bapspatil.rake.util

import android.view.View
import androidx.appcompat.app.AppCompatActivity

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

object CommonUtils {
    private fun hideSystemUI(activity: AppCompatActivity) {
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    private fun showSystemUI(activity: AppCompatActivity) {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}