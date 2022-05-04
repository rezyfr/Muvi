package com.andriiginting.base_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class MuviBaseFlowViewModel<T> : ViewModel() {

    abstract val initialState: T

    protected val _state: MutableStateFlow<T> by lazy { MutableStateFlow(initialState) }
    val state: StateFlow<T>
        get() = _state

    val addDisposable by lazy { CompositeDisposable() }

    override fun onCleared() {
        addDisposable.clear()
    }
}