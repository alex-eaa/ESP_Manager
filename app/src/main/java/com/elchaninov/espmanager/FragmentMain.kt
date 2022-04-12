package com.elchaninov.espmanager

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.databinding.FragmentMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class FragmentMain : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMain by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        viewModel.liveData.observe(viewLifecycleOwner) { setNsdServiceInfo ->
            if (setNsdServiceInfo.isEmpty()) Log.d("qqq", "AVAILABLE services is NULL")
            setNsdServiceInfo.forEachIndexed { index, nsdServiceInfo ->
                Log.d("qqq", "AVAILABLE services: $index - $nsdServiceInfo")
            }
        }

        binding.button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, FragmentMsControl.newInstance())
                .commit()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}