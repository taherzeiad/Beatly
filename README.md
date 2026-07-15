# 🎵 Beatly

**A premium Android application designed to create stunning music videos and slideshows seamlessly synced with the rhythm and beats.**

[![Android Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

<p align="center">
  <a href="#-overview">Overview</a> •
  <a href="#-features">Features</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-tech-stack--libraries">Tech Stack</a>
</p>

---

## 📱 Overview

**Beatly** is an intuitive, creator-focused Android application that allows users to transform ordinary photos and video clips into professional, high-energy short videos. By leveraging automated beat-matching technology, transitions and effects perfectly align with the music track, offering an engaging visual experience.

---

## ✨ Features

* 🎼 **Smart Beat Synchronization:** Transitions and visual cut-offs auto-adjust dynamically to the tempo and rhythm of the track.
* 🎬 **Trendy Templates:** Access a growing library of ready-to-use video templates with advanced cinematic effects.
* 📷 **Dynamic Slideshows:** Turn static images into fluid, lively video content effortlessly.
* ⚡ **Fluid Performance:** Built entirely natively to ensure lightweight processing, fast rendering, and optimized battery usage.
* 💾 **High-Quality Export:** Support for exporting high-definition (HD) media outputs without compromising fidelity.

---

## 🛠️ Tech Stack & Libraries

The app follows modern Android development standards, ensuring scalable, testable, and robust codebases:

* **Language:** [Kotlin](https://kotlinlang.org/) - Modern, expressive, and concise.
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - Declarative UI development for fluid animations and modern layouts.
* **Asynchronous Flow:** Coroutines & Kotlin Flow for structured concurrency and reactive data streams.
* **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Built on top of Dagger to provide compile-time correctness and decoupled architecture.
* **Navigation:** Jetpack Compose Navigation for decoupled, type-safe navigation graphs.
* **Image Loading:** Coil for efficient, lifecycle-aware asynchronous image rendering.

---

## 🚀 Architecture

Beatly is structured around **Clean Architecture** principles alongside the **MVVM (Model-View-ViewModel)** design pattern to enforce clear separation of concerns:

```text
📊 app
 └── 📂 src
      └── 📂 main
           └── 📂 java/com/taherzeiad/beatly
                ├── 📂 data        # Repositories, Data Sources, and API configurations
                ├── 📂 domain      # Pure business logic (Use Cases & Core Models)
                └── 📂 ui          # Presentation Layer (Compose Screens, ViewModels, Theme)
                     ├── 📂 components
                     ├── 📂 screens
                     └── 📂 theme       # Custom design system (Colors, Typography)
