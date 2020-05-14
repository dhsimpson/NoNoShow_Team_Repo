package com.example.nonoshow.ui.bookingList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookingListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is BoolingList Fragment"
    }
    val text: LiveData<String> = _text
}