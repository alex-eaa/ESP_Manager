package com.elchaninov.espmanager

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.databinding.FragmentMsControlBinding

class FragmentMsControl : Fragment(R.layout.fragment_ms_control) {

    private var _binding: FragmentMsControlBinding? = null
    private val binding get() = _binding!!

    private var ip: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsControlBinding.bind(view)

        ip = FragmentMsControlArgs.fromBundle(requireArguments()).ipAddress
        binding.ipAddress.text = ip

        binding.imageLight.setImageResource(R.drawable.light_on)
        ImageViewCompat.setImageTintList(
            binding.imagePir1,
            ColorStateList.valueOf(getColor(requireContext(), R.color.purple_700))
        )
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}