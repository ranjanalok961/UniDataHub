package com.assignmentwaala.unidatahub.domain.di

import android.app.Application
import android.util.Log
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.data.repository.RepositoryImpl
import com.assignmentwaala.unidatahub.domain.PrefDataStore
import com.assignmentwaala.unidatahub.domain.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideRepository(application: Application, firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth): Repository {
        Log.d(TAG, "Domain Module")
        return RepositoryImpl(application,firestore, firebaseAuth)
    }

    @Singleton
    @Provides
    fun providePrefDataStore(application: Application): PrefDataStore {
        return PrefDataStore(application)
    }
}