package com.lygttpod.android.auto.wx.ui

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
import com.lygttpod.android.auto.wx.R
import com.lygttpod.android.auto.wx.adapter.FriendInfoAdapter
import com.lygttpod.android.auto.wx.data.WxUserInfo
import com.lygttpod.android.auto.wx.databinding.FragmentWxMainBinding
import com.lygttpod.android.auto.wx.helper.FriendStatusHelper
import com.lygttpod.android.auto.wx.helper.HBTaskHelper
import com.lygttpod.android.auto.wx.helper.TaskByGroupHelper
import com.lygttpod.android.auto.wx.helper.TaskHelper
import com.lygttpod.android.auto.wx.ktx.formatTime
import com.lygttpod.android.auto.wx.service.WXAccessibility
import com.lygttpod.android.auto.wx.version.currentWXVersion
import com.lygttpod.android.auto.wx.version.wechatVersionArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WxMainFragment : Fragment() {

    private var _binding: FragmentWxMainBinding? = null

    private val binding get() = _binding!!

    private val adapter = FriendInfoAdapter()

    private val accServiceLiveData = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWxMainBinding.inflate(inflater, container, false)
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
            requireContext().isAccessibilityOpened(WXAccessibility::class.java)
    }

    private fun initObserver() {
        accServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.chAutoHb.isEnabled = open
            binding.btnGetFriendList.isEnabled = open
            binding.btnCheck.isEnabled = open
            binding.btnCheckByGroup.isEnabled = open

            binding.btnOpenService.text = if (open) "无障碍服务已开启" else "点击打开无障碍服务"

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
                TaskHelper.getUserList(requireContext().applicationContext, true)
            }
        }

        binding.btnCheck.setOnClickListener {
            GlobalScope.launch {
                TaskHelper.quickCheck(requireContext().applicationContext)
            }
        }

        binding.btnCheckByGroup.setOnClickListener {
            GlobalScope.launch {
                TaskByGroupHelper.startCheckByCreateGroup(requireContext().applicationContext)
            }
        }

        binding.chAutoHb.setOnCheckedChangeListener { buttonView, isChecked ->
            HBTaskHelper.autoFuckMoney(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}