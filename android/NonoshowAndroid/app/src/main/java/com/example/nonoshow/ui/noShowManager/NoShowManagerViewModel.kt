package com.example.nonoshow.ui.noShowManager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoShowManagerViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is NoShowManager Fragment"
    }
    val text: LiveData<String> = _text
}