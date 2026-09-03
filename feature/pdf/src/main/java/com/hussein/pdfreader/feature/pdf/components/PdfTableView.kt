package com.hussein.pdfreader.feature.pdf.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.pdfreader.domain.model.PdfTableRow

@Composable
fun PdfTableView(rows: List<PdfTableRow>) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(vertical = 8.dp)
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row {
                row.cells.forEach { cell ->
                    Text(
                        text = cell.text,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = if (rowIndex == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
