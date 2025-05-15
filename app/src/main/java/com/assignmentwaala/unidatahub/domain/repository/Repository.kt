package com.assignmentwaala.unidatahub.domain.repository

import com.assignmentwaala.unidatahub.common.AuthStatus
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.CategoryModel
import com.assignmentwaala.unidatahub.domain.models.CommunityModel
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getDocuments(category: String): Flow<ResultState<List<DocumentModel>>>
    fun addDocument(document: DocumentModel) : Flow<ResultState<DocumentModel>>
    fun getCategories(): Flow<ResultState<List<CategoryModel>>>
    fun getUserDocuments(userId: String): Flow<ResultState<List<DocumentModel>>>

    fun login(email: String, password: String): Flow<AuthStatus<UserModel>>
    fun signup(username: String, email: String, password: String, role: String): Flow<AuthStatus<UserModel>>
    fun logout(): Flow<AuthStatus<UserModel>>
    fun getCurrentUser(): Flow<AuthStatus<UserModel>>
    fun changePassword(currentPassword: String, newPassword: String): Flow<ResultState<Boolean>>

    fun createCommunity(name: String, description: String): Flow<ResultState<CommunityModel>>
    fun getCommunities(): Flow<ResultState<List<CommunityModel>>>
    fun deleteCommunity(communityId: String): Flow<ResultState<CommunityModel>>
}