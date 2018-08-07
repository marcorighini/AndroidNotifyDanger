package com.marcorighini.notifydanger.misc.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.reflect.KProperty


class LiveDataDelegate<T>(
        initialState: T
) {
    private val liveData = MutableLiveData<T>()

    init {
        liveData.value = initialState
    }

    fun observe(owner: LifecycleOwner, observer: (T) ->Unit){
        liveData.observe(owner, Observer { observer(it!!) })
    }

    fun observeForever(observer: (T) ->Unit){
        liveData.observeForever { observer(it!!) }
    }

    operator fun setValue(ref: Any, p: KProperty<*>, value: T) {
        liveData.value = value
    }

    operator fun getValue(ref: Any, p: KProperty<*>): T =
            liveData.value!!
}