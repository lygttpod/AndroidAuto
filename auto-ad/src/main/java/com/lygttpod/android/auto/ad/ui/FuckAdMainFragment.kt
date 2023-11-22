package com.lygttpod.android.auto.ad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.accessibility.ext.default
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.android.accessibility.ext.toast
import com.lygttpod.android.auto.ad.R
import com.lygttpod.android.auto.ad.accessibility.FuckADAccessibility
import com.lygttpod.android.auto.ad.data.AdApp
import com.lygttpod.android.auto.ad.databinding.DialogAppConfigBinding
import com.lygttpod.android.auto.ad.databinding.DialogFilterKeywordBinding
import com.lygttpod.android.auto.ad.databinding.FragmentFuckAdMainBinding
import com.lygttpod.android.auto.ad.task.FuckADTask
import com.lygttpod.android.auto.ad.ui.adapter.AppConfigAdapter


class FuckAdMainFragment : Fragment() {

    private var _binding: FragmentFuckAdMainBinding? = null

    private val binding get() = _binding!!

    private val toolsServiceLiveData = MutableLiveData<Boolean>()

    private var appConfigAdapter = AppConfigAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFuckAdMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
        FuckADTask.getKeywordList()
    }

    private fun initView() {
        appConfigAdapter.onItemClick = { adApp ->
            val view = layoutInflater.inflate(R.layout.dialog_app_config, null)
            val binding = DialogAppConfigBinding.bind(view)
            binding.enableCheck.isChecked = adApp.enableCheck
            binding.enableCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                adApp.enableCheck = isChecked
            }
            binding.etNodeAction.setText(adApp.adNode.action)
            binding.etNodeActionMaxLength.setText("${adApp.getMaxLength()}")
            binding.etNodeId.setText(adApp.getNodeId())
            AlertDialog
                .Builder(requireContext())
                .setView(view)
                .setTitle(adApp.appName)
                .setNegativeButton("取消") { _, _ ->
                    {
                    }
                }
                .setPositiveButton("保存") { _, _ ->
                    run {
                        adApp.adNode.apply {
                            action = binding.etNodeAction.text.toString()
                            actionMaxLength =
                                binding.etNodeActionMaxLength.text?.toString()?.toIntOrNull() ?: 5
                            id = binding.etNodeId.text.default()
                        }
                        updateData(adApp)
                    }
                }
                .show()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appConfigAdapter
        }
    }

    private fun updateData(adApp: AdApp) {
        appConfigAdapter.updateData(adApp)
        FuckADTask.updateData(adApp)
    }

    override fun onResume() {
        super.onResume()
        toolsServiceLiveData.value =
            requireContext().isAccessibilityOpened(FuckADAccessibility::class.java)
        FuckADTask.analysisAppConfig()
    }

    private fun initObserver() {
        toolsServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.btnOpenService.text =
                if (open) "【自动跳过广告(FuckAD)】服务已开启" else "打开【自动跳过广告(FuckAD)】服务"
        }
        FuckADTask.fuckAdAppsLiveData.observe(viewLifecycleOwner) {
            it?.fuckAd?.apps?.let { appConfigAdapter.setData(it) }
        }
    }

    private fun initListener() {
        binding.btnOpenService.setOnClickListener {
            activity?.openAccessibilitySetting()
        }
        binding.btnFilterKeyword.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_filter_keyword, null)
            val binding = DialogFilterKeywordBinding.bind(view)
            binding.tvFilterKeywordList.text = FuckADTask.getKeywordList()?.list.toString()
            AlertDialog
                .Builder(requireContext())
                .setView(view)
                .setTitle("过滤关键词")
                .setNegativeButton("取消") { _, _ ->
                    {
                    }
                }
                .setPositiveButton("添加") { _, _ ->
                    run {
                        val keyword = binding.etKeyword.text?.toString().default()
                        if (keyword.isBlank()) {
                            requireContext().toast("添加的关键词不能为空")
                        } else {
                            FuckADTask.updateKeywordList(keyword)
                        }
                    }
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}