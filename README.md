# Solo Ledger 💰

A modern personal budgeting Android app built with Kotlin, Material 3 design, and Room database.

## Features

- 📊 **Dashboard** – Budget overview, spending progress, recent expenses
- ➕ **Add Expense** – Title, amount, category, date, notes, recurring options
- 📅 **History** – Full list with search
- 📈 **Analytics** – Pie & bar charts (MPAndroidChart)
- 👤 **Profile** – Name, currency, monthly budget, dark theme toggle
- ⚙️ **Settings** – PIN lock, fingerprint, import/export, clear data
- 🏷️ **Categories** – 9 defaults + custom categories
- 🔐 **Security** – PIN + biometric authentication
- 📤 **Import/Export** – JSON and PDF export
- 🔄 **Recurring Expenses** – Weekly / Monthly / Yearly
- 🤖 **Auto-categorization** – Keyword-based smart category suggestion
- 🌙 **Dark Mode** – Full light/dark theme support
- 📴 **Offline First** – All data stored locally with Room

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Material 3, XML Layouts, ViewBinding |
| Architecture | MVVM (ViewModel + LiveData) |
| Database | Room (with KSP) |
| Navigation | Navigation Component |
| Charts | MPAndroidChart |
| Auth | BiometricPrompt |
| Serialization | Gson |
| Coroutines | Kotlin Coroutines |

## Project Structure

```
app/src/main/java/com/mkr/soloLedger/
├── data/           # Room DB, DAOs, Entities, Repository
├── ui/             # Activities and Fragments
├── viewmodel/      # ViewModels
├── adapters/       # RecyclerView Adapters
└── utils/          # Utilities (AutoCategorizer, SecurityManager, etc.)
```

## Build Requirements

- Android Studio Hedgehog (2023.1.1) or later
- JDK 8+
- Android SDK 34
- Gradle 8.0+

## Default Categories

Food 🍕 · Travel ✈️ · Shopping 🛍️ · Entertainment 🎬 · Bills 💡 · Education 📚 · Groceries 🥦 · Subscriptions 📺 · Other

## Developer

**Mohammad Kaif Raja**

- GitHub: [mkr-infinity](https://github.com/mkr-infinity)
- Instagram: [@mkr_infinity](https://instagram.com/mkr_infinity)
- Website: [mkr-infinity.github.io](https://mkr-infinity.github.io)
- Support: [supportmkr.netlify.app](https://supportmkr.netlify.app)

## License

See [LICENSE](LICENSE) file.
