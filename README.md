# 🤖 Nexus Chatbot

A modern Android chatbot application built with Kotlin and XML layouts. The application provides a conversational interface where users can send messages and receive AI-generated responses through a backend API.

## 📱 Project Overview

Nexus Chatbot is an Android-based AI chatbot application designed to provide a simple and user-friendly conversational experience.

The application includes:

- 🔐 User Login
- 📝 User Registration
- 💬 AI Chat Interface
- 🤖 Backend-based AI Responses
- 📜 Chat History Drawer
- ⚙️ Settings Screen
- 👤 User Session Management
- 📎 Attachment Support
- 📷 Camera
- 🖼️ Gallery
- 📄 Document / File
- 🌐 API Communication
- 📱 Material Design UI

The Android application communicates with a backend server for authentication and AI chat functionality.

---

# ✨ Features

## 🔐 Authentication

- User login using email and password
- User registration
- Password confirmation
- Remember me option
- Forgot password option
- Google sign-in interface

## 💬 AI Chat

- Modern chatbot interface
- Message input
- Send messages
- Receive chatbot responses
- Chat message display
- Navigation drawer
- Settings access

## 📎 Attachment System

Users can attach different types of content:

- 📷 Camera
- 🖼️ Gallery
- 📄 Document / File

## 📜 Chat History

The application provides a navigation drawer for accessing:

- New Chat
- Recent conversations
- Chat history

## ⚙️ Settings

The settings screen includes:

- 👤 User Profile
- ✏️ Edit Profile
- 🎨 Appearance
- 🌙 Theme
- 💬 Chat Settings
- 🔔 Notifications
- 🤖 Nexus Settings
- 🔒 Privacy
- 👤 Account Settings
- ℹ️ About Nexus

---

# 📸 Application Screenshots

## 💬 Main Chat Screen

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.13%20PM.jpeg" width="350">

---

## 📎 Choose Attachment

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.08%20PM.jpeg" width="350">

---

## 📜 Navigation Drawer

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.14%20PM.jpeg" width="350">

---

## ⚙️ Settings - Profile & Appearance

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.15%20PM.jpeg" width="350">

---

## 💬 Settings - Chat & Notifications

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.16%20PM.jpeg" width="350">

---

## 🔒 Settings - Privacy, Account & About

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.16%20PM%20%281%29.jpeg" width="350">

---

## 🚪 Logout Confirmation

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.17%20PM.jpeg" width="350">

---

## 🔐 Login Screen

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.18%20PM%20%281%29.jpeg" width="350">

---

## 📝 Sign Up Screen

<img src="./WhatsApp%20Image%202026-09-25%20at%207.04.18%20PM.jpeg" width="350">

---

# 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Application development |
| Android Studio | Development environment |
| XML | UI layouts |
| RecyclerView | Displaying chat messages |
| Material Components | UI components |
| DrawerLayout | Navigation drawer |
| Retrofit | API communication |
| Gson Converter | JSON serialization/deserialization |
| Kotlin Coroutines | Background/network operations |
| HTTP/REST API | Communication with backend |
| Gradle Kotlin DSL | Project build configuration |

The project targets SDK 36 and has a minimum SDK of 26.

---

# 🏗️ Project Architecture

```text
24012021038_chatbot
│
├── app
│   ├── src
│   │   └── main
│   │       ├── java
│   │       │   └── com.jayshil.a24012021038_chatbot
│   │       │       │
│   │       │       ├── api
│   │       │       │   ├── NexusApi.kt
│   │       │       │   └── NexusClient.kt
│   │       │       │
│   │       │       ├── MainActivity.kt
│   │       │       ├── MainLogin.kt
│   │       │       ├── ChatAdapter.kt
│   │       │       ├── ChatMessage.kt
│   │       │       ├── SessionManager.kt
│   │       │       ├── SettingsActivity.kt
│   │       │       ├── User.kt
│   │       │       └── UserRepository.kt
│   │       │
│   │       ├── res
│   │       │   ├── drawable
│   │       │   ├── layout
│   │       │   ├── mipmap
│   │       │   ├── values
│   │       │   └── xml
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts
│   └── ...
│
├── gradle
├── README.md
├── build.gradle.kts
└── settings.gradle.kts
```

---

# 🔄 How the Application Works

```text
        ┌─────────────────┐
        │    Open App     │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │   Login Screen  │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Authentication  │
        │   via Backend   │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │   Chat Screen   │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Enter Message   │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Android Client  │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Backend Service │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Chat Response   │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Display Reply   │
        └─────────────────┘
```

---

# 🌐 API Communication

The project contains a dedicated API package:

```text
api/
├── NexusApi.kt
└── NexusClient.kt
```

Retrofit and Gson are used for communication between the Android application and backend server.

Example chat request:

```json
{
  "message": "Hello"
}
```

The backend processes the message and returns the chatbot response to the Android application.

> **Important:** The backend server must be running and reachable from the Android application for chatbot functionality to work.

---

# 💻 Requirements

Before running the project, make sure you have:

- Android Studio
- Android SDK
- JDK 11
- Android device or emulator
- Running chatbot backend server
- Internet/network access when required by the backend

### Project Configuration

```text
Minimum SDK: 26
Target SDK: 36
Compile SDK: 36
Java Compatibility: 11
```

---

# 🚀 Installation

## 1. Clone the Repository

```bash
git clone https://github.com/jayshilpatel29/24012021038_chatbot.git
```

## 2. Open in Android Studio

Open the cloned project in Android Studio.

## 3. Sync Gradle

Allow Android Studio to download and configure the required dependencies.

## 4. Configure the Backend

Make sure the Nexus backend server is running and accessible from your Android device or emulator.

## 5. Run the Application

Connect an Android device or start an emulator and click:

```text
Run ▶
```

---

# 📂 Important Files

### `MainActivity.kt`

Responsible for:

- Main chatbot screen
- Message input
- Sending messages
- Receiving responses
- Navigation drawer
- Opening settings

### `MainLogin.kt`

Handles:

- Login screen
- User authentication
- Login API communication

### `ChatMessage.kt`

Represents an individual chat message.

### `ChatAdapter.kt`

Connects chat messages with the RecyclerView.

### `SessionManager.kt`

Manages user session information.

### `User.kt`

Represents user information.

### `UserRepository.kt`

Provides user-related repository operations.

### `SettingsActivity.kt`

Provides the Nexus settings screen.

### `NexusApi.kt`

Defines the API data models and API endpoints.

### `NexusClient.kt`

Creates and configures the Retrofit client.

---

# 🔒 Security Note

Do not commit:

- Passwords
- API keys
- Private tokens
- Authentication secrets
- Other sensitive credentials

For production deployment, sensitive configuration should be stored securely.

---

# 🎯 Future Improvements

- 💾 Persistent chat history
- 🗑️ Delete conversations
- 🌓 Dark/light theme support
- 🎙️ Voice input
- 🔊 Text-to-speech responses
- 📎 Advanced file/image support
- 🔔 Push notifications
- 🔐 Improved authentication
- 🌍 Multiple language support
- ⚡ Streaming responses
- ☁️ Production backend deployment

---

# 👨‍💻 Developer

**Jayshil Patel**

Student Project — Android Chatbot Application

---

# 📄 License

This project is created for educational and development purposes.

---

# 🔗 Repository

[24012021038_chatbot](https://github.com/jayshilpatel29/24012021038_chatbot)
