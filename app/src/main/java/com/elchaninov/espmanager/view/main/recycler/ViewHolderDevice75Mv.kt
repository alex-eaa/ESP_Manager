package com.elchaninov.espmanager.view.main.recycler

import android.content.res.ColorStateList
import androidx.core.widget.ImageViewCompat
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.RecyclerItem75mvBinding

class ViewHolderDevice75Mv(
    private val binding: RecyclerItem75mvBinding,
    override var onListItemClickListener: (ItemType.Device) -> Unit
) : ViewHolderBase(binding) {

    override fun bind(itemType: ItemType) {
        (itemType as ItemType.Device).also { item ->
            binding.root.setOnClickListener { onListItemClickListener(itemType) }

            binding.itemDeviceAlias.text = item.device.alias
            binding.itemDeviceName.text = item.device.name

            if (item.device.ip != null) {
                "${item.device.ip}:${item.device.port}".also { binding.itemDeviceHost.text = it }

                ImageViewCompat.setImageTintList(
                    binding.iconNet,
                    ColorStateList.valueOf(binding.iconNet.resources.getColor(R.color.my_color))
                )
            } else {
                binding.itemDeviceHost.text = ""
                ImageViewCompat.setImageTintList(
                    binding.iconNet,
                    ColorStateList.valueOf(binding.iconNet.resources.getColor(R.color.gray_text))
                )
            }
        }
    }
}