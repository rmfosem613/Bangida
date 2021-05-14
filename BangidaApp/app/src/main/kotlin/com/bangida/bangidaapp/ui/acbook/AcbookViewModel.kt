package com.bangida.bangidaapp.ui.acbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AcbookViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is acbook Fragment"
    }
    val text: LiveData<String> = _text
}