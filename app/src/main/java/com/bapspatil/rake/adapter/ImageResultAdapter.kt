package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class ImageResultAdapter(private val mContext: Context, private val mLabels: List<FirebaseVisionImageLabel>) : RecyclerView.Adapter<ImageResultAdapter.TextViewHolder>() {

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
        viewHolder.itemView.textItemTextView.text = mLabels[position].text
    }

    override fun getItemCount() = if (mLabels.isNotEmpty()) mLabels.size else 0

    fun getLabels(): ArrayList<String> {
        val mStringLabels = arrayListOf<String>()
        mLabels.forEach { label ->
            mStringLabels.add(label.text)
        }
        return mStringLabels
    }
}