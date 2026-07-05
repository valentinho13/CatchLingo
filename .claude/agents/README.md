# CatchLingo Sub-Agents — Routing & Übergabeformate

## Routing-Regel

1. Bauen oder nicht bauen? Reihenfolge? Akzeptanzkriterien? → `catchlingo-product-strategist`
2. Wie soll es aussehen, klingen, sich anfühlen? → `catchlingo-ux-designer` (liefert Spec)
3. Kann die Erkennung das? Mapping, Schwellen, Diagnostik-Analyse? → `catchlingo-camera-ml-specialist`
4. Vokabeln, Kategorien, Sprachkorrektheit, Review-Logik-Inhalt? → `catchlingo-learning-content`
5. Umsetzung in Kotlin/Compose (alles außer ML-Pipeline)? → `catchlingo-android-architect`
6. Fertig gebaut oder vor Merge? → `catchlingo-qa-test-critic`
7. Typische Kette für ein Feature: Strategist → UX-Spec → (ML-Machbarkeit falls Kamera) → Architect → QA.
8. Kleinigkeiten (<30 min, 1–2 Dateien, klarer Auftrag): kein Agent, direkt im Hauptkontext.
9. Produkt-Sparring/Diskussion im Hauptkontext → Skill `catchlingo-product-lead`; ausgelagerte Analyse mit Repo-Zugriff → Agent `catchlingo-product-strategist`.
10. Zwei Agents nie parallel auf dieselben Dateien.
11. Jeder Auftrag nutzt das Briefing-Template, jede Antwort das Handover-Template.

## Briefing-Template (Auftrag an einen Agent)

```
Ziel: <1 Satz, beobachtbares Ergebnis>
Kontext: <2–3 Sätze: was gilt schon als entschieden>
Dateien: <konkrete Pfade oder "selbst suchen in feature/X">
Grenzen: <was NICHT angefasst werden darf>
Output: <welcher Abschnitt des Agent-Output-Formats gebraucht wird>
```

## Handover-Template (Antwort eines Agents)

```
Ergebnis: <1–2 Sätze>
Geänderte Dateien: <Liste oder "keine">
Verifikation: <Tests/Build gelaufen? Ergebnis>
Risiken: <max. 3, konkret>
Nächste Schritte: <max. 3>
Offene Fragen: <nur echte Blocker>
```
