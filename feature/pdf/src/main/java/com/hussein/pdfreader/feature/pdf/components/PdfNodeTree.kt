package com.hussein.pdfreader.feature.pdf.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.pdfreader.domain.model.PdfNode

@Composable
fun PdfNodeTree(
    node: PdfNode,
    expandedNodes: Set<String>,
    onToggle: (String) -> Unit
) {
    val isExpanded = expandedNodes.contains(node.id)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(node.id) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = node.title,
                style = when (node.level) {
                    1 -> MaterialTheme.typography.headlineLarge
                    2 -> MaterialTheme.typography.headlineMedium
                    3 -> MaterialTheme.typography.headlineSmall
                    else -> MaterialTheme.typography.titleMedium
                },
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                // Render elements
                node.elements.forEach { element ->
                    PdfElementView(element)
                }

                // Render children
                node.children.forEach { child ->
                    PdfNodeTree(child, expandedNodes, onToggle)
                }
            }
        }
    }
}
