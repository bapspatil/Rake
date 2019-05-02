package com.bapspatil.rake.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapspatil.rake.R
import com.bapspatil.rake.model.RisOutputModel
import kotlinx.android.synthetic.main.item_text.view.*
import org.jetbrains.anko.browse

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class MoreInfoAdapter(private val mContext: Context, private val mRisOutputModel: RisOutputModel) : RecyclerView.Adapter<MoreInfoAdapter.TextViewHolder>() {

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
        val title = mRisOutputModel.titles?.get(position)
        val link = mRisOutputModel.links?.get(position)
        viewHolder.itemView.textItemTextView.text = title
        viewHolder.itemView.textItemTextView.setOnClickListener {
            link?.let { it1 -> mContext.browse(it1) }
        }
    }

    override fun getItemCount() = mRisOutputModel.titles!!.size

}