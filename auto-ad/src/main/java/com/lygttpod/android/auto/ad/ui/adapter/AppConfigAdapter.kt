package com.lygttpod.android.auto.ad.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lygttpod.android.auto.ad.R
import com.lygttpod.android.auto.ad.data.AdApp
import com.lygttpod.android.auto.ad.databinding.ItemAppBinding

class AppConfigAdapter : RecyclerView.Adapter<AppConfigAdapter.AppConfigViewHolder>() {

    private var list: MutableList<AdApp> = mutableListOf()

    var onItemClick: ((AdApp) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppConfigViewHolder {
        return AppConfigViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AppConfigViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    fun setData(list: MutableList<AdApp>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateData(app: AdApp) {
        val index = this.list.indexOfFirst { it.packageName == app.packageName }
        if (index > -1) {
            list[index] = app
            notifyItemChanged(index)
        }
    }

    inner class AppConfigViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAppBinding.bind(view)
        var data: AdApp? = null

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(
                    data ?: return@setOnClickListener
                )
            }
        }

        fun bindData(adApp: AdApp) {
            this.data = adApp
            binding.tvAppName.text = adApp.appName
        }
    }
}