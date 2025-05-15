package com.assignmentwaala.unidatahub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.domain.models.DiscussionModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(): ViewModel() {
    private val _messages = MutableStateFlow<List<DiscussionModel>>(emptyList())
    val messages: StateFlow<List<DiscussionModel>> = _messages.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()


    fun fetchMessages(communityId: String) {
        _loading.value = true
        val db = FirebaseDatabase.getInstance().getReference("discussions").child(communityId)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList =
                    snapshot.children.mapNotNull { it.getValue(DiscussionModel::class.java) }
                _messages.value = messageList
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG + "DiscussionViewModel", "Database error: ${error.message}")
                _loading.value = false
            }
        })
    }

    fun sendMessage(communityId: String, message: String) = viewModelScope.launch {
        val user = Firebase.auth.currentUser ?: return@launch
        val db = FirebaseDatabase.getInstance().getReference("discussions").child(communityId)
        val messageId = db.push().key ?: return@launch

        val discussion =
            DiscussionModel(
                id = messageId,
                communityId = communityId,
                message = message,
                senderId =  user.uid,
                senderName = user.displayName ?: "User"
            )
        db.child(messageId).setValue(discussion).await()
    }

    fun deleteMessage(communityId: String, messageId: String) = viewModelScope.launch {
        val db = FirebaseDatabase.getInstance().getReference("discussions").child(communityId)
        db.child(messageId).removeValue().await()
    }
}

