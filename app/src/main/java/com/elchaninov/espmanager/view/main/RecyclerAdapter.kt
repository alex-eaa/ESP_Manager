package com.elchaninov.espmanager.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elchaninov.espmanager.databinding.RecyclerMainItemBinding
import com.elchaninov.espmanager.model.DeviceModel

class RecyclerAdapter(
    private var onListItemClickListener: (DeviceModel) -> Unit
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerItemViewHolder>() {

    private var data: List<DeviceModel> = arrayListOf()

    fun setData(data: List<DeviceModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        val binding = RecyclerMainItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecyclerItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerItemViewHolder(private val binding: RecyclerMainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onListItemClickListener(data[layoutPosition]) }
        }

        fun bind(deviceModel: DeviceModel) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                binding.itemDeviceAlias.text = deviceModel.alias
                binding.itemDeviceName.text = deviceModel.name
                "${deviceModel.ip}:${deviceModel.port}".also { binding.itemDeviceHost.text = it }
            }
        }
    }
}
