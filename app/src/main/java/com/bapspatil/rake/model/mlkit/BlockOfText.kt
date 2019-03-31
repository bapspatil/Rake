package com.bapspatil.rake.model.mlkit

data class BlockOfText(
        val text: String,
        val confidence: Float,
        var linesOfText: ArrayList<LineOfText> = ArrayList()
)