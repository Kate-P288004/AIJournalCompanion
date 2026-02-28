package com.kate.aijournalcompanion

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * Purpose:
 * - Custom Binary Search Tree implementation
 * - Used for fast emotion-based searching
 *
 * Assessment Concepts Demonstrated:
 * - Tree data structure
 * - Recursive insertion algorithm
 * - Iterative search algorithm
 * - Grouping multiple values per key
 * =========================================================
 */

class BinaryTree {

    /**
     * ---------------------------------------------------------
     * Node structure
     *
     * key     = emotion name
     * entries = all journal entries with same emotion
     * left    = smaller keys
     * right   = greater keys
     * ---------------------------------------------------------
     */
    private data class Node(
        val key: String,
        val entries: MutableList<JournalEntry> = mutableListOf(),
        var left: Node? = null,
        var right: Node? = null
    )

    // Root node of tree
    private var root: Node? = null

    /**
     * ---------------------------------------------------------
     * Insert journal entry into tree
     *
     * Time complexity:
     * Average O(log n)
     * Worst O(n)
     * ---------------------------------------------------------
     */
    fun insert(entry: JournalEntry) {

        val key = entry.emotion.trim().uppercase()
        root = insertRec(root, key, entry)
    }

    /**
     * ---------------------------------------------------------
     * Recursive insertion helper
     *
     * Rules:
     * - smaller key to left
     * - greater key to right
     * - same key to add to existing list
     * ---------------------------------------------------------
     */
    private fun insertRec(
        current: Node?,
        key: String,
        entry: JournalEntry
    ): Node {

        if (current == null) {
            return Node(key, mutableListOf(entry))
        }

        when {
            key < current.key ->
                current.left = insertRec(current.left, key, entry)

            key > current.key ->
                current.right = insertRec(current.right, key, entry)

            else ->
                current.entries.add(entry) // duplicate emotion
        }

        return current
    }

    /**
     * ---------------------------------------------------------
     * Search for emotion inside tree
     *
     * Uses iterative traversal:
     * - move left if target smaller
     * - move right if target greater
     *
     * Time complexity:
     * Average O(log n)
     * ---------------------------------------------------------
     */
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