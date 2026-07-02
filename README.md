<p align="center">
  <img src="app/src/main/assets/logo/solo_ledger_logo.svg" width="130" alt="Solo Ledger Logo"/>
</p>

<h1 align="center">💰 Solo Ledger</h1>

<p align="center">
  <b>A premium, offline-first budgeting app for students & young professionals.</b><br/>
  <sub>Track expenses · Set budgets · Hit savings goals — 100% offline, zero cloud, zero tracking.</sub>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose"/>
  <img src="https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Architecture-MVVM-FF6F00?style=flat-square" alt="Architecture"/>
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue?style=flat-square" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Target%20SDK-34-blue?style=flat-square" alt="Target SDK"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License"/>
  <img src="https://img.shields.io/badge/Offline-100%25-success?style=flat-square" alt="Offline"/>
</p>

<p align="center">
  <a href="#-features">Features</a> ·
  <a href="#-screenshots">Screenshots</a> ·
  <a href="#-tech-stack">Tech Stack</a> ·
  <a href="#-installation">Install</a> ·
  <a href="#-release-builds">Release</a> ·
  <a href="CONTRIBUTING.md">Contribute</a>
</p>

---

## 📖 Overview

**Solo Ledger** is a production-quality fintech application built with modern Android development practices. It helps you track expenses, set budgets, manage savings goals, and build real financial awareness — **all completely offline**. No sign-up, no servers, no data ever leaves your device.

> 🎨 Inspired by the design quality of **CRED**, **Google Wallet**, and **Pixel's** design language.

---

## ✨ Features

| | Feature | Description |
|:---:|---|---|
| 📊 | **Budget Tracking** | Set monthly budgets, monitor spending, and visualize usage |
| 💸 | **Expense Management** | Add, edit, delete, search, filter, and restore expenses |
| 🗂️ | **9 Default Categories** | Food, Travel, Shopping, Bills, Education, Entertainment, Groceries, Subscriptions, Other |
| ➕ | **Custom Categories** | Add unlimited custom categories with icons |
| 🎯 | **Savings Goals** | Set targets, track progress, add savings incrementally |
| 📅 | **Calendar View** | Monthly calendar with spending indicators and daily details |
| 📈 | **Analytics** | Donut charts, bar breakdowns, category analysis, savings progress |
| 🧩 | **Budget Templates** | Student, Hostel, Saver, Minimal — one-tap apply |
| 🗑️ | **Bin / Recycle** | Soft-delete with restore or permanent deletion |
| 💱 | **Multi-Currency** | INR default, supports custom currencies with symbols |
| 📤 | **Data Export** | JSON and CSV export with share functionality |
| 📥 | **Data Import** | Restore from JSON backup |
| 🎨 | **8 Themes** | Ledger Dark/Light, Emerald Dark/Light, Anime Dark/Light, Spider Dark/Light |
| 🧭 | **7 Nav Styles** | Capsule, Floating, Minimal, Elevated, Pill, Compact, Material Standard |
| 📴 | **Fully Offline** | No internet required, no cloud dependency |
| 🌟 | **Premium Feel** | Smooth animations, clean typography, modern card-based UI |

---

## 🎨 UI Highlights

- 🧱 Material 3 design system throughout
- 🍩 Custom donut chart component
- 🎬 Animated bottom navigation with 7 interchangeable styles
- 🪄 Expandable cards with smooth transitions
- 🌈 Consistent color system across all themes
- ⚙️ Card-based settings with clean hierarchy
- 📆 Calendar with spending indicators
- 🕳️ Empty states with contextual messaging

---

## 📸 Screenshots

| 🏠 Home | 🧾 History | 📅 Calendar |
|:---:|:---:|:---:|
| ![Home](screenshots/home.png) | ![History](screenshots/history.png) | ![Calendar](screenshots/calendar.png) |

| 📈 Analytics | ⚙️ Settings | 🎨 Themes |
|:---:|:---:|:---:|
| ![Analytics](screenshots/analytics.png) | ![Settings](screenshots/settings.png) | ![Themes](screenshots/themes.png) |

---

## 🌗 Theme Showcase

| 🖤 Ledger Dark | 🤍 Ledger Light | 💚 Emerald Dark | 🕷️ Spider Dark |
|:---:|:---:|:---:|:---:|
| Warm charcoal with sage accents | Cream with deep greens | Deep forest green | Red and black cinematic |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| 🧑‍💻 Language | Kotlin |
| 🎨 UI | Jetpack Compose |
| 🧱 Design | Material 3 |
| 🏛️ Architecture | MVVM |
| 🧭 Navigation | Navigation Compose |
| 🗄️ Database | Room |
| ⚙️ Preferences | DataStore |
| ⚡ Async | Coroutines + StateFlow |
| 🖼️ Image | Coil |
| 🔁 Serialization | Gson |

