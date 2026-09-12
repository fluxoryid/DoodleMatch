# DoodleMatch Asset Contract

The Phase 1 UI intentionally uses placeholder symbols and flat-color cards so the application can compile before final artwork is ready.

## Visual direction
Warm, rounded, child-friendly light theme. Core palette:
- Sky blue `#64C7F5`
- Sunshine `#FFD86B`
- Coral `#FF9B8B`
- Mint `#8DDEC5`
- Lavender `#C9B6F4`
- Warm cream `#FFF9EF`
- Navy `#213354`

Avoid harsh red/error styling in child-facing surfaces.

## Final artwork slots
When final Canva assets are ready, export transparent PNG or WebP artwork for these logical slots:
- `mascot_doodlematch`
- `category_animals`
- `category_dinosaurs`
- `category_vehicles`
- `category_fruits`
- `category_space`
- `category_abc_numbers`

Recommended master export: 1024×1024 px for mascot/category artwork, then generate Android density variants during asset integration.

## Phase 2 drawing assets
Each category will contain five objects. Every object should ultimately support:
1. Official full-color art
2. Trace-guide/outline art
3. Memory-card crop
4. Optional reward sticker art

Keep filenames lowercase snake_case so resource replacement is deterministic.
