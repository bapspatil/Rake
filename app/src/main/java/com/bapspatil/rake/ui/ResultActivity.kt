package com.bapspatil.rake.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.bapspatil.rake.R
import com.bapspatil.rake.databinding.ActivityResultBinding
import com.bapspatil.rake.util.Constants
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class ResultActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityResultBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)
        job = Job()
        setupAppBar()
        testTextView.text = intent.getStringExtra(Constants.KEY_TEXT_RESULT)
    }

    private fun setupAppBar() {
        binding.apply {
            collapsingToolbar.apply {
                setExpandedTitleColor(ContextCompat.getColor(this@ResultActivity, android.R.color.transparent))
                setCollapsedTitleTextColor(ContextCompat.getColor(this@ResultActivity, R.color.white))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
