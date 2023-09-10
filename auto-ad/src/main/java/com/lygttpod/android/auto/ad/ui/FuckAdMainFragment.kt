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
import com.lygttpod.android.auto.ad.task.FuckADTask


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
        FuckADTask.analysisAppConfig()
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
        FuckADTask.fuckAdAppsLiveData.observe(viewLifecycleOwner) {
            val sb = StringBuilder("当前设备支持的跳过启动页广告的APP\n\n")
            it?.fuckAd?.apps?.forEach { sb.append("${it.appName}\n") }
            binding.tvSupportApp.text = sb.toString()
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