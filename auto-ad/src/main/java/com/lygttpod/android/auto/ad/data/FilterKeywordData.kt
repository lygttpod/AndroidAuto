package com.lygttpod.android.auto.ad.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterKeywordData(val list: MutableList<String>) : Parcelable
