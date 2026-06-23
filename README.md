# Solo Ledger

Premium offline-first budgeting app for students & young professionals.
Kotlin · Jetpack Compose · MVVM · Room · DataStore · Navigation Compose · Material 3.

## Status: feature-complete per PRD (pending your compile in Android Studio)

### Themes (8) — all implemented
Ledger Light/Dark, Emerald Light/Dark, Anime Light/Dark, Spider Light/Dark — all no-blue,
switchable live in Settings. Themes drive colors, cards, buttons, charts, nav and accents.

### Screens
- Onboarding (3 screens: About / Setup with budget templates / Permissions with real runtime request)
- Home dashboard (budget overview, donut + bar charts, insights, category breakdown, recent) with show/hide widgets
- Quick Add (amount, title, category, date picker, time picker, notes, image attachment)
- Edit expense (same full field set) + soft delete to Bin
- History (search incl. notes, sort Newest/Oldest/Highest, category filter, expandable detail with time/notes/attachment)
- Calendar (month spend heatmap, tappable day → per-day total, donut breakdown, transactions)
- Analytics (7/30/90-day ranges, donut + bar + animated line charts, category table)
- Savings Goals (create, contribute, delete, progress)
- Bin (restore, delete forever, clear all)
- Categories editor (add/edit/delete, icon grid, color palette)
- Profile (name, avatar upload, budget, currency)
- Settings (theme, dark/animations/reduced-motion/high-contrast, font size + corner radius sliders,
  nav style, dashboard widgets, Quick Add field toggles, JSON import/export, PDF report,
  Coming Soon badges, working Support links, The Architect card)

### Architecture & platform
- MVVM with StateFlow; manual DI via ServiceLocator (no codegen friction)
- Room (Expense / Category / Goal) + repository + JSON BackupManager + PdfExporter
- DataStore for all preferences; offline-first, no backend
- Edge-to-edge + splash screen; safe-area insets; 6 bottom-nav styles + center Quick Add FAB
- Custom Canvas charts (donut / bar / line) with theme-aware colors and animation toggle
- Logo: adaptive icon (your image foreground + #032315 background + monochrome), splash, legacy PNGs, SVG source

## CI: GitHub Actions
`.github/workflows/build-debug-apk.yml` builds a debug-signed APK (auto debug/temp key) on push and
uploads it as the `solo-ledger-debug-apk` artifact. No wrapper jar or signing setup required.

IMPORTANT: push the CONTENTS of this folder as the repo ROOT (settings.gradle.kts and .github/ at top level).

## Build locally
Open the project root in Android Studio (Koala+), let it sync (it creates the Gradle wrapper), Run.
minSdk 24, targetSdk 34.
