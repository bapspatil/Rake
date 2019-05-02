package com.bapspatil.rake.model

import com.google.gson.annotations.SerializedName

data class RisOutputModel(
        @SerializedName("buy_link")
        var buyLink: List<String?>?,
        @SerializedName("images")
        var images: List<String?>?,
        @SerializedName("links")
        var links: List<String?>?,
        @SerializedName("shop")
        var shop: List<String?>?,
        @SerializedName("sources")
        var sources: List<String?>?,
        @SerializedName("titles")
        var titles: List<String?>?
)