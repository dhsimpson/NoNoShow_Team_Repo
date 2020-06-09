package com.example.nonoshow.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompanyManageViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is CompanyManage Fragment"
    }
    val text: LiveData<String> = _text
}