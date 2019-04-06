package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.bapspatil.rake.ui.CameraActivity
import com.bapspatil.rake.util.Constants
import kotlinx.android.synthetic.main.item_picker.view.*
import org.jetbrains.anko.startActivity

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class PickerAdapter(private val pickerOptions: ArrayList<String>) : RecyclerView.Adapter<PickerAdapter.PickerViewHolder>() {
    private lateinit var mContext: Context

    inner class PickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PickerViewHolder {
        mContext = viewGroup.context
        return PickerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_picker, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: PickerViewHolder, position: Int) {
        viewHolder.itemView.textPickerItemView.text = pickerOptions[position]
        viewHolder.itemView.textPickerItemView.typeface = ResourcesCompat.getFont(mContext, R.font.manrope_bold)
        when (position) {
            0 -> viewHolder.itemView.setOnClickListener { mContext.startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_RECOGNIZE_TEXT) }
            1 -> viewHolder.itemView.setOnClickListener { mContext.startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_SCAN_BARCODE) }
            2 -> viewHolder.itemView.setOnClickListener { mContext.startActivity<CameraActivity>(Constants.KEY_FUNCTION to Constants.VALUE_LABEL_IMAGE) }
        }
    }

    override fun getItemCount() = pickerOptions.size
}
