package com.hussein.pdfreader
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var hasPermission by remember {
                mutableStateOf(
                    // On Android 13+, we rely on the Picker, so we can default to true
                    // On older versions, we check the actual storage permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
                    else ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            if (hasPermission) {
                // The actual PDF App Logic
                PdfAppScreen(intent?.data)
            } else {
                // Permission Request Screen
                PermissionRequestScreen(onPermissionGranted = { hasPermission = true })
            }

            // Check if app was opened via a PDF file intent
            val intentUri = intent?.data
            var selectedUri by remember { mutableStateOf(intentUri) }

            val pickerLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri ->
                selectedUri = uri
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("PDF Reader") },
                        actions = {
                            Button(onClick = { pickerLauncher.launch("application/pdf") }) {
                                Text("Open")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    selectedUri?.let {
                        PdfViewer(it, this@MainActivity)
                    } ?: CenterText("Please select a PDF file")
                }
            }
        }
    }
}

@Composable
fun CenterText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
@Composable
fun PermissionRequestScreen(onPermissionGranted: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onPermissionGranted()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("This app needs storage access to read PDFs")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }) {
                Text("Grant Permission")
            }
        }
    }
}