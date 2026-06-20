# Solo Ledger

![Solo Ledger brand mark](assets/transparent-background.png)

Solo Ledger is a premium offline-first budgeting app for students and young professionals.

## Product Direction

Solo Ledger is built as a Kotlin and Jetpack Compose Android application using MVVM, Navigation Compose, Room, DataStore, Kotlin Coroutines, StateFlow, and Material 3.

The first implementation slice establishes the Android project foundation, app identity, approved theme palette system, and capsule bottom navigation shell. Persistence, expense management, analytics, onboarding, and export features are intentionally handled in later committed slices.

## Design Rules

No emojis are used in the application.

No generic gradients, crypto styling, excessive glow, blue fintech defaults, gold fintech defaults, or placeholder color systems are used.

The visual direction follows premium wallet-style surfaces, strong contrast, short labels, safe icon spacing, and restrained Material 3 motion.

## Themes

The theme scaffold includes:

1. Ledger Light
2. Ledger Dark
3. Emerald Light
4. Emerald Dark
5. Anime Light
6. Anime Dark
7. Spider Light
8. Spider Dark

## Status

Current slice: PRD gap closure for appearance, dashboard controls, Quick Add behavior, avatar storage, and offline PDF export.

Implemented foundations:

1. Android Compose project scaffold
2. Theme system and app identity
3. GitHub Actions signed debug APK workflow
4. Room entities, DAOs, database, repositories, and DataStore settings
5. Onboarding flow with budget template selection stored offline
6. Quick Add expense form with local image attachment copy and Room persistence
7. Home dashboard metrics, recent transactions, category breakdown, and monthly graph from saved expenses
8. History screen with search, category filters, sorting, expandable details, and move-to-bin
9. Bin section with restore, delete permanently, and clear all actions
10. Calendar screen with spending-day highlights, date details, and range summaries
11. Savings goal creation, progress additions, archiving, and Home progress cards
12. Settings screen with profile, theme, dashboard controls, coming-soon cards, support, and Architect details
13. Category management with add, edit, archive, icon name, and color assignment
14. App-private JSON export/import for categories, expenses, and savings goals
15. Transaction editing from History with local Room updates
16. Quick Add field visibility controls applied to the form
17. Appearance controls for font scale, motion, high contrast, and border radius preferences
18. Dashboard hide/show and reorder controls
19. Profile avatar upload copied into local app storage
20. App-private PDF report export

Next slice: richer chart visuals, local attachment preview, and broader accessibility polish.
