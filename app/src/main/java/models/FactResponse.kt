package com.example.funfactgenerator.models

import com.google.gson.annotations.SerializedName

data class FactResponse(
    @SerializedName("text")
    val fact: String
)
