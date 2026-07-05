# CatchLingo Subagents – Routing & Briefing

Sechs Opus-Subagents, je eine abgegrenzte **Engineering-/Craft-/Daten-/Qualitäts**-Disziplin. Sie halten den
Hauptkontext schlank: **Domänenwissen lebt hier, nicht in `CLAUDE.md`** – es kostet nur Tokens, wenn der
jeweilige Agent tatsächlich läuft. Produkturteil, Richtung und Kritik bleiben **außerhalb** der Subagents (Skills).

## Wann welcher Agent

| Agent | Zuständig für | Nicht zuständig für |
|---|---|---|
| **compose-architect** | Compose-Struktur, Feature-Architektur, Navigation, State-Hoisting, Paketmigration | Datenmodell, Motion-Detail, ML, visuelles Urteil |
| **discover-vision-engineer** | CameraX, ML Kit, On-device-Erkennung, Kandidatenlogik, Discover-Diagnostik | Speichern/Dictionary, Darstellung, Sprach-Scope |
| **learning-data-engineer** | Dictionary, Persistenz, Lernstatus, Review-Daten, Korrekturen, Duplicate-Blocking | Erkennung, UI/Composables, Didaktik-Scope |
| **motion-feel-engineer** | Catch-Moment, Animation, Haptik, Responsiveness, emotionale Signatur | Feature-Struktur, Farb-/Identitätsurteil, Daten |
| **design-guardian** *(read-only)* | Designprüfung gegen Field-Journal-Ästhetik, Maskottchen-Canon, Anti-Scanner | Code ändern, emotionale Delight-Bewertung |
| **qa-verifier** | Build, Tests, Edge-Cases, Regressionen, ehrliches Pass/Fail | Features/Refactors bauen, visuelles/Feel-Urteil |

## Routing-Beispiele

| Aufgabe | Primärer Agent | Optionaler Review-Agent |
|---|---|---|
| Neuer Compose-Screen | `compose-architect` | `design-guardian`, `qa-verifier` |
| CameraX/ML Kit einführen | `discover-vision-engineer` | `qa-verifier`, `design-guardian` |
| Dictionary-Persistenz bauen | `learning-data-engineer` | `qa-verifier` |
| Review-Scheduling entwerfen | `learning-data-engineer` | `compose-architect`, `qa-verifier` |
| Catch-Animation verbessern | `motion-feel-engineer` | `design-guardian`, `qa-verifier` |
| UI gegen CatchLingo-Gefühl prüfen | `design-guardian` | keiner |
| Build-/Regression prüfen | `qa-verifier` | keiner |

## Sinnvolle Kombinationen (Handover-Ketten)
- **Neuer Screen:** `compose-architect` (Struktur) → `motion-feel-engineer` (Feel) → `design-guardian` (Review) → `qa-verifier` (Verifikation).
- **Discover-Feature:** `discover-vision-engineer` (Kandidaten/Zustände) → `learning-data-engineer` (Persistenz erst nach Bestätigung) → `compose-architect`/`motion-feel-engineer` (Darstellung/Catch) → `qa-verifier`.
- **Review-Feature:** `learning-data-engineer` (Datenmodell/Scheduling zuerst) → `compose-architect` (UI) → `qa-verifier`.

## Nicht gleichzeitig dieselbe Aufgabe
- **Persistenz/Datenmodell** gehört **nur** `learning-data-engineer` – nicht in `compose-architect`-ViewModels oder `discover-vision-engineer`-Pipeline einbauen.
- **Erkennung** gehört **nur** `discover-vision-engineer`; er speichert nichts dauerhaft.
- **Code-Änderungen an UI** macht `compose-architect`/`motion-feel-engineer`; `design-guardian` bleibt read-only (nur Fixes vorschlagen).
- **Verifikation** macht `qa-verifier`; andere Agents markieren "Test nötig", übernehmen die Abnahme aber nicht selbst.

## Smoke-Test für Agent-Routing
Konkreter Trockenlauf entlang des Kernflusses **Discover → Confirm → Dictionary → Review** – so muss die
Arbeit getrennt laufen (jeder Schritt ein eigener Agent, sauberes Handover):
1. **`discover-vision-engineer`** definiert Kandidaten + Zustände (`searching → candidate → …`) – **speichert nichts**.
2. **`learning-data-engineer`** definiert Persistenz/Events – **erst ab bestätigtem Kandidaten** (`ConfirmationEvent` → `Word`).
3. **`compose-architect`** baut nur UI-/State-Hüllen – **keine** Persistenz-, ML- oder Motion-Logik.
4. **`motion-feel-engineer`** definiert Catch-Moment/Transitions – **ohne** Datenlogik.
5. **`design-guardian`** prüft read-only gegen Field-Journal, Anti-Scanner und Katzen-Canon.
6. **`qa-verifier`** führt Build/Tests aus und meldet Pass/Fail.

**Negativbeispiel (so NICHT):**
- ❌ Falsch: `compose-architect` baut in einem Schritt Screen **+** ViewModel **+** Room-Entity **+** ML-Kandidaten **+** Testfixes.
- ✅ Richtig: Aufgabe über die Handover-Kette trennen – jeder Agent nur sein Ownership-Feld, Review-Agent erst danach.

Nach Änderungen an den Agents selbst: diesen Smoke-Test gedanklich einmal durchspielen – landet jede Teilaufgabe
beim richtigen Agent, ohne dass einer „alles auf einmal" macht?

## Handover-Template (beim Delegieren mitgeben)
1. **Ziel** in einem Satz + Definition of Done.
2. **Kontext/Constraints:** relevante Datei:Zeile, betroffener Fluss-Zustand (erkannt/bestätigt/…), Design-/Motion-Regel.
3. **Was NICHT** angefasst werden soll (Scope-Grenze).
4. **Erwartete Rückgabe:** knapp, geerdet, mit offenen Risiken und "Verifikation nötig?".

## Bewusst KEINE Subagents (durch Skills / externe Arbeitsweise abgedeckt)
- Produkt/Roadmap/Scope → Skill **catchlingo-product-lead**
- Emotionale Delight/Magie → Skill **catchlingo-experience-designer**
- Erst-Nutzer-Klarheit/Onboarding → Skill **catchlingo-first-time-user-critic**
- UX-Philosophie-Filter → Skill **valentin-product-dna**
- Allgemeine Codebase-Recherche → eingebauter **Explore**-Agent (kein eigener „Scout")
- Autonomer Sprint → Skill **catchlingo-sprint** (Achtung: Flutter-orientiert; dieses Repo ist Kotlin/Compose)

## Konventionen (für alle Agents)
- Modell: **Opus**. Fokussierter Prompt, knappe geerdete Rückgabe – kein Datei-Dump, keine Essays.
- Build: `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`. Zielpaket: `de.valentinho13.catchlingo` (Migration von `com.example.*` läuft).
- **Keine Fake-Features, keine Fake-Daten, keine erfundenen ML-Ergebnisse, keine Flutter-Annahmen.**
- **Konsistenz schlägt Kreativität; Designvision hat Vorrang vor Technik.** Kein Commit/Push ohne Auftrag.
