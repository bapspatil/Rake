package com.bapspatil.rake.ui

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import com.bapspatil.rake.R
import com.bapspatil.rake.adapter.MoreInfoAdapter
import com.bapspatil.rake.databinding.ActivityRisBinding
import com.bapspatil.rake.model.RisInputModel
import com.bapspatil.rake.model.RisOutputModel
import com.bapspatil.rake.ris.RisApiService
import com.bapspatil.rake.util.CommonUtils.makeUiLight
import com.bapspatil.rake.util.Constants
import kotlinx.android.synthetic.main.activity_ris.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.browse
import org.jetbrains.anko.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class RisActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityRisBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ris)
        job = Job()

        makeUiLight()

        if (!progressDialog.isShowing) {
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setMessage("Getting more info...")
            progressDialog.setOnCancelListener {
                NavUtils.navigateUpFromSameTask(this@RisActivity)
            }
            progressDialog.show()
        }

        val labelForRis = intent.getStringExtra(Constants.KEY_LABEL_FOR_RIS)
        if (labelForRis != null) {
            val risInputModel = RisInputModel(labelForRis)
            RisApiService.create()
                    .getRisInfo(risInputModel)
                    .enqueue(object : Callback<RisOutputModel> {
                        override fun onFailure(call: Call<RisOutputModel>, t: Throwable) {
                            longToast("Failed to fetch your data!")
                            if (progressDialog.isShowing)
                                progressDialog.hide()
                            NavUtils.navigateUpFromSameTask(this@RisActivity)
                        }

                        override fun onResponse(call: Call<RisOutputModel>, response: Response<RisOutputModel>) {
                            if (response.isSuccessful && response.body() != null) {
                                updateUiWithData(response.body()!!)
                                if (progressDialog.isShowing)
                                    progressDialog.hide()
                            }
                        }
                    })
        }
    }

    private fun updateUiWithData(risOutputModel: RisOutputModel) {
        moreInfoRecyclerView.adapter = MoreInfoAdapter(this, risOutputModel)
        googleImagesButton.setOnClickListener {
            risOutputModel.images?.get(0)?.let { it1 -> browse(it1) }
        }
        labelledImagesDashboardButton.setOnClickListener {
            browse("https://rake.now.sh/#/dashboard/labelled-images")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
