package com.assignmentwaala.unidatahub.domain

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrefDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "unitdatahub_preferences")

    companion object {
        private val GUEST_LOGIN_KEY = booleanPreferencesKey("guest_login_key")
    }



    val isGuestLogin: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[GUEST_LOGIN_KEY] ?: false }

    suspend fun setIsGuestLogin(isGuestLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GUEST_LOGIN_KEY] = isGuestLogin
        }
    }
}