package com.android.wechat.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.android.accessibility.ext.runOnUiThread
import com.android.wechat.tools.adapter.FriendInfoAdapter
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.databinding.FragmentMainBinding
import com.android.wechat.tools.em.FriendStatus
import com.android.wechat.tools.helper.TaskByGroupHelper
import com.android.wechat.tools.helper.FriendStatusHelper
import com.android.wechat.tools.helper.TaskHelper
import com.android.wechat.tools.ktx.formatTime
import com.android.wechat.tools.service.WXAccessibility
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val adapter = FriendInfoAdapter()

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
        binding.friendRv.layoutManager = LinearLayoutManager(context)
        binding.friendRv.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        binding.buttonOpenService.text =
            if (requireActivity().isAccessibilityOpened(WXAccessibility::class.java)) "已打开无障碍服务" else "点击打开无障碍服务"
    }

    private fun initObserver() {
        FriendStatusHelper.friendListResult.observe(viewLifecycleOwner) { finish ->
            val list = mutableListOf<WxUserInfo>()
            FriendStatusHelper.getFriendList()
                .forEach { list.add(WxUserInfo(it, "", FriendStatus.UNKNOW)) }
            adapter.setData(list)
        }
        FriendStatusHelper.taskCallBack = object : FriendStatusHelper.TaskCallBack {
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
        binding.buttonOpenService.setOnClickListener {
            activity?.openAccessibilitySetting()
        }

        binding.buttonFriendList.setOnClickListener {
            GlobalScope.launch {
                TaskHelper.getUserList(true)
            }
        }

        binding.buttonFriendCheck.setOnClickListener {
            GlobalScope.launch {
                TaskHelper.startCheckFromList(FriendStatusHelper.getFriendList())
            }
        }

        binding.buttonCheck.setOnClickListener {
            GlobalScope.launch {
                TaskHelper.startCheck()
            }
        }

        binding.buttonCheckByGroup.setOnClickListener {
            GlobalScope.launch {
                TaskByGroupHelper.startCheckByCreateGroup()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}