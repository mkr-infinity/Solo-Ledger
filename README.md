<p align="center">
  <img src="app/src/main/assets/logo/solo_ledger_logo.svg" width="120" alt="Solo Ledger Logo"/>
</p>

<h1 align="center">Solo Ledger</h1>

<p align="center">
  A premium offline-first budgeting application for students and young professionals.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square" alt="Platform"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square" alt="Language"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=flat-square" alt="UI"/>
  <img src="https://img.shields.io/badge/Architecture-MVVM-orange?style=flat-square" alt="Architecture"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License"/>
</p>

---

## Overview

Solo Ledger is a production-quality fintech application built with modern Android development practices. It helps users track expenses, set budgets, manage savings goals, and gain financial awareness — all completely offline.

Inspired by the design quality of CRED, Google Wallet, and Pixel's design language.

---

## Features

- **Budget Tracking** — Set monthly budgets, monitor spending, and visualize usage
- **Expense Management** — Add, edit, delete, search, filter, and restore expenses
- **9 Default Categories** — Food, Travel, Shopping, Bills, Education, Entertainment, Groceries, Subscriptions, Other
- **Custom Categories** — Add unlimited custom categories with icons
- **Savings Goals** — Set targets, track progress, add savings incrementally
- **Calendar View** — Monthly calendar with spending indicators and daily details
- **Analytics** — Donut charts, bar breakdowns, category analysis, savings progress
- **Budget Templates** — Student, Hostel, Saver, Minimal — one-tap apply
- **Bin / Recycle** — Soft-delete with restore or permanent deletion
- **Multi-Currency** — INR default, supports custom currencies with symbols
- **Data Export** — JSON and CSV export with share functionality
- **Data Import** — Restore from JSON backup
- **8 Themes** — Ledger Dark/Light, Emerald Dark/Light, Anime Dark/Light, Spider Dark/Light
- **7 Navigation Styles** — Capsule, Floating, Minimal, Elevated, Pill, Compact, Material Standard
- **Fully Offline** — No internet required, no cloud dependency
- **Premium Feel** — Smooth animations, clean typography, modern card-based UI

---

## UI Highlights

- Material 3 design system throughout
- Custom donut chart component
- Animated bottom navigation with 7 interchangeable styles
- Expandable cards with smooth transitions
- Consistent color system across all themes
- Card-based settings with clean hierarchy
- Calendar with spending indicators
- Empty states with contextual messaging

---

## Screenshots

| Home | History | Calendar |
|------|---------|----------|
| ![Home](screenshots/home.png) | ![History](screenshots/history.png) | ![Calendar](screenshots/calendar.png) |

| Analytics | Settings | Themes |
|-----------|----------|--------|
| ![Analytics](screenshots/analytics.png) | ![Settings](screenshots/settings.png) | ![Themes](screenshots/themes.png) |

---

## Theme Showcase

| Ledger Dark | Ledger Light | Emerald Dark | Spider Dark |
|-------------|--------------|--------------|-------------|
| Warm charcoal with sage accents | Cream with deep greens | Deep forest green | Red and black cinematic |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design | Material 3 |
| Architecture | MVVM |
| Navigation | Navigation Compose |
| Database | Room |
| Preferences | DataStore |
| Async | Coroutines + StateFlow |
| Image | Coil |
| Serialization | Gson |

---

## Installation

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build

```bash
git clone https://github.com/mkr-infinity/Solo-Ledger.git
cd Solo-Ledger
./gradlew assembleDebug
```

### Install

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or open in Android Studio and run on device/emulator.

---

## Project Structure

```
app/src/main/java/com/solo/ledger/
├── SoloLedgerApp.kt          # Application class
├── MainActivity.kt            # Entry point
├── data/
│   ├── dao/                   # Room DAOs
│   ├── database/              # Database definition
│   ├── model/                 # Entity models
│   ├── preferences/           # DataStore preferences
│   └── repository/            # Data repositories
└── ui/
    ├── components/            # Reusable UI components
    ├── navigation/            # Nav graph, bottom bar, styles
    ├── screens/               # All app screens
    ├── theme/                 # Colors, typography, theme
    └── viewmodel/             # MVVM ViewModel
```

---

## Contributing

Contributions are welcome. Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push to branch (`git push origin feature/your-feature`)
5. Open a Pull Request

### Guidelines

- Follow existing code style and architecture
- Use Material 3 components
- Maintain offline-first philosophy
- Test on multiple screen sizes
- No emojis in code or UI

---

## Support

If you find Solo Ledger useful, consider supporting the project:

- [Buy Me a Coffee](https://buymeacoffee.com/mkr_infinity)
- [Star on GitHub](https://github.com/mkr-infinity/Solo-Ledger)
- Share with friends

---

## Developer

**Mohammad Kaif Raja**

- [GitHub](https://github.com/mkr-infinity)
- [Portfolio](https://mkr-infinity.github.io/)
- [Instagram](https://instagram.com/mkr_infinity)
- [Telegram](https://t.me/mkr_infinity)

---

## License

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
