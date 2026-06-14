# Migration Notes

Stand: 2026-06-14

Das neue Repository `C:\CatchLingo` ist der offizielle Kotlin-/Jetpack-Compose-Neustart. Das alte
Flutter-Projekt `C:\src\catch_lingo` wurde nur als Archiv, Referenz und Materiallager genutzt.

## Uebernommen

- `PROJECT_VISION.md` nach Repo-Root: verbindlicher Produktvertrag und Single Source of Truth.
- `KOTLIN_REWRITE_MASTERPLAN.md` nach `docs/reference/`: technischer, untergeordneter Neustartplan.
- `DESIGN_VISION.md` nach `docs/reference/`: emotionale/visuelle Referenz, untergeordnet zur Vision.
- `welcome_cat.png` nach `app/src/main/res/drawable/`: Begleiter-Katze als Produktasset.
- `app_icon_reference.jpg` und `catchlingo_ui_vision_v1.png` nach `docs/design/`: verbindliche
  gestalterische Zielbilder fuer Markenidentitaet, Aufbau, Stimmung und UI-Richtung.
- Weitere App-Icon- und Designquellen nach `assets/reference/`: Branding-/Designreferenz.
- Indonesische Mock-Wortdaten als JSON nach `app/src/main/assets/mock_words_id.json`: nur Daten,
  kein Dart-Code und keine alte Flutter-Architektur.

## Bewusst nicht uebernommen

- Flutter-/Dart-Code aus `lib/`, `test/` und `scripts/`.
- Flutter-Konfiguration wie `pubspec.yaml`, `pubspec.lock`, `.metadata`,
  `.flutter-plugins-dependencies`, `analysis_options.yaml`.
- Plattformordner des alten Flutter-Projekts: `android/`, `ios/`, `linux/`, `macos/`, `windows/`,
  `web/`.
- Generierte oder lokale Ordner: `build/`, `.dart_tool/`, `.idea/`, Logs.
- Screenrecordings, extrahierte Frames und Installer-Dateien unter `screenshots/screenrecordings/`.
- Alte Screenshots mit Marker-, Scanner-, Tap-to-Catch- oder dunkler HUD-Richtung.
- Redirect-/Stub-Dokumente wie `DESIGN_SYSTEM.md`, `UI_SPEC.md`,
  `REAL_WORLD_DISCOVERY_CONCEPT.md`, `ROADMAP.md`, `TECHNICAL_DECISIONS.md`,
  `VISION_ALIGNMENT_REVIEW.md`, `ANIMATION_AUDIT.md`, `NEXT_SESSION.md`.
- `SKILL.md`, weil es auf den Flutter-Prototyp und Flutter-Verifikation ausgerichtet ist.
- `CHANGELOG.md`, weil es das historische Flutter-Changelog ist.

## Konservative Entscheidung

Lieber wurden wenige, klare Quellen uebernommen als historische Altlasten. Jede kuenftige Migration
aus dem Flutter-Archiv muss erneut gegen `PROJECT_VISION.md` geprueft werden.
