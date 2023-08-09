package com.android.wechat.tools.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.wechat.tools.R
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.databinding.ItemFriendBinding
import com.android.wechat.tools.em.FriendStatus


class FriendInfoAdapter : RecyclerView.Adapter<FriendInfoAdapter.FriendInfoViewHolder>() {

    private var list = mutableListOf<WxUserInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendInfoViewHolder {
        return FriendInfoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendInfoViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount() = list.size

    fun setData(list: List<WxUserInfo>) {
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }

    fun addData(data: WxUserInfo) {
        val index = list.indexOfFirst { it.nickName == data.nickName }
        if (index == -1) {
            this.list.add(data)
            notifyItemInserted(itemCount)
        } else {
            this.list[index] = data
            notifyItemChanged(index)
        }
    }

    fun addDatas(newData: MutableList<WxUserInfo>) {
        this.list.addAll(list)
        notifyItemRangeInserted(list.size - newData.size, newData.size)
    }

    class FriendInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemFriendBinding.bind(view)

        fun bindData(wxUserInfo: WxUserInfo) {
            binding.tvNickName.text = wxUserInfo.nickName
            binding.tvWxCode.text = wxUserInfo.wxCode
            binding.tvStatus.text = wxUserInfo.status.status
            val color = when (wxUserInfo.status) {
                FriendStatus.BLACK -> R.color.friend_black
                FriendStatus.DELETE -> R.color.friend_delete
                FriendStatus.ACCOUNT_EXCEPTION -> R.color.friend_exc
                FriendStatus.NORMAL -> R.color.friend_normal
                FriendStatus.UNKNOW -> R.color.friend_unknow
            }
            binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, color))
        }
    }
}