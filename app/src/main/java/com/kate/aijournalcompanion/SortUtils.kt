package com.kate.aijournalcompanion

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * File: SortUtils.kt
 *
 * Purpose:
 * - Demonstrates classic sorting algorithms
 * - Sorts journal entries by emotion value
 *
 * Assessment Concepts Demonstrated:
 * - Bubble Sort
 * - Insertion Sort
 * - Selection Sort
 * - Algorithm comparison and implementation
 * =========================================================
 */
object SortUtils {

    // ---------------------------------------------------------
    // Normalize emotion text so sorting is consistent
    // (JOY == joy == Joy)
    // ---------------------------------------------------------
    private fun normalizeEmotion(raw: String): String {
        return raw.trim()
            .uppercase()
            .replace("-", " ")
            .replace("_", " ")
    }

    // =========================================================
    // Bubble Sort
    //
    // Algorithm idea:
    // - Repeatedly compares elements
    // - Swaps if order is wrong
    // - Largest values move toward the end
    // =========================================================
    fun bubbleSort(list: MutableList<JournalEntry>) {

        for (i in 0 until list.size - 1) {

            for (j in 0 until list.size - i - 1) {

                val left = normalizeEmotion(list[j].emotion)
                val right = normalizeEmotion(list[j + 1].emotion)

                if (left > right) {
                    val temp = list[j]
                    list[j] = list[j + 1]
                    list[j + 1] = temp
                }
            }
        }
    }

    // =========================================================
    // Insertion Sort
    //
    // Algorithm idea:
    // - Builds a sorted portion gradually
    // - Inserts current element into correct position
    // =========================================================
    fun insertionSort(list: MutableList<JournalEntry>) {

        for (i in 1 until list.size) {

            val key = list[i]
            val keyEmotion = normalizeEmotion(key.emotion)

            var j = i - 1

            while (
                j >= 0 &&
                normalizeEmotion(list[j].emotion) > keyEmotion
            ) {
                list[j + 1] = list[j]
                j--
            }

            list[j + 1] = key
        }
    }

    // =========================================================
    // Selection Sort
    //
    // Algorithm idea:
    // - Finds smallest element in unsorted area
    // - Swaps it into correct position
    // - Simple but less efficient for large data
    // =========================================================
    fun selectionSort(list: MutableList<JournalEntry>) {

        for (i in 0 until list.size - 1) {

            var minIndex = i

            for (j in i + 1 until list.size) {

                val current = normalizeEmotion(list[j].emotion)
                val min = normalizeEmotion(list[minIndex].emotion)

                if (current < min) {
                    minIndex = j
                }
            }

            if (minIndex != i) {
                val temp = list[minIndex]
                list[minIndex] = list[i]
                list[i] = temp
            }
        }
    }
}