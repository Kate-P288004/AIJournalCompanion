package com.kate.aijournalcompanion

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * File: SearchUtils.kt
 *
 * Purpose:
 * - Demonstrates searching using multiple data structures
 * - Returns all journal entries that match a target emotion
 *
 * Assessment Concepts Demonstrated:
 * - Searching using:
 *   1) Binary Search Tree (BST)
 *   2) HashMap (key to list of entries)
 *   3) Doubly Linked List (linear traversal)
 * =========================================================
 */
object SearchUtils {

    // ---------------------------------------------------------
    // Helper: normalize emotion input so comparisons are consistent
    // ---------------------------------------------------------
    private fun normalizeEmotion(raw: String): String {
        return raw.trim()
            .uppercase()
            .replace("-", " ")
            .replace("_", " ")
    }

    // =========================================================
    // Search Method 1: Binary Tree (BST)
    //
    // Assessment: Data Structure Search
    // - Build a BinaryTree of journal entries keyed by emotion
    // - Search for matching emotion
    // - Returns list of entries under that emotion node
    // =========================================================
    fun searchWithBinaryTree(entries: List<JournalEntry>, emotion: String): List<JournalEntry> {
        val target = normalizeEmotion(emotion)
        if (target.isBlank()) return emptyList()

        val tree = BinaryTree()
        entries.forEach { tree.insert(it) }

        return tree.search(target)
    }

    // =========================================================
    // Search Method 2: HashMap
    //
    // Assessment: Data Structure Search
    // - Build HashMap<Emotion, MutableList<JournalEntry>>
    // - Lookup is O(1) average case
    // =========================================================
    fun searchWithHashMap(entries: List<JournalEntry>, emotion: String): List<JournalEntry> {
        val target = normalizeEmotion(emotion)
        if (target.isBlank()) return emptyList()

        val map = HashMap<String, MutableList<JournalEntry>>()

        entries.forEach { entry ->
            val key = normalizeEmotion(entry.emotion)
            val bucket = map.getOrPut(key) { mutableListOf() }
            bucket.add(entry)
        }

        return map[target] ?: emptyList()
    }

    // =========================================================
    // Search Method 3: Doubly Linked List
    //
    // Assessment: Data Structure Search
    // - Insert all entries into a DoublyLinkedList
    // - Traverse and collect matches (linear search)
    // =========================================================
    fun searchWithDoublyLinkedList(entries: List<JournalEntry>, emotion: String): List<JournalEntry> {
        val target = normalizeEmotion(emotion)
        if (target.isBlank()) return emptyList()

        val list = DoublyLinkedList<JournalEntry>()
        entries.forEach { list.add(it) }

        return list.findAll { normalizeEmotion(it.emotion) == target }
    }
}