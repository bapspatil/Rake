package com.bapspatil.rake.model

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/
//
data class VisionText(
    val text: String,
    var blocksOfText: ArrayList<BlockOfText> = ArrayList()
)