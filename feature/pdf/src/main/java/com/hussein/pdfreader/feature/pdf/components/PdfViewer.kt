package com.hussein.pdfreader.feature.pdf.components

import android.net.Uri
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.pdf.viewer.fragment.PdfViewerFragment

@Composable
fun PdfViewer(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager ?: return

    val containerId = remember { View.generateViewId() }

    Box(modifier = modifier) {
        val isSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                android.os.ext.SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }

        if (isSupported) {
            AndroidView(
                factory = { ctx ->
                    FragmentContainerView(ctx).apply {
                        id = containerId
                    }
                },
                update = { view ->
                    val fragment = fragmentManager.findFragmentByTag("pdf_viewer") as? PdfViewerFragment
                        ?: PdfViewerFragment().also {
                            fragmentManager.beginTransaction()
                                .replace(view.id, it, "pdf_viewer")
                                .commit()
                        }
                    if (fragment.documentUri != uri) {
                        fragment.documentUri = uri
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "PDF Viewing requires Android 12+ (SDK Ext 13).",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
