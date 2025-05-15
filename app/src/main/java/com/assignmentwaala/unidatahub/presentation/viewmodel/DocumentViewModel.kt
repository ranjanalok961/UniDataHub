package com.assignmentwaala.unidatahub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val _uploadState = MutableStateFlow<ResultState<DocumentModel>>(ResultState.Idle)
    val uploadState = _uploadState.asStateFlow()

    val _documents = MutableStateFlow<ResultState<List<DocumentModel>>>(ResultState.Idle)
    val documents = _documents.asStateFlow()



    fun uploadDocument(document: DocumentModel) {
        Log.d(TAG, "ViewMode document: $document")
        viewModelScope.launch {
            repository.addDocument(document).collect {
                _uploadState.value = it
                Log.d(TAG, "ViewMode result: $it")
            }
        }

    }

    fun getDocuments(category: String) {
        viewModelScope.launch {
            repository.getDocuments(category).collect {
                Log.d(TAG, "ViewMode result: $it")
                _documents.value = it
            }
        }
    }

    fun getUserDocuments(userId: String) {
        viewModelScope.launch {
            repository.getUserDocuments(userId).collect {
                Log.d(TAG, "ViewMode result: $it")
                _documents.value = it
            }
        }
    }


}