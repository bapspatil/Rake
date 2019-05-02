package com.bapspatil.rake.model

import com.google.gson.annotations.SerializedName

data class RisInputModel(
        @SerializedName("q")
        var query: String?
)