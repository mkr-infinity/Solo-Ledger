# Solo Ledger - APK Build Guide

A beginner-friendly guide to building the Solo Ledger APK.

## Prerequisites

1. **Install Android Studio**: Download from https://developer.android.com/studio
2. **Install JDK 8+**: Usually bundled with Android Studio
3. **Install Android SDK 34**: Via Android Studio SDK Manager

## Steps to Build Debug APK

### 1. Clone / Download the Project

```bash
git clone https://github.com/mkr-infinity/Solo-Ledger.git
cd Solo-Ledger
```

### 2. Open in Android Studio

- Launch Android Studio
- Click **File → Open**
- Select the `Solo-Ledger` folder
- Wait for Gradle sync to complete

### 3. Build Debug APK

**Option A – Android Studio:**
- Menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
- APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

**Option B – Terminal:**
```bash
# On Linux/macOS
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

## Steps to Build Release APK

### 1. Generate a Keystore (one-time)

```bash
keytool -genkey -v -keystore solo-ledger-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias solo-ledger
```

Follow the prompts and remember your passwords.

### 2. Configure Signing in build.gradle

Add to `app/build.gradle` under `android { ... }`:

```groovy
signingConfigs {
    release {
        storeFile file("path/to/solo-ledger-key.jks")
        storePassword "YOUR_STORE_PASSWORD"
        keyAlias "solo-ledger"
        keyPassword "YOUR_KEY_PASSWORD"
    }
}
buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

> ⚠️ **NEVER commit your keystore or passwords to git!**

### 3. Build Release APK

```bash
./gradlew assembleRelease
```

APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Install on Device

```bash
# Enable USB Debugging on your phone first
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

| Issue | Solution |
|---|---|
| Gradle sync fails | Check internet, try `File → Invalidate Caches` |
| SDK not found | Install SDK 34 via SDK Manager |
| Build error | Check `Build → Rebuild Project` |
| KSP error | Ensure KSP version matches Kotlin version |

## Notes

- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- All data is stored locally; no internet required
