package com.lygttpod.android.auto.tools.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.lygttpod.android.auto.tools.accessibility.AutoToolsAccessibility
import com.lygttpod.android.auto.tools.databinding.FragmentToolsMainBinding
import com.lygttpod.android.auto.tools.ktx.showPrintFloat


class ToolsMainFragment : Fragment() {

    private var _binding: FragmentToolsMainBinding? = null

    private val binding get() = _binding!!


    private val toolsServiceLiveData = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentToolsMainBinding.inflate(inflater, container, false)
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
            requireContext().isAccessibilityOpened(AutoToolsAccessibility::class.java)
    }

    private fun initObserver() {
        toolsServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.btnNodePrint.isEnabled = open
            binding.btnOpenService.text =
                if (open) "【页面节点速查】服务已开启" else "点击打开【页面节点速查】服务"
            binding.tvIpAddress.visibility = if (open) View.VISIBLE else View.GONE
        }
    }

    private fun initListener() {
        binding.btnOpenService.setOnClickListener {
            activity?.openAccessibilitySetting()
        }

        binding.btnNodePrint.setOnClickListener {
            activity?.application?.let { it.showPrintFloat(it) }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}