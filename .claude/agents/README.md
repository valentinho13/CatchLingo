# CatchLingo Subagents — Routing & Übergabeformate

CatchLingo nutzt zwei ergänzende Agent-Gruppen: die vorhandenen Produkt-/UX-/Content-Spezialisten und sechs
klar abgegrenzte Engineering-/Craft-/Daten-/Qualitäts-Agenten. Native Android-App: Kotlin + Jetpack Compose.

## Routing-Regel

1. Bauen oder nicht bauen? Reihenfolge? Akzeptanzkriterien? → `catchlingo-product-strategist`
2. Wie soll es aussehen, klingen, sich anfühlen? → `catchlingo-ux-designer`
3. ML-Machbarkeit, Mapping, Schwellen, Diagnostik-Analyse? → `catchlingo-camera-ml-specialist`
4. Vokabeln, Kategorien, Sprachkorrektheit, Review-Inhalt? → `catchlingo-learning-content`
5. Allgemeine Kotlin-/Compose-Umsetzung außerhalb der spezialisierten Ownership → `catchlingo-android-architect`
6. Vorhandene Produktarbeit abschließend kritisch prüfen → `catchlingo-qa-test-critic`
7. Für die spezialisierten Engineering-Ownerships die Tabelle unten verwenden.
8. Kleinigkeiten (<30 min, 1–2 Dateien, klarer Auftrag): kein Agent, direkt im Hauptkontext.
9. Zwei Agents nie parallel auf dieselben Dateien.

## Engineering-Ownership

| Agent | Zuständig für | Nicht zuständig für |
|---|---|---|
| `compose-architect` | Compose-Struktur, Navigation, State-Hoisting, Feature-Architektur | Datenmodell, ML, Motion-Detail |
| `discover-vision-engineer` | CameraX, ML Kit, Kandidatenlogik, Discover-Diagnostik | Persistenz, Dictionary, UI-Darstellung |
| `learning-data-engineer` | Dictionary, Persistenz, Lernstatus, Review-Daten, Duplicate-Blocking | Erkennung, Composables, Didaktik |
| `motion-feel-engineer` | Catch-Moment, Animation, Haptik, Responsiveness | Feature-Struktur, Daten, visuelles Canon-Urteil |
| `design-guardian` | Read-only Review gegen Field-Journal, Anti-Scanner und Katzen-Canon | Codeänderungen |
| `qa-verifier` | Builds, Tests, Edge-Cases, Regressionen, ehrliches Pass/Fail | Features, Architektur- oder Designumbauten |

Die spezialisierten Engineering-Agenten ersetzen nicht Produkt-, UX- oder Content-Entscheidungen. Bei
Überlappung entscheidet die Ownership: Persistenz → `learning-data-engineer`, Erkennung →
`discover-vision-engineer`, UI-Struktur → `compose-architect`, Motion → `motion-feel-engineer`, visuelles Review →
`design-guardian`, technische Verifikation → `qa-verifier`.

## Typische Handover-Ketten

- Produktfeature: `catchlingo-product-strategist` → `catchlingo-ux-designer` → zuständiger Engineering-Agent → `qa-verifier`.
- Discover: `discover-vision-engineer` → `learning-data-engineer` (erst nach Bestätigung) → `compose-architect` → `motion-feel-engineer` → `design-guardian` → `qa-verifier`.
- Dictionary/Review: `learning-data-engineer` → `compose-architect` → `design-guardian` → `qa-verifier`.
- Kamera-/ML-Spezialanalyse: `catchlingo-camera-ml-specialist` → `discover-vision-engineer` → `qa-verifier`.

## Kernfluss-Smoke-Test

Für **Discover → Confirm → Dictionary → Review** gilt:

1. `discover-vision-engineer` liefert ehrliche Kandidaten und Zustände, speichert aber nichts.
2. `learning-data-engineer` übernimmt erst bestätigte Kandidaten und besitzt Persistenz/Events.
3. `compose-architect` baut UI-/State-Hüllen ohne Persistenz- oder ML-Logik.
4. `motion-feel-engineer` verantwortet Catch-Moment und Transitions ohne Datenlogik.
5. `design-guardian` prüft read-only gegen Designvision und Katzen-Canon.
6. `qa-verifier` führt echte Builds/Tests aus und meldet Pass/Fail.

## Briefing-Template

```text
Ziel: <1 Satz, beobachtbares Ergebnis>
Kontext: <was gilt bereits als entschieden>
Dateien: <konkrete Pfade oder Suchbereich>
Grenzen: <was nicht angefasst werden darf>
Output: <benötigtes Ergebnisformat>
```

## Handover-Template

```text
Ergebnis: <1–2 Sätze>
Geänderte Dateien: <Liste oder "keine">
Verifikation: <Tests/Build und Ergebnis>
Risiken: <maximal 3, konkret>
Nächste Schritte: <maximal 3>
Offene Fragen: <nur echte Blocker>
```

## Konventionen

- Zielpaket und Namespace: `de.valentinho13.catchlingo`.
- Build-JDK: `C:\Program Files\Android\Android Studio\jbr`.
- Keine Fake-Features, Fake-Daten oder erfundenen ML-Ergebnisse; speichern erst nach Bestätigung.
- Keine Flutter-Annahmen: Dieses Repository ist ausschließlich Kotlin/Compose.
- Motion/Haptik über `CatchLingoMotion` / `CatchLingoHaptics`, nicht ad hoc.
- Designvision ist bindend; Konsistenz schlägt Kreativität.
- Kein Commit oder Push ohne Auftrag.
