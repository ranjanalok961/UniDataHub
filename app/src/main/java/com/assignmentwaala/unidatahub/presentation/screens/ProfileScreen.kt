package com.assignmentwaala.unidatahub.presentation.screens

import android.provider.ContactsContract.Profile
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.assignmentwaala.unidatahub.common.AuthStatus
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.presentation.components.PasswordResetModal
import com.assignmentwaala.unidatahub.presentation.viewmodel.AuthViewModel
import com.assignmentwaala.unidatahub.ui.theme.primaryBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit,
    onViewDocuments: () -> Unit
) {

    val authStatus by authViewModel.authStatus.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    var changePasswordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        when(authStatus) {
            is AuthStatus.Authenticated -> {
                username = (authStatus as AuthStatus.Authenticated).user.name
                email = (authStatus as AuthStatus.Authenticated).user.email
                role = (authStatus as AuthStatus.Authenticated).user.role
            }
            else -> {

            }
        }
    }

    fun handlePasswordChange(currentPassword: String,newPassword: String) {
        scope.launch {
            authViewModel.changePassword(currentPassword, newPassword).collect {
               when(it) {
                   is ResultState.Success -> {
                       Toast.makeText(context, "Password reset successfully", Toast.LENGTH_SHORT).show()
                       changePasswordDialog = false
                   }
                   is ResultState.Loading -> {

                   }
                   is ResultState.Error -> {
                       Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                       changePasswordDialog = false
                   }
                   else -> {

                   }
               }

            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3D5AF1),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if(isAuthenticated) {

            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .padding(24.dp)
                                .size(72.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Info Section
                    UserInfoSection(username, email, role)

                    Spacer(modifier = Modifier.height(32.dp))

                    TextButton(
                        onClick = onViewDocuments,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("View My Documents", fontSize = 16.sp)
                    }

                    HorizontalDivider()

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { changePasswordDialog = !changePasswordDialog }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Change Password")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Change Password", fontSize = 16.sp)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLogout() }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout", fontSize = 16.sp, color = Color.Red)
                        }
                    }
                }

                if(changePasswordDialog) {
                    PasswordResetModal(
                        showDialog = changePasswordDialog,
                        onDismiss = {
                            changePasswordDialog = false
                        },
                        onPasswordReset = ::handlePasswordChange
                    )
                }
            }

        }
        else {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You are not logged in",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = onLoginClick,
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = primaryBlue
//                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Login")
                    }
                }
            }
        }

    }

}


@Composable
fun UserInfoSection(
    username: String,
    email: String,
    role: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = role,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp
            )
        }
    }
}
