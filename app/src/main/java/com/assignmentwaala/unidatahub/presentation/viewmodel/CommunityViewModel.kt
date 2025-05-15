package com.assignmentwaala.unidatahub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.CommunityModel
import com.assignmentwaala.unidatahub.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _communities = MutableStateFlow<ResultState<List<CommunityModel>>>(ResultState.Idle)
    val communities = _communities.asStateFlow()

    private val _createCommunityResult = MutableStateFlow<ResultState<CommunityModel>>(ResultState.Idle)
    val createCommunityResult = _createCommunityResult.asStateFlow()

    fun getCommunities() {
        viewModelScope.launch {
            repository.getCommunities().collect {
                _communities.value = it
            }
        }
    }

    fun createCommunity(name: String, description: String) {
        viewModelScope.launch {
            repository.createCommunity(name, description).collect {
                _createCommunityResult.value = it

                if(it is ResultState.Success)
                    getCommunities()
            }
        }
    }

    fun deleteCommunity(communityId: String) {
        viewModelScope.launch {
            repository.deleteCommunity(communityId).collect {
                if(it is ResultState.Success)
                    getCommunities()
            }
        }
    }

}
