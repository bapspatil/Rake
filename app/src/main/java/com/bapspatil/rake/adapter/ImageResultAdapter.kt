package com.bapspatil.rake.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class ImageResultAdapter(private val mContext: Context, private val mLabels: List<FirebaseVisionCloudLabel>) : RecyclerView.Adapter<ImageResultAdapter.TextViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TextViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_text, p0, false)
        return TextViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mLabels != null && mLabels.isNotEmpty()) mLabels.size
        else 0
    }

    override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
        viewHolder.textResultTextView.text = mLabels[position].label + "\t" + mLabels[position].confidence
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textResultTextView: TextView = itemView.textItemTextView
    }
}