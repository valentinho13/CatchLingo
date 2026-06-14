# CatchLingo

CatchLingo ist der native Android-Neustart der App in Kotlin und Jetpack Compose.

Die App verwandelt die echte Welt in eine persoenliche Vokabelquelle: Nutzer richten die Kamera auf
Dinge um sich herum, CatchLingo bemerkt nuetzliche Woerter, zieht sie mit der verbindlichen
Magnet-/Staubsauger-Mechanik herein und bewahrt sie als kleine, schoene Funde.

**Single Source of Truth:** [PROJECT_VISION.md](PROJECT_VISION.md). Bei jedem Widerspruch gewinnt
dieses Dokument.

## Plattform

- Android-first
- Kotlin + Jetpack Compose
- Single-Activity Compose-App
- Echte Kamera als Herzstueck der Erfahrung
- Mock-Erkennung nur als austauschbares Geruest bis zur echten Recognition-Pipeline

## Produktkern

- Core Loop: Discover -> Catch -> Remember
- Einzige Fangmechanik: Magnet/Staubsauger -> gehaltene Specimen-Karte
- Kein Tap-to-Catch
- Kein Lasso-/Cowboy-Bild
- Kein dunkles Scanner-HUD, keine Debug-/Confidence-Overlays
- Warmes Feldjournal-Gefuehl mit Cream, Gruen, Amber und ruhiger Haptik

## Designreferenzen

Die verbindlichen visuellen Zielbilder liegen unter `docs/design/`:

- `app_icon_reference.jpg` - Markenbasis mit orangefarbener Begleiter-Katze
- `catchlingo_ui_vision_v1.png` - Zielbild fuer Aufbau, Stimmung und Designsprache der App

Neue UI soll diese Designsprache konsequent fortfuehren: warm, hochwertig, organisch, ruhig,
emotional und klar. Konsistenz hat Vorrang vor neuen visuellen Richtungen.
Bei funktional gleichwertigen Loesungen gewinnt die visuell begeisternde Variante, solange
Bedienbarkeit und Performance nicht leiden.

## Dokumentation

Der Dokumentationsindex liegt in [docs/README.md](docs/README.md).

Historische Flutter-Dateien wurden nicht portiert. Das alte Projekt ist nur Archiv, Referenz und
Materiallager.
