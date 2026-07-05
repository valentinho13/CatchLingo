# CatchLingo — Startkompass

Native Android-App mit Kotlin und Jetpack Compose. Nutzer entdecken reale Gegenstände mit der Kamera, bestätigen
ehrliche Erkennungskandidaten und lernen daraus indonesische Vokabeln. Die App fühlt sich wie ein warmes,
persönliches Field-Journal an — kein Scanner, HUD, Lernspiel oder Tech-Demo.

**Kernfluss:** Discover → Confirm → Dictionary → Review

## Invarianten

- Keine Fake-Erkennung, Fake-Daten oder UI-only-Scheinfeatures.
- ML-Ergebnisse nie erfinden, glätten oder vortäuschen; lieber „nichts erkannt" als raten.
- Unsichere Erkennung → Confirm-Card; speichern erst nach Bestätigung.
- Kein Tap-to-Catch als Hauptmechanismus; Catch folgt dem Magnet-/Suction-Prinzip.
- Die Designvision ist bindend; Konsistenz schlägt Kreativität.
- Keine Flutter-Annahmen: Dieses Repository ist Kotlin/Compose.

## Stack und Struktur

- Namespace und Zielpaket: `de.valentinho13.catchlingo`.
- Kotlin 2.2.10, Compose, Material 3, `minSdk 30`, `targetSdk 36`, Java 11.
- `app/` Navigation · `data/` bestehende Foundation-Daten · `dictionary/` Lern-/Persistenz-Domain ·
  `designsystem/` Theme/Motion/Haptik · `feature/{discover,dictionary,review}/`.
- Motion und Haptik nur über `CatchLingoMotion` / `CatchLingoHaptics`, nie ad hoc.

## Build und Test

- JDK: `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`.
- Kompilieren/Build: `.\gradlew.bat assembleDebug`.
- Unit-Tests: `.\gradlew.bat testDebugUnitTest`.
- Instrumentation: `.\gradlew.bat connectedDebugAndroidTest` nur mit verbundenem Gerät/Emulator.

## Subagent-Routing

Routing, Ownership und Übergabeformate stehen in `.claude/agents/README.md`. Vor Delegation den passenden
primären Agent wählen; Review-Agenten erst danach einsetzen. Produkt-, UX- und Content-Spezialisten bleiben
erhalten, ergänzt um sechs Engineering-Agenten für Compose, Discover/ML, Learning/Data, Motion, Design-Review
und technische QA.

## Verweise

- Agent-Routing: `.claude/agents/README.md`
- Designvision: `docs/reference/DESIGN_VISION.md`
- Motion-Playbook: `docs/design/MOTION_PLAYBOOK.md`
- Ausführliche Motion-Guidelines: `docs/motion/MOTION_GUIDELINES.md`
- Katzen-Canon: `docs/design/cat_character_sheet.md`

## Arbeitsregeln

- Vor Änderungen den echten Repository-Stand lesen.
- Keine großen Architektur- oder Semantikänderungen ohne knappe Begründung und Freigabe.
- Fach-Ownership respektieren: Erkennung, Persistenz, UI, Motion, Designreview und QA nicht vermischen.
- Kein Commit oder Push ohne Auftrag.
