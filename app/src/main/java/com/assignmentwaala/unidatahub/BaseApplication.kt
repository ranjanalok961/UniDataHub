package com.assignmentwaala.unidatahub

import android.app.Application
import com.assignmentwaala.unidatahub.common.CLOUDINARY_API_KEY
import com.assignmentwaala.unidatahub.common.CLOUDINARY_API_SECRET
import com.assignmentwaala.unidatahub.common.CLOUDINARY_NAME
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        provideCloudinary()
    }


    private fun provideCloudinary() {
        val config : MutableMap<Any?, Any?> = mutableMapOf<Any?, Any?>()
        config["cloud_name"] = CLOUDINARY_NAME
        config["api_key"] = CLOUDINARY_API_KEY
        config["api_secret"] = CLOUDINARY_API_SECRET
        config["secure"] = true
        MediaManager.init(this, config)
    }
}