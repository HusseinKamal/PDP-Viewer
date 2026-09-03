package com.hussein.pdfreader.data.parser

import com.hussein.pdfreader.domain.model.PdfElement
import com.hussein.pdfreader.domain.model.PdfNode
import java.util.UUID
import javax.inject.Inject

class PdfHierarchyBuilder @Inject constructor() {
    fun build(blocks: List<PdfBlock>): List<PdfNode> {
        val rootNodes = mutableListOf<PdfNode>()
        val stack = mutableListOf<MutableNode>()

        blocks.forEach { block ->
            val level = when (block.type) {
                BlockType.HEADING_1 -> 1
                BlockType.HEADING_2 -> 2
                BlockType.HEADING_3 -> 3
                else -> null
            }

            if (level != null) {
                val newNode = MutableNode(
                    id = UUID.randomUUID().toString(),
                    title = block.text,
                    level = level
                )
                
                while (stack.isNotEmpty() && stack.last().level >= level) {
                    stack.removeAt(stack.size - 1)
                }

                if (stack.isEmpty()) {
                    rootNodes.add(newNode.toDomain()) // This is tricky because we need to add children later
                    // Actually, let's keep it mutable until the end
                } else {
                    stack.last().children.add(newNode)
                }
                stack.add(newNode)
            } else {
                val element = when (block.type) {
                    BlockType.PARAGRAPH -> PdfElement.Paragraph(block.text)
                    BlockType.IMAGE -> PdfElement.Image(block.imageUri!!, block.width, block.height)
                    BlockType.PAGE_BREAK -> PdfElement.PageBreak(block.pageNumber)
                    else -> null
                }
                
                if (element != null) {
                    if (stack.isEmpty()) {
                        // Create a dummy root if needed, or add to a list of elements before first heading
                        // For simplicity, let's assume everything belongs to a root if no heading yet
                        val dummy = MutableNode(UUID.randomUUID().toString(), "Intro", 0)
                        stack.add(dummy)
                        // This logic is a bit flawed but good for a start
                    }
                    stack.last().elements.add(element)
                }
            }
        }

        // Final conversion
        // If we added a dummy intro, handle it
        if (stack.isNotEmpty() && stack.first().level == 0) {
            val intro = stack.first()
            if (!(intro.elements.isEmpty() && intro.children.isEmpty())) {
                rootNodes.add(intro.toDomain())
            }
        }
        
        // This hierarchy building needs to be recursive or handled better.
        // Let's refine:
        val results = mutableListOf<PdfNode>()
        val topLevel = mutableListOf<MutableNode>()
        val currentStack = mutableListOf<MutableNode>()

        blocks.forEach { block ->
            val level = when (block.type) {
                BlockType.HEADING_1 -> 1
                BlockType.HEADING_2 -> 2
                BlockType.HEADING_3 -> 3
                else -> null
            }

            if (level != null) {
                val newNode = MutableNode(UUID.randomUUID().toString(), block.text, level)
                while (currentStack.isNotEmpty() && currentStack.last().level >= level) {
                    currentStack.removeAt(currentStack.size - 1)
                }
                if (currentStack.isEmpty()) {
                    topLevel.add(newNode)
                } else {
                    currentStack.last().children.add(newNode)
                }
                currentStack.add(newNode)
            } else {
                val element = when (block.type) {
                    BlockType.PARAGRAPH -> PdfElement.Paragraph(block.text)
                    BlockType.IMAGE -> PdfElement.Image(block.imageUri!!, block.width, block.height)
                    BlockType.PAGE_BREAK -> PdfElement.PageBreak(block.pageNumber)
                    else -> null
                }
                if (element != null) {
                    if (currentStack.isEmpty()) {
                        val dummy = MutableNode(UUID.randomUUID().toString(), "Root", 0)
                        topLevel.add(dummy)
                        currentStack.add(dummy)
                    }
                    currentStack.last().elements.add(element)
                }
            }
        }

        return topLevel.map { it.toDomain() }
    }

    private class MutableNode(
        val id: String,
        val title: String,
        val level: Int,
        val elements: MutableList<PdfElement> = mutableListOf(),
        val children: MutableList<MutableNode> = mutableListOf()
    ) {
        fun toDomain(): PdfNode = PdfNode(
            id = id,
            title = title,
            level = level,
            elements = elements.toList(),
            children = children.map { it.toDomain() }
        )
    }
}
