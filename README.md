# DoodleMatch

DoodleMatch is an Android drawing-to-memory-card game for children ages 4–9. A child creates a drawing, then uses that drawing alongside official artwork in a memory-match experience.

## Product baseline
- Android only
- Kotlin + Jetpack Compose + Material 3
- Portrait-first, no-scroll Home screen for ~360×640dp phones
- Home mode selector: Draw / Match
- Categories: Animals, Dinosaurs, Vehicles, Fruits, Space, ABC & Numbers
- Category taps are ignored until a mode is selected
- Sticker Book shortcut is available from Home
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

## Phase 3 — Match gameplay
- Real memory-match gameplay: for each completed object, one card shows the child's own saved drawing and one card shows the category's official emoji/letter placeholder.
- Availability rule: 0–1 completed drawings → Match unavailable; 2–4 → dynamic board; 5 → full 10-card board.
- Native Compose card-flip animation, match pulse, and haptic feedback.
- Child-friendly completion overlay with first-completion sticker reward.
- Per-category Match progress persisted locally: completed games, best attempts, best time, sticker state, and reward timestamp.
- Pure Kotlin game logic and tests.

## Phase 4 — rewards and Parent Zone
- Full Sticker Book UI with six category rewards.
- Locked/unlocked sticker states use the Phase 3 reward source of truth; no duplicate reward database is introduced.
- Home includes a direct Sticker Book shortcut while Parent Zone remains protected by the long-press gesture.
- Parent Zone dashboard aggregates locally stored Draw and Match activity.
- Overview metrics: saved drawings, completed Match categories, unlocked stickers, and total completed games.
- Per-category detail: drawing completion, completed games, best attempts, best time, and reward state.
- Parent-facing activity narrative uses observable in-app activity only and explicitly avoids developmental, educational, or medical conclusions.
- Pure Kotlin progress aggregation and sticker-catalog tests.

## Next phase
Production UX and content polish: final Canva artwork, sticker art, mascot assets, audio/haptic settings, accessibility refinement, and broader device/layout testing.

## Toolchain
- Android Studio Koala or newer
- JDK 17
- Android Gradle Plugin 8.5.2
- Kotlin 1.9.24
- minSdk 24

## Build
Open the project in Android Studio and sync Gradle, or run with Gradle 8.7+:

```bash
gradle :app:testDebugUnitTest
gradle :app:assembleDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/`. GitHub Actions also retains successful debug APKs as the `DoodleMatch-debug` artifact.
