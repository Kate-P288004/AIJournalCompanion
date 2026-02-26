package com.kate.aijournalcompanion

object SearchUtils {

    fun searchWithBinaryTree(entries: List<JournalEntry>, emotion: String): List<JournalEntry> {
        val tree = BinaryTree()
        entries.forEach { tree.insert(it) }
        return tree.search(emotion)
    }

    fun searchWithHashMap(entries: List<JournalEntry>, emotion: String): List<JournalEntry> {
        val map = HashMap<String, MutableList<JournalEntry>>()

        entries.forEach { entry ->
            val key = entry.emotion.trim().uppercase()
            if (!map.containsKey(key)) {
                map[key] = mutableListOf()
            }
            map[key]!!.add(entry)
        }

        return map[emotion.trim().uppercase()] ?: emptyList()
    }

    fun searchWithDoublyLinkedList(entries: List<JournalEntry>, emotion: String): List<JournalEntry> {
        val list = DoublyLinkedList<JournalEntry>()
        entries.forEach { list.add(it) }

        val target = emotion.trim().uppercase()
        return list.findAll { it.emotion.trim().uppercase() == target }
    }
}