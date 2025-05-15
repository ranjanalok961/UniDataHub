package com.assignmentwaala.unidatahub.data.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.common.AuthStatus
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.CategoryModel
import com.assignmentwaala.unidatahub.domain.models.CommunityModel
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.domain.models.UserModel
import com.assignmentwaala.unidatahub.domain.repository.Repository
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.rpc.context.AttributeContext.Auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val context: Application, val firestore: FirebaseFirestore, val firebaseAuth: FirebaseAuth): Repository {

    override fun getDocuments(category: String): Flow<ResultState<List<DocumentModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val documents = firestore.collection("documents")
                .whereEqualTo("category", category)
                .get()
                .await()
                .toObjects(DocumentModel::class.java)
            trySend(ResultState.Success(documents))
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching documents: ${e.message}")
            trySend(ResultState.Error("Firestore error: ${e.message}"))
        }
        awaitClose { close() }
    }

    override fun addDocument(document: DocumentModel): Flow<ResultState<DocumentModel>> = callbackFlow {
        // Upload To Cloudinary then save metadata to Firestore
        trySend(ResultState.Loading)
        try {
            Log.d(TAG, "Document Repository document: $document")
            val filePath = getFilePathFromUri(context, Uri.parse(document.url)) ?: return@callbackFlow

            // Upload To Cloudinary
            MediaManager.get()
                .upload(filePath)
                .option("folder","UniDataHub/Documents")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d(TAG, "Upload started" )
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d(TAG, "Upload in progress")
                        trySend(ResultState.Uploading(bytes, totalBytes))
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?
                    ) {
                        Log.d(TAG, "Upload success")
                        Log.d(TAG, "Request Id: $requestId")
                        Log.d(TAG, "Result Data: $resultData")

                        val newDoc = document.copy(url = resultData?.get("url").toString())

                        // Save metadata to Firestore
                        saveMetadataToFirestore(newDoc) { success, message ->
                            if (success) {
                                trySend(ResultState.Success(newDoc))
                            } else {
                                deleteFromCloudinary(resultData?.get("public_id").toString())
                                trySend(ResultState.Error("$message"))
                            }
                        }

                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.d(TAG, "Upload failed")
                        trySend(ResultState.Error("Upload failed: $error"))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.d(TAG, "Upload rescheduled")
                    }

                }).dispatch()

        } catch (e: Exception) {
            Log.e("Exception", "Error uploading PDF: ${e.message}")
            trySend(ResultState.Error("Error: ${e.localizedMessage}" ))
        }

        awaitClose { close() }
    }

    override fun getCategories(): Flow<ResultState<List<CategoryModel>>> = callbackFlow {
        try {
            // Create a list to store results
            val categoryCountList = mutableListOf<CategoryModel>()

            // Get all distinct categories and their counts in one go
            val querySnapshot = firestore.collection("documents")
                .get()
                .await()

            // Group by category and count
            val categoryMap = mutableMapOf<String, Int>()

            for (document in querySnapshot.documents) {
                val category = document.getString("category") ?: continue
                categoryMap[category] = (categoryMap[category] ?: 0) + 1
            }

            // Convert map to list
            for ((category, count) in categoryMap) {
                categoryCountList.add(CategoryModel(category, count))
            }

            trySend(ResultState.Success(categoryCountList))

        } catch (e: Exception) {
            println("Error getting categories: ${e.message}")
            trySend(ResultState.Error("Error getting categories: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun getUserDocuments(userId: String): Flow<ResultState<List<DocumentModel>>> = callbackFlow {
        try {
            trySend(ResultState.Loading)

            val documents = firestore.collection("documents")
                .whereEqualTo("ownerId", userId)
                .get()
                .await()
                .toObjects(DocumentModel::class.java)

            trySend(ResultState.Success(documents))
        } catch (e: Exception) {
            trySend(ResultState.Error("Error getting user documents: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun login(email: String, password: String): Flow<AuthStatus<UserModel>> = callbackFlow {
        trySend(AuthStatus.Loading)
        try{
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user

                        user?.let {
                            firestore.collection("users").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val username = document.getString("username") ?: ""
                                        val role = document.getString("role") ?: ""
                                        val uid = document.getString("uid") ?: ""

                                        trySend(AuthStatus.Authenticated(UserModel(name = username, email = email, uid = uid, role = role)))
                                    } else {
                                        trySend(AuthStatus.Unauthenticated)
                                    }
                                }
                                .addOnFailureListener {
                                    trySend(AuthStatus.Error(" Error getting user: ${it.message}"))
                                }
                        }
                    }
                }.addOnFailureListener {
                    trySend(AuthStatus.Error("Error: ${it.message}"))
                }

        } catch (e: Exception) {
            Log.d(TAG, "Login Exception: ${e.message}")
            trySend(AuthStatus.Error("Error: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun signup(
        username: String,
        email: String,
        password: String,
        role: String
    ): Flow<AuthStatus<UserModel>> = callbackFlow{
        trySend(AuthStatus.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            // Update display name in Firebase Authentication
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                            it.updateProfile(profileUpdates)

                            // Store user details in Firestore
                            val userMap = hashMapOf(
                                "uid" to it.uid,
                                "username" to username,
                                "email" to email,
                                "role" to role
                            )

                            firestore.collection("users").document(it.uid).set(userMap)
                                .addOnSuccessListener {
                                    Log.d(TAG, "User added successfully")
                                    trySend(AuthStatus.Authenticated(UserModel(name = username, email = email, uid = user.uid, role = role)))
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error adding user", e)
                                    trySend(AuthStatus.Error("Error adding user due to : ${e.message}"))
                                }
                        }
                    } else {
                        Log.e(TAG, "Sign-up failed", task.exception)
                        trySend(AuthStatus.Error("Sign-up failed: ${task.exception?.message}"))
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Sign-up failed", it)
                    trySend(AuthStatus.Error("Sign-up failed: ${it.message}"))
                }
        } catch (e: Exception) {
            Log.d(TAG, "SignUp Exception: ${e.message}")
            trySend(AuthStatus.Error("Error: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun logout(): Flow<AuthStatus<UserModel>> = callbackFlow {
        trySend(AuthStatus.Loading)
        try {
            firebaseAuth.signOut()
            trySend(AuthStatus.Unauthenticated)
        } catch (e: Exception) {
            Log.d(TAG, "Logout Exception: ${e.message}")
            trySend(AuthStatus.Error("Error: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun getCurrentUser(): Flow<AuthStatus<UserModel>> = callbackFlow {
        trySend(AuthStatus.Loading)
        try{
            val user = firebaseAuth.currentUser

            if(user != null) {
                firestore.collection("users").document(user.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val username = document.getString("username") ?: ""
                            val email = document.getString("email") ?: ""
                            val role = document.getString("role") ?: ""
                            val uid = document.getString("uid") ?: ""

                            trySend(AuthStatus.Authenticated(UserModel(name = username, email = email, uid = uid, role = role)))
                        } else {
                            trySend(AuthStatus.Unauthenticated)
                        }
                    }
                    .addOnFailureListener {
                        trySend(AuthStatus.Error(" Error getting user: ${it.message}"))
                    }
            }
            else {
                trySend(AuthStatus.Unauthenticated)
            }
        } catch (e: Exception) {
            Log.d(TAG, "GetCurrentUser Exception: ${e.message}")
            trySend(AuthStatus.Unauthenticated)
        }

        awaitClose{ close() }
    }

    override fun changePassword(currentPassword: String, newPassword: String): Flow<ResultState<Boolean>> = callbackFlow {
        try {
            trySend(ResultState.Loading)
            val currentUser = firebaseAuth.currentUser
            if(currentUser != null) {
                val credentials = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
                currentUser.reauthenticate(credentials)
                    .addOnSuccessListener {
                        currentUser.updatePassword(newPassword)
                            .addOnSuccessListener {
                                trySend(ResultState.Success(true))
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "ChangePassword Repository Error: ${it.message}")
                                trySend(ResultState.Error(it.message.toString()))
                            }
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Error(it.message.toString()))
                        Log.d(TAG, "ChangePassword Repository Error: ${it.message}")
                    }
            }
            else {
                trySend(ResultState.Error("You are not Authenticated"))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            trySend(ResultState.Error("Error: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun createCommunity(
        name: String,
        description: String
    ): Flow<ResultState<CommunityModel>> = callbackFlow {
        try {
            trySend(ResultState.Loading)
            val user = firebaseAuth.currentUser
            if(user == null) {
                trySend(ResultState.Error("You are not Authenticated"))
                return@callbackFlow
            }
            val community = CommunityModel(id = UUID.randomUUID().toString(), name = name, description = description, createdBy = user.uid)

            firestore.collection("communities")
                .document(community.id).set(community)
                .addOnSuccessListener {
                    trySend(ResultState.Success(community))
                }
                .addOnFailureListener {
                    Log.e("FirestoreError", "Error creating community: ${it.message}")
                    trySend(ResultState.Error("${it.message}"))
                }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message.toString()))
            Log.e(TAG+"FirestoreError", "Error creating community: ${e.message}")
        }

        awaitClose { close() }
    }

    override fun getCommunities(): Flow<ResultState<List<CommunityModel>>> = callbackFlow {
        try {
            trySend(ResultState.Loading)
            val communities = firestore.collection("communities")
                .get()
                .await()
                .toObjects(CommunityModel::class.java)

            trySend(ResultState.Success(communities))

        } catch (e: Exception) {
            Log.e(TAG, "Error getting communities: ${e.message}")
            trySend(ResultState.Error("Error getting communities: ${e.message}"))
        }

        awaitClose { close() }
    }

    override fun deleteCommunity(communityId: String): Flow<ResultState<CommunityModel>> = callbackFlow{
        try{
            FirebaseDatabase.getInstance()
                .getReference("discussions")
                .child(communityId)
                .removeValue()
                .await()

            firestore.collection("communities")
                .document(communityId)
                .delete()
                .addOnSuccessListener {
                    trySend(ResultState.Success(CommunityModel()))
                }
                .addOnFailureListener {
                    Log.d(TAG, "Error deleting community: ${it.message}")
                    trySend(ResultState.Error("Error deleting community: ${it.message}"))
                }
        }
        catch (e: Exception) {
            Log.d(TAG, "Error deleting community: ${e.message}")
            trySend(ResultState.Error(e.message.toString()))
        }

        awaitClose { close() }

    }

    private fun deleteFromCloudinary(publicId: String) {
        try {
            MediaManager.get().cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
        } catch (e: Exception) {
            Log.e("CloudinaryError", "Error deleting from Cloudinary: ${e.message}")
        }
    }

    private fun saveMetadataToFirestore( document: DocumentModel,onComplete: (Boolean, String?) -> Unit) {
        val metadata = mapOf(
            "title" to document.title,
            "description" to document.description,
            "category" to document.category,
            "url" to document.url,
            "author" to document.author,
            "uploadBy" to document.uploadBy,
            "date" to document.date,
            "ownerId" to document.ownerId
        )

        firestore.collection("documents")
            .add(metadata)
            .addOnSuccessListener {
                onComplete(true, "Upload successful and metadata saved")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error saving metadata: ${e.message}")
                onComplete(false, "Firestore error: ${e.message}")
            }
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val file = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

