package com.assignmentwaala.unidatahub.presentation.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignmentwaala.unidatahub.MainActivity
import com.assignmentwaala.unidatahub.R

@Composable
fun PDFPicker(
    modifier: Modifier, onDocumentSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    var selectedPdfUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedPdfName by remember { mutableStateOf<String?>(null) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            try {
                selectedPdfUri = uri
                selectedPdfName = uri?.let { getFileNameFromUri(context, it) }
                if (uri != null) {
                    onDocumentSelected(uri)
                }
            } catch (e: Exception) {
                Log.e(MainActivity.TAG, "PDFPicker: ${e.message}")
            }
        }
    )


    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3D5AF1),
                            Color(0xFF84A1FF)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(selectedPdfUri == null) {
                    if (selectedPdfUri == null) {
                        pdfPickerLauncher.launch(arrayOf("application/pdf"))
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            if (selectedPdfUri == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Button(
                        modifier = Modifier.background(color = Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.White
                        ),
                        onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) }
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_upload),
                            contentDescription = "Select PDF",
                            modifier = Modifier.size(100.dp),
                            colorFilter = ColorFilter.tint(Color.Gray.copy(alpha = 0.8f))
                        )
                    }
                    Text(
                        text = "Select a PDF",
                        fontSize = 22.sp,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            imageVector = Icons.Default.Close,
                            contentDescription = "De-select PDF",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    selectedPdfUri = null
                                    selectedPdfName = null
                                }
                                .padding(12.dp)

                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = "Selected: " + selectedPdfName?.toString(),
                            fontSize = 18.sp,
                            color = Color.Gray.copy(alpha = 1f)
                        )
                    }

                }
            }

        }
    }

}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }
    return fileName
}