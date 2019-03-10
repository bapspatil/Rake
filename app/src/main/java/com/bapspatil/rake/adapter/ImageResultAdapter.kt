package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class ImageResultAdapter(private val mContext: Context, private val mLabels: List<FirebaseVisionImageLabel>) : RecyclerView.Adapter<ImageResultAdapter.TextViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TextViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_text, p0, false)
        return TextViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mLabels != null && mLabels.isNotEmpty()) mLabels.size
        else 0
    }

    override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
        viewHolder.textResultTextView.text = mLabels[position].text + "\t" + mLabels[position].confidence
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textResultTextView: TextView = itemView.textItemTextView
    }

    fun getLabels(): ArrayList<String> {
        val mStringLabels = arrayListOf<String>()
        mLabels.forEach { label ->
            mStringLabels.add(label.text)
        }
        return mStringLabels
    }
}