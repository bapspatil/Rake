package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class BarcodeResultAdapter(private val mContext: Context, private val mBarcode: FirebaseVisionBarcode) : RecyclerView.Adapter<BarcodeResultAdapter.TextViewHolder>() {

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
        viewHolder.itemView.textItemTextView.text = mBarcode.displayValue
    }

    override fun getItemCount() = 1

    fun getInfo(): String? = mBarcode.displayValue
}