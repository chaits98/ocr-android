package com.extempo.typescan.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.extempo.typescan.databinding.ListItemDocumentBinding
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.DocumentRepository
import com.extempo.typescan.view.HomeActivity
import com.extempo.typescan.view.TextEditorActivity
import kotlinx.android.synthetic.main.list_item_document.view.*
import java.util.*

class DocumentPagedListAdapter(private val context: Context): PagedListAdapter<DocumentItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemDocumentBinding.inflate(inflater, parent, false)
        return ViewHolder(parent.context, binding.root, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).apply { holder.binding.documentItem = getItem(position) }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, TextEditorActivity::class.java)
            intent.putExtra(HomeActivity.TEXT_EDITOR_DOCUMENT_ITEM, getItem(position))
            startActivity(context, intent, Bundle())
        }

        holder.documentDeleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete this document?")
                .setPositiveButton("Yes") { _, _ -> DocumentRepository(context).deleteDocumentItem(holder.binding.documentItem!!) }
                .setNegativeButton("No", null)
                .show()
        }
    }

    class ViewHolder(val context: Context, itemView: View, val binding: ListItemDocumentBinding): RecyclerView.ViewHolder(itemView) {
        val documentDeleteButton: Button = itemView.list_item_document_delete_button

        companion object {
            @JvmStatic
            @BindingAdapter("bind:time")
            fun loadtime(tv: TextView, data: DocumentItem?) {
                data?.let {
                    var time = "at "
                    val cal = Calendar.getInstance(Locale.ENGLISH)
                    cal.timeInMillis = it.timestamp
                    val temp = DateFormat.format("hh:mm", cal).toString()
                    time += temp
                    tv.setText(time, TextView.BufferType.EDITABLE)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<DocumentItem>() {
            override fun areItemsTheSame(oldItem: DocumentItem, newItem: DocumentItem) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: DocumentItem, newItem: DocumentItem) = oldItem == newItem
        }
    }
}