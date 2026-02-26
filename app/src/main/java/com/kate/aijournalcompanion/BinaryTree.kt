package com.kate.aijournalcompanion

class BinaryTree {

    private data class Node(
        val key: String,
        val entries: MutableList<JournalEntry> = mutableListOf(),
        var left: Node? = null,
        var right: Node? = null
    )

    private var root: Node? = null

    fun insert(entry: JournalEntry) {
        val key = entry.emotion.trim().uppercase()
        root = insertRec(root, key, entry)
    }

    private fun insertRec(current: Node?, key: String, entry: JournalEntry): Node {
        if (current == null) {
            return Node(key, mutableListOf(entry))
        }

        when {
            key < current.key -> current.left = insertRec(current.left, key, entry)
            key > current.key -> current.right = insertRec(current.right, key, entry)
            else -> current.entries.add(entry) // same emotion, store multiple
        }
        return current
    }

    fun search(key: String): List<JournalEntry> {
        val target = key.trim().uppercase()
        var cur = root
        while (cur != null) {
            cur = when {
                target < cur.key -> cur.left
                target > cur.key -> cur.right
                else -> return cur.entries
            }
        }
        return emptyList()
    }
}