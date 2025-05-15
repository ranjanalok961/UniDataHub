package com.assignmentwaala.unidatahub.domain.models

data class CommunityModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val createdBy: String = "", // Faculty UID
    val members: List<String> = emptyList() // UIDs of faculty & students
)

data class DiscussionModel(
    val id: String = "",
    val communityId: String = "",
    val message: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
