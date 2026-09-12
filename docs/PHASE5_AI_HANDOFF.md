# DoodleMatch Phase 5 AI Handoff

This file defines the responsibilities and prompts for Claude, Canva, and Gemini during Phase 5. GitHub remains the source of truth.

## Operating model

- GitHub: canonical code, asset contract, tests, CI, version history.
- Canva: production artwork and visual design source.
- Claude: Android/Kotlin implementation and asset integration.
- Gemini: visual QA, prompt refinement, consistency review, and multimodal critique.

No tool should independently rename categories, object IDs, or Android asset names.

---

## Canva production brief

Use the approved Canva design `DoodleMatch Mascot & Wordmark` (design ID `DAHU-qPn-CA`) as the visual reference.

Create the full asset inventory defined in `docs/PHASE5_ASSET_CONTRACT.md`.

Visual requirements:
- children ages 4–9
- modern edutainment aesthetic
- rounded geometric vector forms
- strong but friendly navy outlines
- clean flat shapes
- readable at small mobile sizes
- playful, warm, not babyish
- no photorealism
- no copyrighted characters
- use only the locked DoodleMatch palette unless a neutral white/transparent treatment is technically required

Official object art must be visually richer than trace guides while preserving the same silhouette. Trace guides must be simplified outline versions of the official object so the child can intuitively connect the guide, their drawing, and the official Match card.

Export names must exactly match `ProductionAssetCatalog`.

---

## Claude implementation prompt

You are implementing DoodleMatch Phase 5 in repository `fluxoryid/DoodleMatch`.

Branch: `feature/phase5-production-ux-assets`
Baseline: Phase 4 on `main`, version 0.4.0.

Do not rebuild the application. Preserve the current architecture and game rules.

### Source of truth
Read and follow:
- `docs/PHASE5_ASSET_CONTRACT.md`
- `app/src/main/java/com/fluxoryid/doodlematch/domain/assets/ProductionAssetCatalog.kt`

### Objectives
1. Replace child-facing emoji/letter placeholders with real production assets when each corresponding drawable is present.
2. Preserve a graceful placeholder fallback when a production asset has not yet been supplied.
3. Keep Draw, Match, Sticker Book, Parent Zone, and persistence behavior unchanged.
4. Introduce a resource-resolution layer instead of scattering `R.drawable.*` references throughout screens.
5. Use category cover art on Home while maintaining the portrait-first no-scroll layout.
6. Use official object art on Match cards.
7. Use trace-guide assets on DrawScreen behind the child’s strokes.
8. Use production sticker art in Sticker Book.
9. Use mascot states for empty states, encouragement, and completion moments without obstructing gameplay.
10. Implement adaptive launcher icon and splash assets when supplied.
11. Add accessibility content descriptions for production art.
12. Preserve all existing Phase 3 and Phase 4 unit tests and add tests around resource-name resolution/fallback logic where possible without Android dependencies.

### Technical constraints
- Kotlin + Jetpack Compose + Material 3.
- minSdk 24.
- Do not add an image-loading library for bundled local drawables.
- Do not introduce Room, network storage, or cloud dependencies.
- Keep child data local.
- Do not add diagnostic/developmental claims to Parent Zone.
- Keep official-art mapping replaceable and centralized.

### Acceptance gates
Run:
- `gradle :app:testDebugUnitTest --stacktrace`
- `gradle :app:assembleDebug --stacktrace`

The GitHub workflow must retain `DoodleMatch-debug` after success.

Prepare a PR titled:
`Phase 5: Production UX and Visual Asset Integration`

Do not merge until CI is green.

---

## Gemini visual QA prompt

Act as a senior children’s product visual QA reviewer for DoodleMatch, an Android drawing-and-memory game for ages 4–9.

Review each supplied Canva export against the approved DoodleMatch mascot/wordmark and the Phase 5 asset contract.

For every asset, evaluate:
1. recognition at small mobile size
2. silhouette clarity
3. consistency of outline weight
4. palette compliance
5. age appropriateness for 4–9
6. visual consistency with the approved mascot/wordmark
7. risk of confusion with another object in the same category
8. traceability: whether a simplified trace-guide version can clearly correspond to the official asset
9. excessive detail that will disappear on phone screens
10. accessibility/contrast concerns

For trace guides specifically, confirm that the guide is materially simpler than official art while preserving the same defining geometry.

For category covers and stickers, check that each category is visually distinguishable without relying only on color.

Output only:
- PASS
- REVISE
- BLOCK

For REVISE or BLOCK, provide concrete edit instructions. Do not redesign the category taxonomy or rename any asset.

---

## Handoff sequence

1. Canva produces asset batch.
2. Gemini reviews batch against contract.
3. Failed assets return to Canva for revision.
4. Approved assets are exported with exact canonical filenames.
5. Claude integrates assets on the Phase 5 branch.
6. GitHub CI runs tests and APK assembly.
7. APK is reviewed on-device.
8. Only then is the Phase 5 PR merged.
