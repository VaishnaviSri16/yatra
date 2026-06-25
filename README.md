# Yatra 🇮🇳

A travel discovery app for exploring popular Indian destinations — built entirely with **Kotlin** and **Jetpack Compose** to showcase custom UI design and motion/animation skills for frontend Android development.

All images used in the app (destination photos, icons) are original/self-sourced and placed in `drawable`.

---

## ✨ Features

- **Animated Featured Carousel** — auto-scrolling destination cards on the home screen
- **Popular Destinations List** — scrollable card list with live-style pricing and ratings
- **Destination Detail Screen** — weather stats (temperature, humidity, wind), star ratings, and dynamic pricing
- **Animated Booking Confirmation** — confetti animation with a dynamically generated Booking ID on successful booking
- **Dark theme UI** built from scratch with custom composables (no UI library dependency)

---

## 📱 Screenshots

| Home Screen | Destination Detail | Booking Confirmation |
|---|---|---|
| ![Home]() | ![Detail]() | ![Confirmation]() |

> 🎥 **[Watch the full demo →]()**

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Architecture:** MVVM
- **Animation:** Compose Animation APIs (`animateFloatAsState`, `AnimatedVisibility`, custom `Canvas` for confetti)
- **Navigation:** Navigation Compose
- **Layouts:** `LazyRow`, `LazyColumn`, custom composables for cards and stat chips

---

## 🚀 Getting Started

```bash
git clone https://github.com/Vaishnavisri16/yatra.git
```

Open the project in Android Studio, let Gradle sync, and run on an emulator or device (min SDK as configured in `app/build.gradle`).

---

## 🎯 Why I built this

This project was built to demonstrate frontend/UI engineering skills for Android — custom layouts, smooth animations, and a polished, production-feel user experience — entirely using Jetpack Compose without any pre-built UI kit.

---

## 📄 License

This project is for portfolio/demonstration purposes.
