package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemHeaderBinding
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(
    private val headerTitle: String,
    private val clickListener: ElectionListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val TYPE_LIST_HEADER = 0
    private val TYPE_LIST_ITEM = 1

    fun addHeaderAndSubmitList(list: List<Election>?) {
        val items = when (list) {
            null -> listOf(DataItem.Header)
            else -> listOf(DataItem.Header) + list.map { DataItem.ElectionItem(it) }
        }
        submitList(items)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LIST_HEADER -> ViewHolderElectionHeader.from(parent)
            TYPE_LIST_ITEM -> ViewHolderElectionBinding.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> TYPE_LIST_HEADER
            is DataItem.ElectionItem -> TYPE_LIST_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderElectionHeader -> {
                holder.bind(headerTitle)
            }
            is ViewHolderElectionBinding -> {
                holder.bind(
                    (getItem(position) as DataItem.ElectionItem).election,
                    clickListener
                )
            }
        }
    }

    class ViewHolderElectionBinding(private var binding: ListItemElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(election: Election, clickListener: ElectionListener) {
            binding.election = election
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderElectionBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemElectionBinding.inflate(layoutInflater, parent, false)
                return ViewHolderElectionBinding(binding)
            }
        }
    }

    class ViewHolderElectionHeader(private var binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title = title
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderElectionHeader {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeaderBinding.inflate(layoutInflater, parent, false)
                return ViewHolderElectionHeader(binding)
            }
        }
    }
}

sealed class DataItem {
    data class ElectionItem(val election: Election) : DataItem() {
        override val id = election.id
    }

    object Header : DataItem() {
        override val id = Int.MIN_VALUE
    }

    abstract val id: Int
}

class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}