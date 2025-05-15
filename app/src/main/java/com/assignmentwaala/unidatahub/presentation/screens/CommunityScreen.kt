package com.assignmentwaala.unidatahub.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.common.ACADEMIC_ROLE
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.CommunityModel
import com.assignmentwaala.unidatahub.domain.models.DiscussionModel
import com.assignmentwaala.unidatahub.presentation.Routes
import com.assignmentwaala.unidatahub.presentation.components.CreateCommunityModal
import com.assignmentwaala.unidatahub.presentation.viewmodel.AuthViewModel
import com.assignmentwaala.unidatahub.presentation.viewmodel.CommunityViewModel
import com.assignmentwaala.unidatahub.presentation.viewmodel.DiscussionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    authViewModel: AuthViewModel,
    communityViewModel: CommunityViewModel,
    onCommunityClick: (communityId: String, communityName: String) -> Unit
) {
    val communitiesState by communityViewModel.communities.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val createCommunityState by communityViewModel.createCommunityResult.collectAsState()
    var communities by remember { mutableStateOf(emptyList<CommunityModel>()) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showCreateCommunityModal by remember { mutableStateOf(false) }

    var isFirstCall by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if(isFirstCall) {
            communityViewModel.getCommunities()
            isFirstCall = false
        }
    }

    LaunchedEffect(communitiesState) {
        when(communitiesState) {
            is ResultState.Success -> {
                communities = (communitiesState as ResultState.Success).data
                loading = false
            }

            ResultState.Loading -> {
                loading = true
            }

            else -> {
                loading = false
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Community",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },

                actions = {
                    if(currentUser != null && currentUser!!.role == ACADEMIC_ROLE[1]) {
                        IconButton(onClick = {
                            showCreateCommunityModal = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add community",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3D5AF1),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            CommunityList(
                communities = communities,
                authViewModel = authViewModel,
                communityViewModel = communityViewModel,
                onCommunityClick = onCommunityClick
            )

            if(loading) CircularProgressIndicator()


            if(createCommunityState is ResultState.Loading) {
                CircularProgressIndicator()
            }

            CreateCommunityModal(
                showDialog = showCreateCommunityModal,
                onDismiss = { showCreateCommunityModal = false },
                onCreateCommunity = { communityName, description ->
                    communityViewModel.createCommunity(communityName, description)
                    showCreateCommunityModal = false
                }
            )
        }

    }
}

@Composable
fun CommunityList(
    communities: List<CommunityModel>,
    authViewModel: AuthViewModel,
    communityViewModel: CommunityViewModel,
    onCommunityClick: (communityId: String, communityName: String) -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF0F2F5)
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val descriptionColor = if (isDarkMode) Color(0xFFB0B0B0) else Color.DarkGray

    var showContextMenu by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(communities) { community ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
//                        onCommunityClick(community.id, community.name)
                    }
                    .pointerInput(Unit){
                        detectTapGestures(
                            onLongPress = {
                                if(currentUser?.uid == community.createdBy) {
                                    showContextMenu = community.id
                                }
                            },
                            onTap = {
                                onCommunityClick(community.id, community.name)
                            }
                        )
                    },
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_group),
                        contentDescription = "Community Icon",
                        tint = Color(0xFF3D5AF1),
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE3E7FE), shape = CircleShape)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = community.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = community.description,
                            fontSize = 14.sp,
                            color = descriptionColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                DropdownMenu(
                    expanded = showContextMenu == community.id,
                    onDismissRequest = { showContextMenu = "" }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            if(currentUser?.uid == community.createdBy) {
                                communityViewModel.deleteCommunity(community.id)
                                showContextMenu = ""
                            }
                        }
                    )
                }
            }
        }
    }
}
