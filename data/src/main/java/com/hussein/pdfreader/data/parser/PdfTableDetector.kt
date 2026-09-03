package com.hussein.pdfreader.data.parser

import javax.inject.Inject

class PdfTableDetector @Inject constructor() {
    // This is a placeholder for actual table detection logic
    // Real implementation would look for grids of text blocks
    fun detect(blocks: List<PdfBlock>): List<PdfBlock> {
        return blocks
    }
}
