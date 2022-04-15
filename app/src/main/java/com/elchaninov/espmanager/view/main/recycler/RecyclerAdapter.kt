package com.elchaninov.espmanager.view.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elchaninov.espmanager.databinding.RecyclerItem75mvBinding
import com.elchaninov.espmanager.databinding.RecyclerItemMsBinding
import com.elchaninov.espmanager.databinding.RecyclerItemPixelBinding
import com.elchaninov.espmanager.databinding.RecyclerItemTitleBinding
import com.elchaninov.espmanager.utils.*

class RecyclerAdapter(
    private var onListItemClickListener: (ItemType.Device) -> Unit
) : RecyclerView.Adapter<ViewHolderBase>() {

    private var data: List<ItemType> = arrayListOf()

    fun setData(devices: List<ItemType>) {
        data = devices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase {
        when (viewType) {
            VIEW_TYPE_DEVICE_MS -> {
                val binding = RecyclerItemMsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolderDeviceMs(binding, onListItemClickListener)
            }
            VIEW_TYPE_DEVICE_75MV -> {
                val binding = RecyclerItem75mvBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolderDevice75Mv(binding, onListItemClickListener)
            }
            VIEW_TYPE_DEVICE_PIXEL -> {
                val binding = RecyclerItemPixelBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolderDevicePixel(binding, onListItemClickListener)
            }
            else -> {
                val binding = RecyclerItemTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolderTitle(binding, onListItemClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolderBase, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val itemType = data[position]) {
            is ItemType.Title -> VIEW_TYPE_TITLE
            is ItemType.Device -> itemType.device.getDeviceType()
        }
    }
}
