
# AI Journal Companion

## Project Overview

AI Journal Companion is a mobile application that helps users reflect on their daily experiences. 
Users can write journal entries, and the system analyzes the text using an AI model to detect the user's emotional state and provide short advice.

The project demonstrates Android development, algorithms, data structures, and AI integration using a REST API backend.

---

## System Architecture

Android App (Jetpack Compose)  
→ REST API (HTTP POST)  
→ FastAPI Backend (Python)  
→ Ollama AI Model (Gemma 2B)  
→ Emotion + Advice Response (JSON)

---

## Features

- Journal entry input
- AI emotion analysis
- Advice generation
- Journal history storage
- Sorting journal entries
- Searching entries by emotion
- Emotion distribution pie chart
- Drag‑and‑drop deletion
- Help popup with instructions

---

## Algorithms Implemented

Sorting algorithms:
- Bubble Sort
- Insertion Sort
- Selection Sort

---

## Data Structures Implemented

- Binary Tree
- HashMap
- Doubly Linked List

These structures are used to search journal entries by emotion.

---

## Technologies Used

Frontend:
- Android Studio
- Kotlin
- Jetpack Compose

Backend:
- Python
- FastAPI
- Uvicorn

AI Model:
- Ollama
- Gemma 2B

---

## Backend Setup

Install dependencies:

pip install fastapi uvicorn

Install Ollama model:

ollama pull gemma:2b

Run backend:

uvicorn main:app --reload

Server runs at:

http://127.0.0.1:8000

---

## Running the Android App

1. Open the project in Android Studio
2. Select an Android Emulator
3. Click Run

Make sure the backend server is running before starting the app.

---

## APK Installation

The application can also be installed using the generated APK file.

Location:

app/build/outputs/apk/debug/app-debug.apk

Steps:
1. Copy APK to Android device or emulator
2. Open the file
3. Tap Install

---

## Project Structure

com.kate.aijournalcompanion

MainActivity.kt  
ApiClient.kt  
ApiService.kt  
JournalEntry.kt  
JournalRequest.kt  
JournalResponse.kt  

SortUtils.kt  
SearchUtils.kt  

BinaryTree.kt  
DoublyLinkedList.kt  

DragDropDelete.kt  
HelpDialog.kt  
PieChart.kt  

ui/theme  
- Color.kt  
- Theme.kt  
- Type.kt  

---

## Testing

Testing included:

- Functional testing
- Backend integration testing
- Performance testing using Android Studio Profiler
- Debugging using breakpoints

All test cases passed successfully.

---


