# DoodleMatch Phase 5 Asset Contract

This document is the canonical production contract for visual assets used by DoodleMatch Phase 5.

## Approved visual direction

The approved Canva design `DoodleMatch Mascot & Wordmark` is the visual source of truth for style consistency.

- Canva design ID: `DAHU-qPn-CA`
- Canva folder: `DoodleMatch Phase 5 Assets`
- Folder ID: `FAHU-usojQ0`

## Locked visual system

Palette:
- Sky Blue `#64C7F5`
- Sunshine `#FFD86B`
- Coral `#FF9B8B`
- Mint `#8DDEC5`
- Lavender `#C9B6F4`
- Warm Cream `#FFF9EF`
- Navy `#213354`

Style:
- rounded geometric forms
- friendly navy outlines
- flat/vector appearance
- high recognition at small sizes
- playful but not babyish
- simple silhouettes and uncluttered details
- no photorealism
- no copyrighted characters or lookalikes
- avoid harsh red child-facing UI

## Required asset inventory

### Mascot
- `mascot_doodlematch_primary`
- `mascot_doodlematch_happy`
- `mascot_doodlematch_celebrate`
- `mascot_doodlematch_encourage`

### Category covers and stickers
For each category `<category>`:
- `category_<category>`
- `sticker_<category>`

Categories:
- `animals`
- `dinosaurs`
- `vehicles`
- `fruits`
- `space`
- `abc_numbers`

### Official object art and tracing guides
For every object:
- official art: `official_<category>_<object>`
- trace guide: `trace_<category>_<object>`

Objects:

**Animals**
- elephant
- lion
- cat
- dog
- fish

**Dinosaurs**
- trex
- sauropod
- triceratops
- egg
- fossil

**Vehicles**
- car
- bus
- train
- airplane
- boat

**Fruits**
- apple
- banana
- strawberry
- grapes
- watermelon

**Space**
- rocket
- planet
- star
- moon
- alien

**ABC & Numbers**
- letter_a
- letter_b
- letter_c
- number_1
- number_2

### App shell assets
- `ic_launcher_doodlematch`
- `splash_doodlematch`

## Export requirements

Official art / category covers / stickers / mascot:
- transparent PNG or WebP where appropriate
- square master artwork preferred
- no text baked into object art unless the asset itself is a letter/number
- generous internal padding so art does not touch card edges
- preserve consistent apparent scale between objects

Trace guides:
- transparent background
- simplified outline-only interpretation of the official object
- dark navy line work based on `#213354`
- avoid tiny interior details
- tracing shape must remain recognizable on a phone screen

App icon:
- foreground designed for Android adaptive icon safe zone
- simple silhouette readable at launcher size
- no tiny type

Splash:
- mascot-centered composition
- minimal visual noise
- warm cream or compatible light background

## Integration rule

The Kotlin `ProductionAssetCatalog` in the application is the source of truth for Android resource names. Asset producers must conform to these names rather than inventing new filenames.

Do not change game object IDs to fit artwork filenames. Artwork filenames must fit the existing domain model.
