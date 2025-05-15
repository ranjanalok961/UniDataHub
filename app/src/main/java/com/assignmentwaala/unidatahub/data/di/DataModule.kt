package com.assignmentwaala.unidatahub.data.di

import android.app.Application
import com.assignmentwaala.unidatahub.common.CLOUDINARY_API_KEY
import com.assignmentwaala.unidatahub.common.CLOUDINARY_API_SECRET
import com.assignmentwaala.unidatahub.common.CLOUDINARY_NAME
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Singleton
    @Provides
    fun provideFirestoreDB(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideRealtimeDB(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
}