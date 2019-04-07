package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.bapspatil.rake.model.PickerItem
import com.bapspatil.rake.ui.CameraActivity
import com.bapspatil.rake.util.Constants
import kotlinx.android.synthetic.main.item_picker.view.*
import org.jetbrains.anko.startActivity

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class PickerAdapter(private val pickerOptions: ArrayList<PickerItem>) : RecyclerView.Adapter<PickerAdapter.PickerViewHolder>() {
    private lateinit var mContext: Context

    inner class PickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PickerViewHolder {
        mContext = viewGroup.context
        return PickerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_picker, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: PickerViewHolder, position: Int) {
        viewHolder.itemView.textPickerItemView.text = pickerOptions[position].title
        viewHolder.itemView.featureImageView.setImageResource(pickerOptions[position].drawable)
        viewHolder.itemView.descriptionPickerTextView.text = pickerOptions[position].description
        viewHolder.itemView.startButton.text = pickerOptions[position].buttonText

        when (position) {
            0 -> viewHolder.itemView.startButton.setOnClickListener { mContext.startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_RECOGNIZE_TEXT) }
            1 -> viewHolder.itemView.startButton.setOnClickListener { mContext.startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_SCAN_BARCODE) }
            2 -> viewHolder.itemView.startButton.setOnClickListener { mContext.startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_LABEL_IMAGE) }
        }
    }

    override fun getItemCount() = pickerOptions.size
}
