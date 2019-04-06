package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class BarcodeResultAdapter(private val mContext: Context, private val mBarcode: FirebaseVisionBarcode) : RecyclerView.Adapter<BarcodeResultAdapter.TextViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TextViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_text, p0, false)
        return TextViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
        viewHolder.textResultTextView.text = mBarcode.displayValue
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textResultTextView: TextView = itemView.textItemTextView
    }

    fun getInfo(): String? = mBarcode.displayValue
}