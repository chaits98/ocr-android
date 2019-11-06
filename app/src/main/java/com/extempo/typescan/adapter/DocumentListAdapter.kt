package com.extempo.typescan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.extempo.typescan.databinding.ListItemDocumentBinding
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.viewmodel.DocumentListViewModel
import kotlinx.android.synthetic.main.list_item_document.view.*

class DocumentListAdapter(private val context: Context) : RecyclerView.Adapter<DocumentListAdapter.BindingViewHolder>() {
    private var documentList: ArrayList<DocumentItem> = ArrayList()

    private var documentListViewModel: DocumentListViewModel? = null

    fun setList(newList: ArrayList<DocumentItem>) {
        val documentListDiffCallback = DocumentListDiffCallback(documentList, newList)
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(documentListDiffCallback)
        documentList.clear()
        documentList.addAll(newList)
        result.dispatchUpdatesTo(this)
    }

    fun setDocumentListViewModel(model: DocumentListViewModel) {
        this.documentListViewModel = model
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemDocumentBinding.inflate(inflater, parent, false)
        val viewHolder = BindingViewHolder(parent.context, binding.root, binding)
        binding.lifecycleOwner = viewHolder
        return viewHolder
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    override fun getItemId(position: Int): Long {
        return documentList[position].id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.apply {
            holder.binding.documentItem = documentList[position]
//            Glide.with(context)
//                .load(documentList[position].thumbnailImage)
//                .timeout(7000)
//                .placeholder(R.drawable.ic_launcher_background)
//                .into(image)
        }
    }

    override fun onViewAttachedToWindow(holder: BindingViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: BindingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

    open class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    class BindingViewHolder(val context: Context, itemView: View, val binding: ListItemDocumentBinding): RecyclerView.ViewHolder(itemView), LifecycleOwner {

        val documentTitle: TextView = itemView.list_item_document_title
        val documentAuthor: TextView = itemView.list_item_document_author
        val documentDate: TextView = itemView.list_item_document_date

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        }

        fun markAttach() {
            lifecycleRegistry.markState(Lifecycle.State.STARTED)
        }

        fun markDetach() {
            lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    class DocumentListDiffCallback(private val oldList: ArrayList<DocumentItem>, private val newList: ArrayList<DocumentItem>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].toString() == newList[newItemPosition].toString()
        }
    }
}