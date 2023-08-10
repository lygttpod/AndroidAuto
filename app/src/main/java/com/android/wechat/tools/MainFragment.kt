package com.android.wechat.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.android.accessibility.ext.runOnUiThread
import com.android.wechat.tools.adapter.FriendInfoAdapter
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.databinding.FragmentMainBinding
import com.android.wechat.tools.em.FriendStatus
import com.android.wechat.tools.helper.FloatManager
import com.android.wechat.tools.helper.TaskByGroupHelper
import com.android.wechat.tools.helper.FriendStatusHelper
import com.android.wechat.tools.helper.TaskHelper
import com.android.wechat.tools.ktx.formatTime
import com.android.wechat.tools.ktx.showPrintFloat
import com.android.wechat.tools.service.WXAccessibility
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val adapter = FriendInfoAdapter()

    private val accServiceLiveData = MutableLiveData<Boolean>()

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
        WXAccessibility.isInWXApp.set(false)
        accServiceLiveData.value = requireActivity().isAccessibilityOpened(WXAccessibility::class.java)
    }

    private fun initObserver() {
        accServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.buttonPrintNode.isEnabled = open
            binding.buttonFriendList.isEnabled = open
            binding.buttonFriendCheck.isEnabled = open
            binding.buttonCheck.isEnabled = open
            binding.buttonCheckByGroup.isEnabled = open

            binding.buttonOpenService.text = if (open) "无障碍服务已开启" else "点击打开无障碍服务"

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
        binding.buttonOpenService.setOnClickListener {
            activity?.openAccessibilitySetting()
        }

        binding.buttonPrintNode.setOnClickListener {
            requireActivity().showPrintFloat()
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
                TaskHelper.quickCheck()
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