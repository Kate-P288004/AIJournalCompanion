package com.kate.aijournalcompanion

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * Purpose:
 * - Custom implementation of a Doubly Linked List
 * - Used for searching journal entries
 *
 * Assessment Concepts Demonstrated:
 * - Custom data structures
 * - Node-based memory links
 * =========================================================
 */

class DoublyLinkedList<T> {

    /**
     * ---------------------------------------------------------
     * Node structure
     * Each node stores:
     * - data value
     * - pointer to previous node
     * - pointer to next node
     * ---------------------------------------------------------
     */
    private class Node<T>(var data: T) {
        var prev: Node<T>? = null
        var next: Node<T>? = null
    }

    // ---------------------------------------------------------
    // List boundaries
    // head = first node
    // tail = last node
    // ---------------------------------------------------------
    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    /**
     * ---------------------------------------------------------
     * Add element to end of list
     * Time complexity: O(1)
     * ---------------------------------------------------------
     */
    fun add(value: T) {

        val node = Node(value)

        // First element case
        if (head == null) {
            head = node
            tail = node
        } else {
            // Link new node at end
            node.prev = tail
            tail!!.next = node
            tail = node
        }
    }

    /**
     * ---------------------------------------------------------
     * Convert linked list to Kotlin List
     * From head to tail
     * Time complexity: O(n)
     * ---------------------------------------------------------
     */
    fun toList(): List<T> {

        val out = mutableListOf<T>()
        var cur = head

        while (cur != null) {
            out.add(cur.data)
            cur = cur.next
        }

        return out
    }

    /**
     * ---------------------------------------------------------
     * Find all elements matching condition
     *
     * Parameter:
     * predicate = function used as filter
     *
     * Example:
     * findAll { it.emotion == "JOY" }
     *
     * Time complexity: O(n)
     * ---------------------------------------------------------
     */
    fun findAll(predicate: (T) -> Boolean): List<T> {

        val out = mutableListOf<T>()
        var cur = head

        while (cur != null) {
            if (predicate(cur.data)) {
                out.add(cur.data)
            }
            cur = cur.next
        }

        return out
    }
}