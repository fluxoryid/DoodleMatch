# DoodleMatch

DoodleMatch is an Android drawing-to-memory-card game for children ages 4–9. A child creates a drawing, then uses that drawing alongside official artwork in a memory-match experience.

## Product baseline
- Android only
- Kotlin + Jetpack Compose + Material 3
- Portrait-first, no-scroll Home screen for ~360×640dp phones
- Home mode selector: Draw / Match
- Categories: Animals, Dinosaurs, Vehicles, Fruits, Space, ABC & Numbers
- Category taps are ignored until a mode is selected
- Locked categories are modeled explicitly and remain non-interactive
- Parent Zone: long-press the small Home control for about 1 second
- Navigation: Home → Draw/Match(categoryId) → Stickers / Parent Zone

## Phase 1
This repository contains the first runnable application scaffold: theme, navigation, responsive Home screen, category model, placeholder Draw/Match/Stickers/Parent Zone screens, and CI build workflow.

## Phase 2
- Five objects per category
- Trace-guide artwork
- Drawing canvas and persistence
- Use child drawings as memory-card faces
- Stickers/reward loop
- Parent progress reporting

## Toolchain
- Android Studio Koala or newer
- JDK 17
- Android Gradle Plugin 8.5.2
- Kotlin 1.9.24
- minSdk 24

## Build
Open the project in Android Studio and sync Gradle, or run with Gradle 8.7+:

```bash
gradle :app:assembleDebug
```

The debug APK will be generated under `app/build/outputs/apk/debug/`.
