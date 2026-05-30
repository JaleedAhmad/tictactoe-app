<div align="center">

# Modern Tic-Tac-Toe Android App

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-%23039BE5.svg?style=for-the-badge&logo=firebase)
![Material UI](https://img.shields.io/badge/Material--UI-0081CB?style=for-the-badge&logo=material-ui&logoColor=white)

A premium, feature-rich Tic-Tac-Toe application built with modern Android development practices. This project showcases a sleek "Midnight Tech" aesthetic with glassmorphism elements, smooth animations, and a robust feature set.

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="150" alt="App Icon">

</div>

## 🏗️ System Architecture

The application is built using the **MVVM (Model-View-ViewModel)** architecture pattern to ensure a clean separation of concerns and a highly reactive user interface. 

- **UI Layer (View):** Built entirely with **Jetpack Compose**. It listens to state changes emitted by the ViewModel and handles all visual interactions, animations, and transitions.
- **Presentation Layer (ViewModel):** The `GameViewModel` acts as the central hub, managing game state, handling business logic (like win/draw detection and minimax AI moves), and coordinating with repositories/managers.
- **Data & Utility Layer (Model):**
  - **FirebaseManager:** Handles secure online capabilities and match data synchronization.
  - **GameFeedbackManager:** Controls custom haptic feedback and sound effects.
  - **Android DataStore:** Persistently stores local user preferences (themes, names, match history).

## 📂 Project Directory Map

```text
app/src/main/kotlin/com/example/tictactoe/
├── MainActivity.kt           # Application entry point and theme setup
├── ui/                       # Jetpack Compose UI Components
│   ├── GameComponents.kt     # Reusable UI elements (buttons, grids, overlays)
│   ├── GameScreen.kt         # Core gameplay interface
│   ├── HistoryScreen.kt      # Match history and statistics view
│   ├── ModeSelectionScreen.kt# Main menu & game mode selection
│   ├── Navigation.kt         # Compose navigation graph and routing
│   ├── OnlineMatchmakingScreen.kt # Firebase matchmaking interface
│   ├── ScoreBoard.kt         # In-game score display component
│   ├── SettingsScreen.kt     # User preferences and customization
│   └── theme/
│       └── Theme.kt          # Material 3 custom tokens and typography
├── util/                     # Core Utilities & Managers
│   ├── FirebaseManager.kt    # Real-time database coordination
│   └── GameFeedbackManager.kt# Haptics and audio management
└── viewmodel/                # State Management
    └── GameViewModel.kt      # Central logic and game state emission
```

## ✨ Core Features

- **🎮 Comprehensive Game Modes:**
  - **Local PvP:** Challenge a friend on the same device.
  - **Player vs CPU:** Compete against an intelligent AI equipped with a Minimax algorithm. Choose from Easy, Medium, or Hard difficulty levels.
  - **Online Matchmaking:** Connect and play with others globally via Firebase real-time integration.
- **⚙️ Deep Customization & Preferences:**
  - Fully customizable player names.
  - **Dynamic Theming:** Seamless switching between a clean Light Mode and a premium "Midnight Tech" Dark Mode.
  - Independent, custom-built haptic feedback and sound controls.
- **📜 Persistent History:** Automatically tracks and safely stores match records using Android DataStore.
- **🏆 Premium UI/UX:**
  - Responsive, reactive layouts with smooth state animations.
  - High-quality glassmorphic overlays for game results.
  - Modern typography powered by *Plus Jakarta Sans*.

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM
- **Design System:** Material 3 (with custom design tokens)
- **Local Storage:** [Android DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore)
- **Backend Services:** [Firebase Realtime Database](https://firebase.google.com/)
- **Navigation:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## 🚀 Developer Setup & Environment

To set up the development environment and build the project locally, follow these steps:

### 1. Environment Requirements
- **JDK:** Java Development Kit 17
- **Android SDK:** API Level 34 (Upside Down Cake)
- **Build System:** Gradle 8.2

### 2. Clone & Setup
```bash
# Clone the repository
git clone https://github.com/JaleedAhmad/tictactoe-app.git
cd tictactoe-app

# Grant execution permissions to the Gradle wrapper
chmod +x gradlew
```

### 3. Build & Run Commands

| Goal | Command |
| :--- | :--- |
| **Clean Project** | `./gradlew clean` |
| **Build Debug APK** | `./gradlew assembleDebug` |
| **Run Unit Tests** | `./gradlew test` |
| **Install on Device** | `./gradlew installDebug` |

*Note: The generated APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.*

---

## 📸 Screenshots

<p align="center">
  <img src="assets/home.jpeg" width="30%" alt="Home Screen">
  <img src="assets/game_display.jpeg" width="30%" alt="Game Screen">
  <img src="assets/settings.jpeg" width="30%" alt="Settings Screen">
</p>

---

<div align="center">
  <i>Developed with ❤️ as a modern take on a classic game.</i>
</div>
