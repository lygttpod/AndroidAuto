package com.lygttpod.android.auto.tools.manager

import androidx.lifecycle.MutableLiveData

object ContentManger {

    var printContent: String? = null
        set(value) {
            field = value
            printContentLiveData.postValue(value)
        }

    var printContentLiveData: MutableLiveData<String> = MutableLiveData()
}