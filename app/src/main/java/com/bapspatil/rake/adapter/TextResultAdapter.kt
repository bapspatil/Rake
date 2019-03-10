package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class TextResultAdapter(private val mContext: Context, private val mTextList: List<FirebaseVisionText.TextBlock>) : RecyclerView.Adapter<TextResultAdapter.TextViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TextViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_text, p0, false)
        return TextViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTextList.size
    }

    override fun onBindViewHolder(p0: TextViewHolder, p1: Int) {
        p0.textResultTextView.text = mTextList[p1].text
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textResultTextView: TextView = itemView.textItemTextView
    }
}