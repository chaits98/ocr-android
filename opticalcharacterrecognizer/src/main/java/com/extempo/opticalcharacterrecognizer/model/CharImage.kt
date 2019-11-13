package com.extempo.opticalcharacterrecognizer.model

import org.opencv.core.Mat

class CharImage(
    val character: String,
    val confidence: Float,
    val mat: Mat
) {
    companion object {
        private val mapper = hashMapOf(
            "0" to arrayListOf("0", "O", "D", "a"),
            "1" to arrayListOf("1", "L", "I", "J"),
            "2" to arrayListOf("Z"),
            "3" to arrayListOf("3"),
            "4" to arrayListOf("4", "Y", "r"),
            "5" to arrayListOf("5", "S"),
            "6" to arrayListOf("6", "G"),
            "7" to arrayListOf("7", "I", "L"),
            "8" to arrayListOf("8"),
            "9" to arrayListOf("9"),
            "a" to arrayListOf("a", "D", "O", "Q", "q"),
            "b" to arrayListOf("b", "b", "f"),
            "d" to arrayListOf("d"),
            "e" to arrayListOf("e"),
            "f" to arrayListOf("f", "b"),
            "g" to arrayListOf("g"),
            "h" to arrayListOf("h", "n"),
            "n" to arrayListOf("n", "h", "r"),
            "q" to arrayListOf("q", "a"),
            "r" to arrayListOf("r", "M", "n", "Y", "H", "4"),
            "t" to arrayListOf("t", "E", "K"),
            "A" to arrayListOf("A"),
            "B" to arrayListOf("B"),
            "C" to arrayListOf("C"),
            "D" to arrayListOf("D", "O"),
            "E" to arrayListOf("E", "t"),
            "F" to arrayListOf("F"),
            "G" to arrayListOf("G"),
            "H" to arrayListOf("H", "M", "r"),
            "I" to arrayListOf("I", "L", "J"),
            "J" to arrayListOf("J", "I"),
            "K" to arrayListOf("K"),
            "L" to arrayListOf("L"),
            "M" to arrayListOf("M", "H"),
            "N" to arrayListOf("N"),
            "O" to arrayListOf("O", "D"),
            "P" to arrayListOf("P"),
            "Q" to arrayListOf("Q", "a"),
            "R" to arrayListOf("R"),
            "S" to arrayListOf("S"),
            "T" to arrayListOf("T"),
            "U" to arrayListOf("U"),
            "V" to arrayListOf("V"),
            "W" to arrayListOf("W"),
            "X" to arrayListOf("X"),
            "Y" to arrayListOf("Y", "r"),
            "Z" to arrayListOf("Z")
        )
        fun getInterchangeableCharacterList(str: String): ArrayList<String> {
            return if(mapper[str] != null) mapper[str]!! else arrayListOf()
        }
    }
}