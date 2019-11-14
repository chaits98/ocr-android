package com.extempo.typescan.adapter

import android.content.Context
import com.extempo.typescan.model.AuthorResult
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.BaseAdapter
import com.extempo.typescan.R
import kotlin.math.round


class AuthorSpinnerAdapter(val context: Context, private var listItems: ArrayList<AuthorResult>): BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return listItems.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.spinner_item_author, parent, false)
            vh = ItemRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        vh.authorName.text = listItems[position].author.name
        if(!listItems[position].percentageMatch.isNaN()) vh.matchPercent.text = String.format("%.02f", listItems[position].percentageMatch)
        else vh.matchPercent.text = "Not enough data"
        return view
    }

    private class ItemRowHolder(row: View?) {
        val authorName: TextView = row?.findViewById(R.id.spinner_item_author_name) as TextView
        val matchPercent: TextView = row?.findViewById(R.id.spinner_item_author_match_percent) as TextView
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}