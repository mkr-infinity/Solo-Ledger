# Solo Ledger

Solo Ledger is an Android budget tracking app built with Jetpack Compose, Room, and Hilt.

## What matters now

- Builds **debug APK only** through GitHub Actions
- Uses local Room database for expenses, categories, and app settings
- Includes Home, History, Reports, Profile, Settings, Trash, and Quick Add flows
- Supports theme switching and Quick Add field visibility toggles

## Build output (GitHub Actions)

The workflow builds this artifact:

- `app/build/outputs/apk/debug/app-debug.apk`

Workflow file:

- `.github/workflows/build.yml`

## Core stack

- Kotlin
- Jetpack Compose + Material 3
- Room
- Hilt
- Coroutines / Flow
- Navigation Compose

## Project layout

```text
app/src/main/java/com/kaif/ledger/
  data/
  di/
  ui/
  utils/
  viewmodel/
```

## Notes

- CI is configured for JDK 17.
- Gradle wrapper and plugin repositories are configured for CI plugin resolution.
