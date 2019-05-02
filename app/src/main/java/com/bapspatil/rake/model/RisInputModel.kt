package com.bapspatil.rake.model

import com.google.gson.annotations.SerializedName

data class RisInputModel(
        @SerializedName("image_url")
        var imageUrl: String?
)