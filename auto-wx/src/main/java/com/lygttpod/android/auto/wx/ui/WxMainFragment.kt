package com.lygttpod.android.auto.wx.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.openAccessibilitySetting
import com.lygttpod.android.auto.wx.R
import com.lygttpod.android.auto.wx.adapter.FriendInfoAdapter
import com.lygttpod.android.auto.wx.databinding.FragmentWxMainBinding
import com.lygttpod.android.auto.wx.helper.FriendStatusHelper
import com.lygttpod.android.auto.wx.helper.HBTaskHelper
import com.lygttpod.android.auto.wx.helper.TaskByGroupHelper
import com.lygttpod.android.auto.wx.helper.TaskHelper
import com.lygttpod.android.auto.wx.ktx.formatTime
import com.lygttpod.android.auto.wx.service.WXAccessibility
import com.lygttpod.android.auto.wx.version.currentWXVersion
import com.lygttpod.android.auto.wx.version.wechatVersionArray


class WxMainFragment : Fragment() {

    private var _binding: FragmentWxMainBinding? = null

    private val binding get() = _binding!!

    private val adapter = FriendInfoAdapter()

    private val accServiceLiveData = MutableLiveData<Boolean>()
    private val taskEndLiveData = MutableLiveData<Long>()
    private val taskStartLiveData = MutableLiveData<String>()

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

    private fun showData() {
        adapter.setData(FriendStatusHelper.getUserResultList())
        binding.clFilter.visibility =
            if (FriendStatusHelper.getUserResultList().isNotEmpty()) View.VISIBLE else View.GONE
    }


    override fun onResume() {
        super.onResume()
        WXAccessibility.isInWXApp.set(false)
        accServiceLiveData.value =
            requireContext().isAccessibilityOpened(WXAccessibility::class.java)
        showData()
    }

    private fun initObserver() {
        accServiceLiveData.observe(viewLifecycleOwner) { open ->
            binding.chAutoHb.isEnabled = open
            binding.btnGetFriendList.isEnabled = open
            binding.btnCheck.isEnabled = open
            binding.btnCheckByGroup.isEnabled = open

            binding.btnOpenService.text = if (open) "【微信自动化】服务已开启" else "打开【微信自动化】服务"

        }

        taskStartLiveData.observe(viewLifecycleOwner) { taskName ->
            binding.tvTaskDes.text = taskName
        }

        taskEndLiveData.observe(viewLifecycleOwner) { totalTime ->
            binding.tvTaskDes.text = "任务耗时：${totalTime.formatTime()}"
            binding.clFilter.visibility = View.VISIBLE
        }

        FriendStatusHelper.taskCallBack = object : FriendStatusHelper.TaskCallBack {

            override fun onTaskStart(taskName: String) {
                taskStartLiveData.postValue(taskName)
            }

            override fun onTaskEnd(totalTime: Long) {
                taskEndLiveData.postValue(totalTime)
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
            clear()
            TaskHelper.startGetUserTask(requireContext().applicationContext, true)
        }

        binding.btnCheck.setOnClickListener {
            val isContinueCheck = FriendStatusHelper.lastCheckUser != null
            if (isContinueCheck) {
                showContinueCheckTipDialog()
            } else {
                clear()
                TaskHelper.startCheckTask(requireContext().applicationContext, false)
            }
        }

        binding.btnCheckByGroup.setOnClickListener {
            clear()
            TaskByGroupHelper.startTask(requireContext().applicationContext)
        }

        binding.chAutoHb.setOnCheckedChangeListener { buttonView, isChecked ->
            HBTaskHelper.autoFuckMoney(isChecked)
        }

        binding.btnFilterAll.setOnClickListener {
            adapter.setData(FriendStatusHelper.filterAllData())
        }

        binding.btnFilterNotNormal.setOnClickListener {
            adapter.setData(FriendStatusHelper.filterNotNormalData())
        }

        binding.btnFilterUncheck.setOnClickListener {
            adapter.setData(FriendStatusHelper.filterUnCheckData())
        }

    }

    private fun clear() {
        FriendStatusHelper.reset()
        adapter.clear()
    }

    private fun showContinueCheckTipDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("温馨提示")
            .setMessage("上次已检测到【${FriendStatusHelper.lastCheckUser?.nickName}】\n\n是否继续检测")
            .setCancelable(true)
            .setNegativeButton("重新检测") { dialog, _ ->
                adapter.clear()
                TaskHelper.startCheckTask(requireContext().applicationContext, false)
                dialog.dismiss()
            }
            .setPositiveButton("继续检测") { dialog, _ ->
                TaskHelper.startCheckTask(requireContext().applicationContext, true)
                dialog.dismiss()
            }.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}