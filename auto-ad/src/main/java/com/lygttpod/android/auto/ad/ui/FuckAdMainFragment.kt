package com.lygttpod.android.auto.ad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.lygttpod.android.auto.ad.accessibility.FuckADAccessibility
import com.lygttpod.android.auto.ad.databinding.FragmentFuckAdMainBinding


class FuckAdMainFragment : Fragment() {

    private var _binding: FragmentFuckAdMainBinding? = null

    private val binding get() = _binding!!


    private val toolsServiceLiveData = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFuckAdMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        toolsServiceLiveData.value =
            requireContext().isAccessibilityOpened(FuckADAccessibility::class.java)
    }

    private fun initObserver() {
        toolsServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.btnOpenService.text =
                if (open) "【自动跳过广告(FuckAD)】服务已开启" else "打开【自动跳过广告(FuckAD)】服务"
        }
    }

    private fun initListener() {
        binding.btnOpenService.setOnClickListener {
            activity?.openAccessibilitySetting()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}