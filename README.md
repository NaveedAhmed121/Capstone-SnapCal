# 📱 SnapCal – Smart Nutrition & Fitness Tracker

## 📌 Overview

SnapCal is a modern Android application designed to help users track their daily nutrition, workouts, and hydration. The app provides an intuitive interface and integrates advanced features like barcode scanning to simplify food tracking and promote a healthier lifestyle.

---

## 🚀 Features

* 🥗 **Meal Tracking**
  Log daily meals and monitor calorie intake.

* 💪 **Workout Tracking**
  Record exercises and track fitness progress.

* 💧 **Water Intake Monitoring**
  Stay hydrated by logging daily water consumption.

* 📷 **Barcode Scanning (ML Kit)**
  Quickly scan food items to retrieve details.

* 📊 **Dashboard Analytics**
  View summarized daily health statistics.

* 🗂 **Local Storage (Room Database)**
  Efficient and reliable offline data storage.

---

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Database:** Room
* **ML Integration:** Google ML Kit (Barcode Scanning)
* **Build Tool:** Gradle (Kotlin DSL)

---

## 🏗 Architecture

The application follows a clean and modular architecture:

* **UI Layer:** Jetpack Compose screens and components
* **Data Layer:** Room Database (Entities, DAO, Database)
* **Logic Layer:** ViewModels for state management

---

## 📂 Project Structure

```
SnapCal/
│
├── app/
│   ├── data/
│   │   ├── db/
│   │   ├── dao/
│   │   └── models/
│   │
│   ├── ui/
│   │   ├── screens/
│   │   └── components/
│   │
│   └── MainActivity.kt
│
└── build.gradle.kts
```

---

## ▶️ Getting Started

### Prerequisites

* Android Studio (Latest Version)
* Android SDK installed
* Emulator or physical Android device

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/NaveedAhmed121/SnapCal.git
   ```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Run the app on an emulator or device

---

## 👨‍💻 Team

* **Naveed Ahmed**
* **Romero Garcia, Omar**
* **Jacques Antoine**
  

---

## 📅 Academic Context

This project was developed as part of:

**COMP3078 – Capstone Project II**
George Brown College

---

## 📌 Future Improvements

* ☁️ Cloud synchronization (Firebase)
* 🤖 AI-based food recommendations
* 📈 Advanced analytics and reports
* 🔐 User authentication system

---

## 📜 License

This project is for academic purposes only.

---
