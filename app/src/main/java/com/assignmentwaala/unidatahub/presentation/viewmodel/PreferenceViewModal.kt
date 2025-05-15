package com.assignmentwaala.unidatahub.presentation.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.domain.PrefDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModal @Inject constructor(private val prefDataStore: PrefDataStore) : ViewModel() {
    private val _isGuestLogin = MutableStateFlow(false)
    val isGuestLogin = _isGuestLogin.asStateFlow()

    init {
        viewModelScope.launch {
            prefDataStore.isGuestLogin.collect {
                _isGuestLogin.value = it
            }
        }
    }

    fun setIsGuestLogin(isGuestLogin: Boolean) {
        viewModelScope.launch {
            prefDataStore.setIsGuestLogin(isGuestLogin)
        }
    }

}