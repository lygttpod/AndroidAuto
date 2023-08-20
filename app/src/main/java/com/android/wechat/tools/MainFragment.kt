package com.android.wechat.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.android.accessibility.ext.runOnUiThread
import com.android.accessibility.ext.toast
import com.android.wechat.tools.adapter.FriendInfoAdapter
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.databinding.FragmentMainBinding
import com.android.wechat.tools.helper.FriendStatusHelper
import com.android.wechat.tools.helper.TaskByGroupHelper
import com.android.wechat.tools.helper.TaskHelper
import com.android.wechat.tools.ktx.formatTime
import com.android.wechat.tools.ktx.showPrintFloat
import com.android.wechat.tools.service.WXAccessibility
import com.android.wechat.tools.service.WxHbAccessibility
import com.android.wechat.tools.version.currentWXVersion
import com.android.wechat.tools.version.wechatVersionArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val adapter = FriendInfoAdapter()

    private val accServiceLiveData = MutableLiveData<Boolean>()
    private val accHBServiceLiveData = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initListener()
        initObserver()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        WXAccessibility.isInWXApp.set(false)
        accServiceLiveData.value =
            requireActivity().isAccessibilityOpened(WXAccessibility::class.java)
        accHBServiceLiveData.value =
            requireActivity().isAccessibilityOpened(WxHbAccessibility::class.java)
    }

    private fun initObserver() {
        accServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.btnPrintNode.isEnabled = open
            binding.btnGetFriendList.isEnabled = open
            binding.btnCheck.isEnabled = open
            binding.btnCheckByGroup.isEnabled = open

            binding.btnOpenService.text = if (open) "无障碍服务已开启" else "点击打开无障碍服务"

        }

        accHBServiceLiveData.observe(viewLifecycleOwner) { open ->
            val currentStatus = binding.btnWxAutoHb.isChecked
            if (currentStatus == open) return@observe
            binding.btnWxAutoHb.isChecked = open
        }

        FriendStatusHelper.taskCallBack = object : FriendStatusHelper.TaskCallBack {
            override fun onGetAllFriend(list: List<String>) {
                if (activity?.isDestroyed == true) return
                runOnUiThread {
                    adapter.setData(list.map { WxUserInfo(it) })
                }
            }

            override fun onFriendChecked(wxUserInfo: WxUserInfo) {
                if (activity?.isDestroyed == true) return
                runOnUiThread {
                    adapter.addData(wxUserInfo)
                }
            }

            override fun onFriendChecked(list: MutableList<WxUserInfo>) {
                if (activity?.isDestroyed == true) return
                runOnUiThread {
                    adapter.addDatas(list)
                }
            }


            override fun onTaskStart() {

            }

            override fun onTaskEnd(totalTime: Long) {
                runOnUiThread {
                    binding.tvTaskDes.text = "检测总耗时：${totalTime.formatTime()}"
                }
            }

        }
    }

    private fun initListener() {
        binding.btnOpenService.setOnClickListener {
            activity?.openAccessibilitySetting()
        }

        binding.btnPrintNode.setOnClickListener {
            requireActivity().showPrintFloat()
        }

        binding.btnWxVersion.adapter =
            ArrayAdapter(requireContext(), R.layout.item_version, wechatVersionArray)

        binding.btnWxVersion.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentWXVersion = wechatVersionArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.btnGetFriendList.setOnClickListener {
            GlobalScope.launch {
                TaskHelper.getUserList(true)
            }
        }

        binding.btnCheck.setOnClickListener {
            GlobalScope.launch {
                TaskHelper.quickCheck()
            }
        }

        binding.btnCheckByGroup.setOnClickListener {
            GlobalScope.launch {
                TaskByGroupHelper.startCheckByCreateGroup()
            }
        }

        binding.btnWxAutoHb.setOnCheckedChangeListener { buttonView, isChecked ->
            activity?.openAccessibilitySetting()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}