package com.example.firebaseandmapspeople

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {
    val isRemoteConfigFetched = MutableLiveData(false)
}