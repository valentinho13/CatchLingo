# CatchLingo — Startkompass

Native Android-App. **Kotlin + Jetpack Compose** (kein Flutter). Diese Datei ist bewusst minimal:
ein Startkompass, kein Wissensfriedhof. Domänenwissen lebt in den Subagents und Skills, nicht hier.

## Produktprinzip
CatchLingo fühlt sich an wie ein **warmes, persönliches Field-Journal** – kein Scanner, kein Lernspiel,
kein Duolingo-Klon. Dinge in der Welt entdecken, sanft „catchen", bestätigen, sammeln, wiederholen, lernen.

**Kernfluss:** Discover → Confirm → Dictionary → Review

## Stack & Struktur
- Kotlin 2.2.10, Compose BOM 2026.02.01, Material3, `minSdk 30`, `targetSdk 36`, Java 11.
- Feature-basierte Struktur unter `de.valentinho13.catchlingo/feature/<name>/`.
- **Paket-Migration läuft:** Gradle-`namespace` noch `com.example.catchlingo`; neuer Code gehört unter
  `de.valentinho13.catchlingo`. `com.example.*` ist Altlast, kein Vorbild.

## Build
- `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"` setzen, sonst findet `gradlew` kein Java.
- Unit-Tests: `./gradlew testDebugUnitTest`. UI-Tests (instrumentiert): `./gradlew connectedDebugAndroidTest` (nur mit Gerät/Emulator).

## Subagent-Routing
Sechs Opus-Subagents unter `.claude/agents/` — Zuständigkeiten, Routing-Tabelle und Handover-Ketten in
[.claude/agents/README.md](.claude/agents/README.md). Produkturteil/Kritik bleibt in den Skills, nicht in Subagents.
- Bei mehrdeutigen Aufgaben **zuerst Routing** anhand der README wählen, dann **nur den passenden primären Agent** starten.
- Review-Agent (z. B. `design-guardian`/`qa-verifier`) erst **danach**, nicht unnötig parallel mehrere Agents werfen.

## Grundregeln
- **Keine Fake-Features.** Kein UI-only-Schein, der Funktion vortäuscht.
- **Keine Fake-Daten.** Keine erfundenen Wörter/Übersetzungen als echte Daten.
- **Keine erfundenen ML-Ergebnisse.** Lieber „nichts erkannt" als geraten. Speichern erst nach Bestätigung.
- **Keine Flutter-Annahmen.** Dieses Repo ist Kotlin/Compose.
- **Keine großen Architekturänderungen ohne knappe Begründung.** Kein Commit/Push ohne Auftrag.
- **Designvision hat Vorrang vor Technik; Konsistenz schlägt Kreativität.**
