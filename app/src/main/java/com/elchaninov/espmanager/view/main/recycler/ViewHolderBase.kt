package com.elchaninov.espmanager.view.main.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewHolderBase(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    abstract var onListItemClickListener: (ItemType.Device) -> Unit

    abstract fun bind(itemType: ItemType)
}