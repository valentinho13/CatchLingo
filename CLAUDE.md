# CatchLingo

Android-App (Kotlin + Jetpack Compose): Nutzer entdecken reale Gegenstände mit der
Kamera und lernen daraus indonesische Vokabeln. Feld-Journal-Gefühl — warm, ruhig,
hochwertig. Kein Scanner, kein HUD, keine Tech-Demo.

## Invarianten (nie verletzen)

- Keine Fake-Erkennung: ML-Ergebnisse nie erfinden, glätten oder vortäuschen.
- Kein Tap-to-Catch als Hauptmechanik.
- Catch folgt dem Magnet/Suction-Prinzip: erkannte Objekte werden eingesogen.
- Unsichere Erkennung → Confirm-Card mit 3–4 Kandidaten + „Nicht dabei".
- Die Design-Vision ist bindend; Konsistenz schlägt Kreativität.

## Build & Test

- Kompilieren: `./gradlew.bat :app:compileDebugKotlin`
- Unit-Tests: `./gradlew.bat :app:testDebugUnitTest`
- (Bash-Tool: `./gradlew.bat` — PowerShell: `.\gradlew.bat`)

## Struktur

- `app/` Navigation · `data/` Persistenz · `designsystem/` Theme/Motion/Haptik ·
  `feature/{discover,dictionary,review}/`
- Motion und Haptik nur über `CatchLingoMotion` / `CatchLingoHaptics`, nie ad hoc.

## Verweise

- Agent-Routing & Übergabeformate: `.claude/agents/README.md`
- Design-Vision: `docs/reference/DESIGN_VISION.md`
- Motion: `docs/design/MOTION_PLAYBOOK.md`

## Arbeitsregel

Spezialarbeit (Produkt, UX, ML, Content, Umsetzung, QA) an den passenden Subagent
delegieren — Routing steht in `.claude/agents/README.md`. Kleinigkeiten mit klarem
Auftrag direkt im Hauptkontext erledigen.
