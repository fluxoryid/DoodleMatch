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

## Phase 3 — Match gameplay
- Real memory-match gameplay: for each completed object, one card shows the child's own saved
  drawing (replayed from the same stroke data Draw already persists) and one card shows the
  category's official emoji/letter placeholder — the child matches their own drawing to the
  official representation, not two identical cards.
- Availability rule (centralized in `MatchAvailabilityRules`): 0-1 completed drawings -> Match
  unavailable with a friendly prompt to go draw; 2-4 -> board built from whatever's completed;
  5 -> full 10-card board.
- Card flip animation (Y-axis rotation, ~350ms), a subtle scale pulse on a successful match, and
  haptic feedback on tap — all native Compose APIs, no new animation library.
- Fixed 2-column, no-scroll grid; completion overlay with supportive messaging (never a raw
  score), a "New sticker unlocked!" banner on a category's first-ever completion, and Play
  Again / Stickers / Home actions.
- Per-category progress (times played, best attempts, best time, sticker-unlocked state)
  persisted the same way Draw already persists drawings — a plain JSON file per category, no
  new dependency, no Room (declared but unused in this project; not introduced just because
  it's on the classpath).
- Game logic (card selection/matching rules, board generation, and the reward-once rule) lives
  in plain Kotlin with no Android dependency, so it's unit-testable without an emulator.

## Next phase
Sticker collection UI, Parent Zone progress reporting, and category completion history.

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
gradle :app:testDebugUnitTest
```

The debug APK will be generated under `app/build/outputs/apk/debug/`.
