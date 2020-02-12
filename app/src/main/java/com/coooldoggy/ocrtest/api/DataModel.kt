package com.coooldoggy.ocrtest.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

object DataModel {
    data class boxResult(@SerializedName("result") val result : boxes)
    data class boxes( @SerializedName("boxes") val boxes : JsonArray)
    data class ocrResult(var result: JsonObject, var recognition_words: JsonArray)
}