---

## 📱 Device Compatibility

| Requirement | Value |
|---|---|
| 🟢 Minimum Android | 8.0 Oreo (API 26) |
| 🎯 Target Android | 14 (API 34) |
| ⬆️ Maximum Android | Latest (no upper limit) |
| 🏗️ Architecture | arm64-v8a, armeabi-v7a, x86_64 |
| 📐 Screen | All sizes supported |

---

## 🚀 Installation

### 📋 Prerequisites

- 🧩 Android Studio Hedgehog or later
- ☕ JDK 17
- 📦 Android SDK 34

### 🔨 Build (debug)

```bash
git clone https://github.com/mkr-infinity/Solo-Ledger.git
cd Solo-Ledger
./gradlew assembleDebug
```

### 📲 Install

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

> 💡 Or simply open the project in **Android Studio** and hit ▶️ Run.

---

## 🔐 Release Builds

Solo Ledger ships as a **signed release APK**. Signing credentials are never committed — they live in a local `keystore.properties` (git-ignored) or, for CI, in GitHub Actions **secrets**.

### 🏠 Local signed release

1. Copy the example config and fill in your keystore values:
   ```bash
   cp keystore.properties.example keystore.properties
   ```
2. Build:
   ```bash
   ./gradlew assembleRelease
   ```
   Output → `app/build/outputs/apk/release/app-release.apk`

### ☁️ CI release (GitHub Actions — manual trigger)

The release workflow (`.github/workflows/release.yml`) is **manual only** (`workflow_dispatch`) — it never runs automatically on push or PR.

> Run it from **GitHub → Actions → Build Signed Release APK → Run workflow**.

It requires these repository **secrets**:

| Secret | Description |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded keystore file |
| `KEYSTORE_PASSWORD` | Keystore (store) password |
| `KEY_ALIAS` | Key alias (`solo-ledger`) |
| `KEY_PASSWORD` | Key password |

📄 Full step-by-step signing & secrets guide → **[CONTRIBUTING.md](CONTRIBUTING.md#-release-signing--secrets)**

---

## 🗂️ Project Structure

```
app/src/main/java/com/solo/ledger/
├── 📱 SoloLedgerApp.kt         # Application class
├── 🚪 MainActivity.kt          # Entry point
├── 📂 data/
│   ├── dao/                    # Room DAOs
│   ├── database/               # Database definition
│   ├── model/                  # Entity models
│   ├── preferences/            # DataStore preferences
│   └── repository/             # Data repositories
└── 🎨 ui/
    ├── components/             # Reusable UI components
    ├── navigation/             # Nav graph, bottom bar, styles
    ├── screens/                # All app screens
    ├── theme/                  # Colors, typography, theme
    └── viewmodel/              # MVVM ViewModel
```

---

## 🤝 Contributing

Contributions are welcome and appreciated! 🎉

Please read the **[Contribution Guide](CONTRIBUTING.md)** for setup, coding standards, and the pull-request workflow.

Quick start:

```bash
git checkout -b feature/your-feature
# make your changes
git commit -m "Add your feature"
git push origin feature/your-feature
```

Then open a Pull Request 🚀

---

## ❤️ Support

If you find Solo Ledger useful, consider supporting the project:

- ☕ [Buy Me a Coffee](https://buymeacoffee.com/mkr_infinity)
- ⭐ [Star on GitHub](https://github.com/mkr-infinity/Solo-Ledger)
- 📢 Share with friends

---

## 👨‍💻 Developer

**Mohammad Kaif Raja**

<p align="left">
  <a href="https://github.com/mkr-infinity"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white" alt="GitHub"/></a>
  <a href="https://mkr-infinity.github.io/"><img src="https://img.shields.io/badge/Portfolio-FF7139?style=flat-square&logo=firefox&logoColor=white" alt="Portfolio"/></a>
  <a href="https://instagram.com/mkr_infinity"><img src="https://img.shields.io/badge/Instagram-E4405F?style=flat-square&logo=instagram&logoColor=white" alt="Instagram"/></a>
  <a href="https://t.me/mkr_infinity"><img src="https://img.shields.io/badge/Telegram-26A5E4?style=flat-square&logo=telegram&logoColor=white" alt="Telegram"/></a>
</p>

---

## 📄 License

Released under the **MIT License** © 2024 Mohammad Kaif Raja.

```
MIT License

Copyright (c) 2024 Mohammad Kaif Raja

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

<p align="center"><sub>Built with ❤️ and Kotlin · Solo Ledger</sub></p>
