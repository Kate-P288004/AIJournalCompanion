package com.kate.aijournalcompanion

object SortUtils {

    // Bubble Sort (by emotion)
    fun bubbleSort(list: MutableList<JournalEntry>) {
        for (i in 0 until list.size - 1) {
            for (j in 0 until list.size - i - 1) {
                if (list[j].emotion > list[j + 1].emotion) {
                    val temp = list[j]
                    list[j] = list[j + 1]
                    list[j + 1] = temp
                }
            }
        }
    }

    // Insertion Sort (by emotion)
    fun insertionSort(list: MutableList<JournalEntry>) {
        for (i in 1 until list.size) {
            val key = list[i]
            var j = i - 1
            while (j >= 0 && list[j].emotion > key.emotion) {
                list[j + 1] = list[j]
                j--
            }
            list[j + 1] = key
        }
    }

    // Selection Sort (by emotion)
    fun selectionSort(list: MutableList<JournalEntry>) {
        for (i in 0 until list.size - 1) {
            var minIndex = i
            for (j in i + 1 until list.size) {
                if (list[j].emotion < list[minIndex].emotion) {
                    minIndex = j
                }
            }
            val temp = list[minIndex]
            list[minIndex] = list[i]
            list[i] = temp
        }
    }
}