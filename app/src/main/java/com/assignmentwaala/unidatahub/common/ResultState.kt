package com.assignmentwaala.unidatahub.common

sealed class ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error<T>(val message: String) : ResultState<T>()
    object Loading : ResultState<Nothing>()
    data class Uploading(var bytes: Long, var totalBytes: Long): ResultState<Nothing>()
    object Idle : ResultState<Nothing>()
}