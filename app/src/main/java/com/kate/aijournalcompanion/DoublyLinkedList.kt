package com.kate.aijournalcompanion

class DoublyLinkedList<T> {

    private class Node<T>(var data: T) {
        var prev: Node<T>? = null
        var next: Node<T>? = null
    }

    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    fun add(value: T) {
        val node = Node(value)
        if (head == null) {
            head = node
            tail = node
        } else {
            node.prev = tail
            tail!!.next = node
            tail = node
        }
    }

    fun toList(): List<T> {
        val out = mutableListOf<T>()
        var cur = head
        while (cur != null) {
            out.add(cur.data)
            cur = cur.next
        }
        return out
    }

    fun findAll(predicate: (T) -> Boolean): List<T> {
        val out = mutableListOf<T>()
        var cur = head
        while (cur != null) {
            if (predicate(cur.data)) out.add(cur.data)
            cur = cur.next
        }
        return out
    }
}