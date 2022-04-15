package com.elchaninov.espmanager.view.main.recycler

import com.elchaninov.espmanager.databinding.RecyclerItemTitleBinding

class ViewHolderTitle(
    private val binding: RecyclerItemTitleBinding,
    override var onListItemClickListener: (ItemType.Device) -> Unit
) : ViewHolderBase(binding) {

    override fun bind(itemType: ItemType) {
        (itemType as ItemType.Title).also { item ->
            binding.itemTitle.text = item.title
        }
    }
}