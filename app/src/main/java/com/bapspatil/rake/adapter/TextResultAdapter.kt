package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.item_text.view.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class TextResultAdapter(private val mContext: Context, private val mTextList: List<FirebaseVisionText.TextBlock>) : RecyclerView.Adapter<TextResultAdapter.TextViewHolder>() {

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text, viewGroup, false))
    }

    override fun onBindViewHolder(textViewHolder: TextViewHolder, position: Int) {
        textViewHolder.itemView.textItemTextView.text = mTextList[position].text
    }

    override fun getItemCount() = mTextList.size

    fun getBlocksOfText(): ArrayList<String> {
        val mStringBlocks = arrayListOf<String>()
        mTextList.forEach { label ->
            mStringBlocks.add(label.text)
        }
        return mStringBlocks
    }
}