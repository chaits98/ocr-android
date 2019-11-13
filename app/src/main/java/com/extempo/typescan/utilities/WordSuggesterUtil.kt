package com.extempo.typescan.utilities

object WordSuggesterUtil {
    fun generatePermutations(
        lists: List<List<String>>,
        result: MutableList<String>,
        depth: Int,
        current: String
    ) {
        if (depth == lists.size) {
            result.add(current)
            println("log tag: opermutatun: " + current)
            return
        }
        for (i in 0 until lists[depth].size) {
            generatePermutations(lists, result, depth + 1, current + lists[depth][i])
        }
    }
}