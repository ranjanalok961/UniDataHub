package com.assignmentwaala.unidatahub.presentation.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.presentation.Routes
import com.assignmentwaala.unidatahub.presentation.modifier.scrollableModifier
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailsScreen(
    document: Routes.DocumentDetails,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isTopBarVisible = remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()
    val isScrolled = remember { derivedStateOf { scrollState.value > 100 } } // Adjust threshold as needed

    LaunchedEffect(document.url) {
        isLoading = true
        errorMessage = null
        try {
            val file = downloadPdf(context, document.url, "${document.title}.pdf")
            pdfFile = file
        } catch (e: Exception) {
            errorMessage = "Failed to load PDF: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }


    Scaffold(
        topBar = {

            AnimatedVisibility(
                visible = isTopBarVisible.value,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Document Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }
                    },
//                    actions = {
//                        IconButton(onClick = {
//                            scope.launch {
//                                sharePdf(context, pdfFile, document.title)
//                            }
//                        }) {
//                            Icon(Icons.Default.Share, contentDescription = "Share")
//                        }
//
//                        IconButton(onClick = {
//                            scope.launch {
//                                pdfFile?.let { saveToDownloads(context, it) }
//                            }
//                        }) {
//                            Icon(
//                                ImageVector.vectorResource(R.drawable.ic_download),
//                                contentDescription = "Download"
//                            )
//                        }
//                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF3D5AF1),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val file = downloadPdf(context, document.url, "${document.title}.pdf")
                                    pdfFile = file
                                } catch (e: Exception) {
                                    errorMessage = "Failed to load PDF: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }) {
                            Text("Retry")
                        }
                    }
                }
                pdfFile != null -> {
                    PdfViewer(
                        url = document.url
                    )
                }
            }
        }
    }

}

@Composable
fun PdfViewer( url: String) {

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                webViewClient = WebViewClient()

                // load the pdf from url
                loadUrl("http://docs.google.com/gview?embedded=true&url=$url")
            }
        },
        modifier = Modifier.fillMaxSize() // WebView takes full space when Card is hidden
    )
}


@Composable
fun DetailsCard(document: DocumentModel) {
    // Document Metadata
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = document.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            document.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Author",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = document.author,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = document.category,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Uploaded by",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = document.uploadBy,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = formatDate(document.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


// Helper function to download PDF file
suspend fun downloadPdf(context: Context, url: String, fileName: String): File = withContext(Dispatchers.IO) {
    val directory = File(context.cacheDir, "pdfs")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    val file = File(directory, fileName)

    try {
        val connection = URL(url).openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        file
    } catch (e: Exception) {
        throw e
    }
}

// Helper function to share PDF
suspend fun sharePdf(context: Context, file: File?, documentTitle: String) {
    file?.let {
        withContext(Dispatchers.IO) {
            // Use FileProvider for Android 7+
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                it
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, documentTitle)
                type = "application/pdf"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        }
    }
}

// Helper function to save to downloads
suspend fun saveToDownloads(context: Context, file: File) {
    withContext(Dispatchers.IO) {
        // For a real app, you would use the MediaStore APIs for Android 10+ and request WRITE_EXTERNAL_STORAGE
        // permission for older versions
        // This is simplified
        val toast = Toast.makeText(
            context,
            "Download feature would be implemented here",
            Toast.LENGTH_SHORT
        )
        withContext(Dispatchers.Main) {
            toast.show()
        }
    }
}

// Format date string
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}
