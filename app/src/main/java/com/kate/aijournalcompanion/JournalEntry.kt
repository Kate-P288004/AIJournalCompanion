package com.kate.aijournalcompanion

/**
 * Student: Kate Odabas
 * Represents one saved journal record (used for history, sort, search, and chart).
 */
data class JournalEntry(
    val id: Int,
    val text: String,
    val emotion: String,
    val advice: String
)