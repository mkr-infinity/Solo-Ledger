<h1 align="center">🤝 Contributing to Solo Ledger</h1>

<p align="center">
  <sub>Thanks for taking the time to contribute! 💚 Every issue, idea, and pull request helps.</sub>
</p>

---

## 📚 Table of Contents

- [🌱 Getting Started](#-getting-started)
- [🧭 Ways to Contribute](#-ways-to-contribute)
- [🔨 Development Setup](#-development-setup)
- [🎨 Coding Standards](#-coding-standards)
- [🧩 Project Structure](#-project-structure)
- [🔀 Pull Request Workflow](#-pull-request-workflow)
- [🐛 Reporting Bugs](#-reporting-bugs)
- [💡 Suggesting Features](#-suggesting-features)
- [🔐 Release Signing & Secrets](#-release-signing--secrets)
- [📜 Code of Conduct](#-code-of-conduct)

---

## 🌱 Getting Started

1. ⭐ **Star** the repo (optional but appreciated!)
2. 🍴 **Fork** the repository
3. 📥 **Clone** your fork:
   ```bash
   git clone https://github.com/<your-username>/Solo-Ledger.git
   cd Solo-Ledger
   ```
4. 🌿 Create a **feature branch**:
   ```bash
   git checkout -b feature/your-feature
   ```

---

## 🧭 Ways to Contribute

| | Type | Examples |
|:---:|---|---|
| 🐛 | **Bug fixes** | Crashes, UI glitches, incorrect calculations |
| ✨ | **Features** | New screens, categories, analytics, themes |
| 🎨 | **Design** | UI polish, animations, new themes / nav styles |
| 📝 | **Docs** | README, comments, guides, typos |
| 🧪 | **Tests** | Unit tests, UI tests |
| ♿ | **Accessibility** | Content descriptions, contrast, TalkBack support |

---

## 🔨 Development Setup

### 📋 Prerequisites

- 🧩 **Android Studio** Hedgehog or later
- ☕ **JDK 17**
- 📦 **Android SDK 34**

### ▶️ Run a debug build

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or open the project in **Android Studio** and press ▶️ **Run**.

---

## 🎨 Coding Standards

- 🧑‍💻 **Language:** Kotlin, official code style (`kotlin.code.style=official`)
- 🏛️ **Architecture:** MVVM — keep UI, ViewModel, and data layers separated
- 🎨 **UI:** Jetpack Compose + Material 3 components
- 🧱 **State:** `StateFlow` / `MutableStateFlow` in ViewModels; collect with lifecycle awareness
- 🗄️ **Data:** Room for persistence, DataStore for preferences
- 🧹 **Clean code:**
  - Descriptive names for functions, variables, and composables
  - Small, focused, reusable composables
  - No hard-coded strings/colors in UI — use resources / theme tokens
  - Add KDoc comments for non-obvious logic
- ♿ **Accessibility:** provide `contentDescription` for icons/images and maintain good contrast

> ✅ Before opening a PR, make sure the project **builds cleanly**:
> ```bash
> ./gradlew assembleDebug
> ```

---

## 🧩 Project Structure

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

## 🔀 Pull Request Workflow

1. 🌿 Branch from `main`:
   ```bash
   git checkout -b feature/your-feature
   ```
2. 🛠️ Make your changes with **clear, atomic commits**:
   ```bash
   git commit -m "Add savings goal reminder"
   ```
3. 🔁 Keep your branch up to date:
   ```bash
   git pull --rebase origin main
   ```
4. 🚀 Push and open a PR:
   ```bash
   git push origin feature/your-feature
   ```
5. 📝 In the PR description, include:
   - **What** changed and **why**
   - **Screenshots / screen recordings** for UI changes
   - How you **tested** it
   - Any **breaking changes** or follow-ups

### ✅ Commit message style

Keep it short and descriptive. Conventional prefixes are welcome:

```
feat:  add multi-currency support
fix:   correct budget percentage calculation
docs:  update README installation steps
style: reformat analytics screen
refactor: extract donut chart composable
test:  add expense repository tests
```

---

## 🐛 Reporting Bugs

Open an issue with:

- 📱 Device + Android version
- 🔢 App version
- 🧭 Steps to reproduce
- ✅ Expected vs ❌ actual behavior
- 🖼️ Screenshots / logs if available

---

## 💡 Suggesting Features

Open an issue describing:

- 🎯 The problem your idea solves
- 🧩 How you imagine it working
- 🌟 Why it benefits Solo Ledger users

---

## 🔐 Release Signing & Secrets

Solo Ledger publishes a **signed release APK**. Signing credentials are **never committed** to the repository — they live either in a local, git-ignored `keystore.properties` file, or (for CI) in **GitHub Actions secrets**.

> ⚠️ **Golden rule:** never commit a keystore (`*.p12`, `*.jks`, `*.keystore`), `keystore.properties`, or any password. These are all listed in `.gitignore`.

### 🗝️ 1. Generating a release keystore

The project uses a **PKCS12** keystore. If you have a JDK installed, the standard tool is `keytool`:

```bash
keytool -genkeypair \
  -alias solo-ledger \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storetype PKCS12 \
  -keystore keystore/solo-ledger-release.p12 \
  -dname "CN=Solo Ledger, OU=Development, O=Solo Ledger, L=India, ST=India, C=IN"
```

No JDK? You can produce an equivalent PKCS12 keystore with **OpenSSL**:

```bash
mkdir -p keystore
openssl req -x509 -newkey rsa:2048 -sha256 -nodes \
  -keyout key.pem -out cert.pem -days 10000 \
  -subj "/CN=Solo Ledger/OU=Development/O=Solo Ledger/L=India/ST=India/C=IN"

openssl pkcs12 -export \
  -inkey key.pem -in cert.pem \
  -name "solo-ledger" \
  -out keystore/solo-ledger-release.p12

rm -f key.pem cert.pem
```

> 💡 For PKCS12 keystores, the **key password equals the store password**.

> 🛟 **Back up your keystore and password** in a safe place (e.g. a password manager).
> If you lose them, you can never ship an update signed with the same identity.

### 🏠 2. Local signed release build

1. Copy the example config:
   ```bash
   cp keystore.properties.example keystore.properties
   ```
2. Fill in your real values:
   ```properties
   storeFile=keystore/solo-ledger-release.p12
   storePassword=YOUR_STORE_PASSWORD
   keyAlias=solo-ledger
   keyPassword=YOUR_KEY_PASSWORD
   ```
3. Build:
   ```bash
   ./gradlew assembleRelease
   ```
   Output → `app/build/outputs/apk/release/app-release.apk`

The Gradle config in `app/build.gradle.kts` reads signing values from **environment variables first**, then falls back to `keystore.properties`. If neither is present, it builds an **unsigned** release APK (handy for dry runs).

### ☁️ 3. GitHub Actions secrets

The release workflow (`.github/workflows/release.yml`) is **manual-trigger only** (`workflow_dispatch`) — it never runs automatically on push or pull request. Trigger it from **GitHub → Actions → Build Signed Release APK → Run workflow**.

First, add these **repository secrets** under
**Settings → Secrets and variables → Actions → New repository secret**:

| 🔑 Secret name | Value |
|---|---|
| `KEYSTORE_BASE64` | Base64 of your keystore file (see below) |
| `KEYSTORE_PASSWORD` | Your keystore (store) password |
| `KEY_ALIAS` | `solo-ledger` |
| `KEY_PASSWORD` | Your key password (same as store password for PKCS12) |

Generate the `KEYSTORE_BASE64` value from your keystore file:

```bash
# Linux
base64 -w0 keystore/solo-ledger-release.p12

# macOS
base64 keystore/solo-ledger-release.p12
```

Copy the entire output string into the `KEYSTORE_BASE64` secret.

During the run, the workflow:
1. 🔓 Decodes `KEYSTORE_BASE64` back into a keystore file
2. 🧱 Builds `assembleRelease` using the other secrets as env vars
3. 📦 Uploads the signed APK as a build artifact
4. 🧹 Deletes the decoded keystore afterwards

> 🔒 GitHub masks secret values in logs, and the keystore file only exists on the ephemeral runner during the build.

---

## 📜 Code of Conduct

Be respectful, constructive, and inclusive. We're all here to build something great together. 💚

---

<p align="center"><sub>Thank you for helping make Solo Ledger better! 🚀</sub></p>
