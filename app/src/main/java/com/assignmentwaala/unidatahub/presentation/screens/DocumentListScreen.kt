package com.assignmentwaala.unidatahub.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.assignmentwaala.unidatahub.common.CATEGORY_LIST
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.presentation.components.DocumentItem
import com.assignmentwaala.unidatahub.presentation.viewmodel.DocumentViewModel


@Composable
fun DocumentList(
    documents: List<DocumentModel>,
    onDocumentClick: (DocumentModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(documents.size) { index ->
            DocumentItem(
                document = documents[index],
                onClick = { onDocumentClick(documents[index]) },
                index = index,
                modifier = modifier
            )

        }
    }
}

// Example usage
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//@Preview(showBackground = true)
fun DocumentListScreen(
    category: String,
    onClickItem: (DocumentModel) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: DocumentViewModel = hiltViewModel()
    val documentsState by viewModel.documents.collectAsState()
    var documents by remember { mutableStateOf(emptyList<DocumentModel>()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        if(CATEGORY_LIST.contains(category)) {
            viewModel.getDocuments(category)
        }
        else {
            // Fetch documents for the selected category
            viewModel.getUserDocuments(category)
        }

    }

    LaunchedEffect(documentsState) {
        when(documentsState) {
            is ResultState.Success -> {
                documents = (documentsState as ResultState.Success<List<DocumentModel>>).data
                loading = false
            }
            is ResultState.Loading -> {
                // Handle loading state
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
                        text = if(CATEGORY_LIST.contains(category)) category else "My Documents",
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

    ) { innerPadding->
        Surface(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DocumentList(
                documents = documents,
                onDocumentClick = onClickItem,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}