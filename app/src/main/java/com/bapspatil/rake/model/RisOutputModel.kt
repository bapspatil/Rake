package com.bapspatil.rake.model

import com.google.gson.annotations.SerializedName

data class RisOutputModel(
        @SerializedName("best_guess")
        var bestGuess: String?,
        @SerializedName("descriptions")
        var descriptions: ArrayList<String?>?,
        @SerializedName("links")
        var links: ArrayList<String?>?,
        @SerializedName("shop")
        var shop: ArrayList<String?>?,
        @SerializedName("similar_images")
        var similarImages: ArrayList<String?>?,
        @SerializedName("sources")
        var sources: ArrayList<String?>?,
        @SerializedName("titles")
        var titles: ArrayList<String?>?
)