package com.bapspatil.rake.model.mlkit

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/
//
data class VisionText(
    val text: String,
    var blocksOfText: ArrayList<BlockOfText> = ArrayList()
)