# DoodleMatch

DoodleMatch is an Android drawing-to-memory-card game for children ages 4–9. A child creates a drawing, then uses that drawing alongside official artwork in a memory-match experience.

## Product baseline
- Android only
- Kotlin + Jetpack Compose + Material 3
- Portrait-first, no-scroll Home screen for ~360×640dp phones
- Home mode selector: Draw / Match
- Categories: Animals, Dinosaurs, Vehicles, Fruits, Space, ABC & Numbers
- Category taps are ignored until a mode is selected
- Parent Zone: long-press the small Home control for about 1 second
- Navigation: Home → Draw/Match(categoryId) → Stickers / Parent Zone

## Phase 1 — foundation
- App theme and navigation
- Responsive Home screen
- Six-category model
- Parent Zone gesture
- Android CI workflow

## Phase 2 — drawing workflow
- Five drawable objects per category
- Object selector
- Touch drawing canvas
- Semi-transparent trace guide placeholder
- Five-color brush palette
- Thin/thick brush controls
- Undo and clear
- Per-object local persistence under app-private storage
- Saved drawings reload automatically when an object is revisited

The final Canva artwork will replace the current emoji/letter trace-guide placeholders without changing the persistence model.

## Next phase
Build Match gameplay using saved child drawings as one side of each memory pair, then add scoring, stickers, and Parent Zone progress reporting.

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
