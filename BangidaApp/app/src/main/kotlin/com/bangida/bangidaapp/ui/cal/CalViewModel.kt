package com.bangida.bangidaapp.ui.cal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is cal Fragment"
    }
    val text: LiveData<String> = _text
}