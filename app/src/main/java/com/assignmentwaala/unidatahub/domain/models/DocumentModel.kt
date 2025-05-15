package com.assignmentwaala.unidatahub.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DocumentModel(
    val title: String = "",
    val description: String? = null,
    val url: String = "",
    val category: String = "",
    val author: String = "",
    val uploadBy: String = "",
    val ownerId: String = "",
//    val thumbnail: String? = null,
    val date: String = ""
) : Parcelable