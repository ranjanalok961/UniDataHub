package com.assignmentwaala.unidatahub.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.common.AuthStatus
import com.assignmentwaala.unidatahub.common.CATEGORY_LIST
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.presentation.components.CustomTextField
import com.assignmentwaala.unidatahub.presentation.components.DropdownTextField
import com.assignmentwaala.unidatahub.presentation.components.ModalCard
import com.assignmentwaala.unidatahub.presentation.components.PDFPicker
import com.assignmentwaala.unidatahub.presentation.viewmodel.AuthViewModel
import com.assignmentwaala.unidatahub.presentation.viewmodel.DocumentViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val documentViewModel: DocumentViewModel = hiltViewModel()
    val uploadState by documentViewModel.uploadState.collectAsState()
    var loading by rememberSaveable { mutableStateOf(false) }
    var uploadProgress by rememberSaveable { mutableStateOf(0) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var documentUri by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var author by rememberSaveable { mutableStateOf("") }
    var submitButtonEnable by rememberSaveable { mutableStateOf(true) }

    val scrollState = rememberScrollState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Loading indicator overlay
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is ResultState.Loading -> {
                loading = true
            }

            is ResultState.Uploading -> {
                loading = true
                var bytes = (uploadState as ResultState.Uploading).bytes
                var total = (uploadState as ResultState.Uploading).totalBytes
                uploadProgress = Math.ceil(((bytes.toFloat() / total) * 100).toDouble()).toInt()

            }
            is ResultState.Success -> {
                loading = false
                Toast.makeText(context, "Document uploaded successfully", Toast.LENGTH_SHORT).show()
                submitButtonEnable=true
                onBack()
            }
            else -> {
                loading = false
                submitButtonEnable=true
            }
        }
    }


    fun handleSubmit() {
        if (title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty() && documentUri != Uri.EMPTY) {
            val document = DocumentModel(
                title = title,
                description = description,
                category = category,
                url = documentUri.toString(),
                date = SimpleDateFormat("dd/MM/yyyy HH:mm").format(System.currentTimeMillis()),
                author = author,
                uploadBy = currentUser?.name ?: "",
                ownerId = currentUser?.uid ?: ""
            )
            submitButtonEnable = false
            documentViewModel.uploadDocument(document)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Document",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3D5AF1),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    PDFPicker(
                        onDocumentSelected = { document ->
                            documentUri = document
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = title,
                        label = "Title",
                        onValueChange = { title = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = description,
                        label = "Description",
                        onValueChange = { description = it },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp, max = 200.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DropdownTextField(
                        label = "Category",
                        selectedOption = category,
                        options = CATEGORY_LIST,
                        onOptionSelected = { category = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = author,
                        label = "Author",
                        onValueChange = { author = it },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        enabled = submitButtonEnable,
                        onClick = {
                           handleSubmit()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3D5AF1),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Submit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }



            if(loading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {  },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier,
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF3D5AF1),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Uploading Document ${uploadProgress}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                }

            }



        }
    }
}