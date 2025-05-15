package com.assignmentwaala.unidatahub.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignmentwaala.unidatahub.common.ACADEMIC_ROLE
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.DiscussionModel
import com.assignmentwaala.unidatahub.presentation.viewmodel.AuthViewModel
import com.assignmentwaala.unidatahub.presentation.viewmodel.DiscussionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionScreen(
    communityId: String,
    communityName: String,
    discussionViewModel: DiscussionViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val discussions: List<DiscussionModel> by discussionViewModel.messages.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    var message by remember { mutableStateOf("") }
    var showContextMenu by remember { mutableStateOf("") }

    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF0F2F5)
    val bubbleOwnColor = if (isDarkMode) Color(0xFF3D5AF1) else Color(0xFF3D5AF1)
    val bubbleOtherColor = if (isDarkMode) Color(0xFF2A2A2A) else Color.White
    val textColor = if(isDarkMode) Color.White else Color.Black

    LaunchedEffect(Unit) {
        discussionViewModel.fetchMessages(communityId)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = communityName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3D5AF1),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor), // Dynamic background
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    reverseLayout = true // Show latest message at the bottom
                ) {
                    items(discussions.reversed()) { discussion ->
                        val isCurrentUser = discussion.senderId == currentUser?.uid

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box() {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrentUser) bubbleOwnColor else bubbleOtherColor
                                    ),
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .widthIn(min = 80.dp, max = 280.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    if(currentUser?.uid == discussion.senderId) {
                                                        showContextMenu = discussion.id
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        if (!isCurrentUser) {
                                            Text(
                                                text = discussion.senderName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF3D5AF1)
                                            )
                                            Text(
                                                text = discussion.message,
                                                fontSize = 16.sp,
                                                color = textColor
                                            )
                                        }
                                        else {
                                            Text(
                                                text = discussion.message,
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }


                                DropdownMenu(
                                    expanded = showContextMenu == discussion.id,
                                    onDismissRequest = { showContextMenu = "" }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            if(currentUser?.uid == discussion.senderId) {
                                                discussionViewModel.deleteMessage(discussion.communityId, discussion.id)
                                                showContextMenu = ""
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDarkMode) Color(0xFF1E1E1E) else Color.White) // Dynamic input field background
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        enabled = currentUser != null,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        placeholder = { Text("Type a message...", color = if (isDarkMode) Color.Gray else Color.Black) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (message.isNotBlank()) {
                                discussionViewModel.sendMessage(communityId, message)
                                message = ""
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF3D5AF1), shape = CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if(discussionViewModel.loading.collectAsState().value) {
                CircularProgressIndicator()
            }
        }
    }
}
