package com.assignmentwaala.unidatahub.presentation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

class CustomNavType<T : Parcelable> (
    private val clazz: KClass<T>,
    private val serializer: KSerializer<T>
): NavType<T>(false) {
    override fun get(bundle: Bundle, key: String): T? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, clazz.java)
        } else {
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): T {
        return Json.decodeFromString(serializer, value)
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(Json.encodeToString(serializer, value))
    }

}

val DocumentModelNavType = object: NavType<DocumentModel>(false) {
    override fun get(bundle: Bundle, key: String): DocumentModel? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, DocumentModel::class.java)
        } else {
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): DocumentModel {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: DocumentModel) {
        bundle.putParcelable(key, value)
    }

    override fun serializeAsValue(value: DocumentModel): String {
        return Json.encodeToString(value)
    }

}