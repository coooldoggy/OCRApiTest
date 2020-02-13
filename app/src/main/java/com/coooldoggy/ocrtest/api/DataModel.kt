package com.coooldoggy.ocrtest.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

object DataModel {
    data class boxResult(@SerializedName("result") val result : boxes)
    data class boxes( @SerializedName("boxes") val boxes : JsonArray)
    data class ocrResult(@SerializedName("result") val result : recoWord)
    data class recoWord(@SerializedName("recognition_words") val recognition_words : JsonArray)
}