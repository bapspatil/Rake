package com.bapspatil.rake.model

data class BlockOfText(
        val text: String,
        val confidence: Float,
        var linesOfText: List<LineOfText>
)