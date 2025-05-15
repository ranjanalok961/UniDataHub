package com.assignmentwaala.unidatahub.presentation

import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes(val route: String) {

    @Serializable
    object Splash: Routes(
        route = Splash::class.qualifiedName.toString()
    )

    @Serializable
    object LoginSignup : Routes(
        route = LoginSignup::class.qualifiedName.toString()
    )


    @Serializable
    object ForgotPassword : Routes(
        route = ForgotPassword::class.qualifiedName.toString()
    )

    @Serializable
    object Home : Routes(
        route = Home::class.qualifiedName.toString()
    )

    @Serializable
    object Community : Routes(
        route = Community::class.qualifiedName.toString()
    )

    @Serializable
    object Profile : Routes(
        route = Profile::class.qualifiedName.toString()
    )

    @Serializable
    object AddDocument : Routes(
        route = AddDocument::class.qualifiedName.toString()
    )

    @Serializable
    data class DocumentList(val category: String = "") : Routes(
        route = DocumentList::class.qualifiedName.toString() + "/{route}?category={category}"
    )

    @Serializable
    data class DocumentDetails(
        val title: String = "",
        val description: String? = null,
        val url: String = "",
        val category: String = "",
        val author: String = "",
        val uploadBy: String = "",
//    val thumbnail: String? = null,
        val date: String = ""
    ) : Routes(
        route = DocumentDetails::class.qualifiedName.toString() + "/{route}?title={title}&description={description}&url={url}&category={category}&author={author}&uploadBy={uploadBy}&date={date}"
    )

    @Serializable
    object CreateCommunity: Routes(
        route = CreateCommunity::class.qualifiedName.toString()
    )

    @Serializable
    data class CommunityDetails(
        val communityId: String = "",
        val communityName: String = ""
    ): Routes(
        route = CommunityDetails::class.qualifiedName.toString() + "/{route}?communityId={communityId}&communityName={communityName}"
    )

}