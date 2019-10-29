package com.extempo.opticalcharacterrecognizer.model

class Result(private var confidence: Float, private var character: String) {
    fun getConfidence(): Float = confidence
    fun getCharacter(): String = character
}