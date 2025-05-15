package com.assignmentwaala.unidatahub.common

sealed class AuthStatus<out T>{
    data class Authenticated<T>(val user: T) : AuthStatus<T>()
    object Unauthenticated : AuthStatus<Nothing>()
    object Loading : AuthStatus<Nothing>()
    data class Error(val message: String) : AuthStatus<Nothing>()
    object Empty : AuthStatus<Nothing>()
}