package com.assignmentwaala.unidatahub.presentation.viewmodel

import CategoryData
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryData>>(listOf(
        CategoryData("Research Paper", 0, Color(0xFF4A78FA), Color(0xFF3F5DFF), R.drawable.ic_research),
        CategoryData("Conference Paper", 0, Color(0xFFFF6B6B), Color(0xFFFF4797), R.drawable.ic_conference),
        CategoryData("Journal Paper", 0, Color(0xFF43CB9D), Color(0xFF35A57C), R.drawable.ic_journal),
        CategoryData("Book", 0, Color(0xFFFFA645), Color(0xFFFF8C42), R.drawable.ic_book),
        CategoryData("Other", 0, Color(0xFF9C42F5), Color(0xFF7239EA), R.drawable.ic_alt_book)
    ))

    val categories = _categories.asStateFlow()


    fun getCategories() {
        Log.d(TAG, "ViewMode getCategories called")
        viewModelScope.launch {
            repository.getCategories().collect {
                Log.d(TAG, "ViewMode result: $it")
                when(it){
                    is ResultState.Success -> {
                        _categories.value = _categories.value.map { category ->
                            category.copy(
                                count = it.data
                                    .find { it.name == category.name }?.documentCount ?: 0
                            )
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }
}