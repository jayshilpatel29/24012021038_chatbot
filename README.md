# 🤖 Nexus Chatbot

A modern Android chatbot application built with **Kotlin** and **XML layouts**. The application provides a conversational interface where users can send messages and receive AI-generated responses through a backend API.

## 📱 Project Overview

**Nexus Chatbot** is an Android-based AI chatbot application designed to provide a simple and user-friendly conversational experience.

The application includes:

- 🔐 User Login
- 💬 AI Chat Interface
- 🤖 Backend-based AI Responses
- 📜 Chat History Drawer
- ⚙️ Settings Screen
- 👤 User Session Management
- 🌐 API Communication
- 📱 Material Design UI

The Android application communicates with a backend server for authentication and AI chat functionality.

## ✨ Features

### 🔐 Authentication
- User login using email and password.
- Authentication is handled through the Nexus API.
- User information and authentication state are managed by the application.

### 💬 AI Chat
- Users can type a message and send it to the chatbot.
- User messages are displayed immediately in the chat.
- The message is sent to the backend AI service.
- The AI response is displayed in the chat interface.
- Chat messages are displayed using a `RecyclerView`.

### 📜 Chat History
The application provides a navigation drawer for accessing chat/history-related functionality.

### ⚙️ Settings
A dedicated settings screen is included and can be opened from the main application interface.

### 🎨 Material UI
The application uses Android Material Components together with XML-based layouts to create the user interface.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| **Kotlin** | Application development |
| **Android Studio** | Development environment |
| **XML** | UI layouts |
| **RecyclerView** | Displaying chat messages |
| **Material Components** | UI components |
| **DrawerLayout** | Navigation drawer |
| **Retrofit** | API communication |
| **Gson Converter** | JSON serialization/deserialization |
| **Kotlin Coroutines** | Background/network operations |
| **HTTP/REST API** | Communication with backend |
| **Gradle Kotlin DSL** | Project build configuration |

The current Gradle configuration uses Retrofit `2.11.0`, Gson converter, Material Components, and DrawerLayout. The project targets SDK 36 and has a minimum SDK of 26. citeturn1view1

## 🏗️ Project Architecture

The application is organized into UI, model, session, repository, and API-related components.

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
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

The repository contains the Android source under `app/src/main`, including the chatbot activity, login activity, settings, user/session classes, and API package. citeturn2view1turn3view0turn4view0

## 🔄 How the Application Works

The basic application flow is:

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
        │ Backend / AI    │
        │     Service     │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ AI Response     │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Display Reply   │
        └─────────────────┘
```

In `MainActivity`, the user's message is added to the chat immediately and then sent to the backend AI service. Network work is performed using a coroutine on the IO dispatcher. citeturn4view1

## 🌐 API Communication

The project contains a dedicated API package:

```text
api/
├── NexusApi.kt
└── NexusClient.kt
```

`NexusApi.kt` defines the login request/response models and the authentication API endpoint. citeturn5view0

The project uses Retrofit and Gson for API communication:

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
```

The Retrofit client is configured with a backend base URL. citeturn5view1turn1view1

### Chat API

The chat functionality sends JSON containing the user's message to the backend:

```json
{
  "message": "Hello"
}
```

The backend processes the message and returns the AI response to the Android application.

> **Important:** The backend server must be running and reachable from the Android application for AI chat functionality to work.

## 💻 Requirements

Before running the project, make sure you have:

- Android Studio
- Android SDK
- JDK 11 or compatible Android Studio Java configuration
- Android device or emulator
- Running chatbot backend server
- Internet/network access when required by the backend

The project is configured with:

```text
Minimum SDK: 26
Target SDK: 36
Compile SDK: 36
Java Compatibility: 11
```

These values are taken from the project's current Gradle configuration. citeturn1view1

## 🚀 Installation

### 1. Clone the repository

```bash
git clone https://github.com/jayshilpatel29/24012021038_chatbot.git
```

### 2. Open in Android Studio

Open the cloned project in **Android Studio**.

### 3. Sync Gradle

Allow Android Studio to download and configure the required Gradle dependencies.

### 4. Configure the Backend

Make sure the chatbot backend is running.

The Android application communicates with the backend using HTTP requests, so the backend address must be reachable from the emulator/device.

### 5. Run the Application

Connect an Android device or start an emulator and click:

```text
Run ▶
```

The application will build and launch on the selected device.

## 🔑 Login API

The application defines a login request containing:

```json
{
  "email": "user@example.com",
  "password": "password"
}
```

The login response can contain:

- `status`
- `message`
- `access_token`
- `token_type`
- `user`

The user object contains:

- User ID
- Name
- Email

This structure is defined in `NexusApi.kt`. citeturn5view0

## 📂 Important Files

### `MainActivity.kt`

Main chatbot screen.

Responsible for:

- Displaying chat messages
- Handling message input
- Sending messages
- Receiving AI responses
- Managing the navigation drawer
- Opening settings citeturn4view1


### `MainLogin.kt`

Handles the application's login screen and authentication flow.

### `ChatMessage.kt`

Represents an individual chat message.

### `ChatAdapter.kt`

Connects chat messages with the `RecyclerView` and displays them in the chat interface.

### `SessionManager.kt`

Handles user session-related information.

### `User.kt`

Represents user information.

### `UserRepository.kt`

Provides a repository layer for user-related operations.

### `SettingsActivity.kt`

Provides the application's settings screen.

### `NexusApi.kt`

Defines the API data models and authentication API interface.

### `NexusClient.kt`

Creates and configures the Retrofit client used to communicate with the backend. citeturn3view0turn4view0


## 🔒 Security Note

Do not commit passwords, API keys, private tokens, or other sensitive credentials to GitHub.

For production deployment, backend URLs and credentials should be managed using secure configuration rather than hard-coded values.

## 🎯 Future Improvements

Possible future improvements include:

- 💾 Persistent chat history
- 🗑️ Delete conversations
- 🌓 Dark/light theme support
- 🎙️ Voice input
- 🔊 Text-to-speech responses
- 📎 File/image support
- 🔔 Notifications
- 🔐 Improved authentication and token management
- 🌍 Multiple language support
- ⚡ Streaming AI responses
- ☁️ Production backend deployment

## 👨‍💻 Developer

**Jayshil Patel**

Student Project — Android AI Chatbot

## 📄 License

This project is created for educational and development purposes.

## 🔗 Repository

[24012021038_chatbot on GitHub](https://github.com/jayshilpatel29/24012021038_chatbot?utm_source=chatgpt.com)
