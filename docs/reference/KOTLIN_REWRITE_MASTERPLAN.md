# CatchLingo — Masterplan für die Kotlin / Jetpack-Compose-Neuentwicklung

> **Zweck:** Dieses Dokument ist die vollständige Analyse des bestehenden Flutter-Prototyps
> und der Masterplan für einen sauberen Neuaufbau in Kotlin + Jetpack Compose.
> Es enthält bewusst **keinen** Produktionscode — nur Analyse, Entscheidungen und Architektur.
>
> **Analysebasis:** gesamter `lib/`-Code, native `MainActivity.kt`, Android-Konfiguration,
> alle 16 Markdown-Dokumente, Tests, Assets, die UI-Vision-Grafik und die aufgezeichneten
> Animationsversuche (`screenshots/screenrecordings/`).
>
> **Haltung:** Senior Android Engineer + Product Designer. Bestehende Entscheidungen werden
> kritisch hinterfragt. Optimiert wird kompromisslos auf das Benutzererlebnis und auf eine
> App, die sich außergewöhnlich hochwertig anfühlt.
>
> Stand: 2026-06-13
>
> **Übergeordnet:** [PROJECT_VISION.md](PROJECT_VISION.md) ist die einzige verbindliche Quelle der
> Wahrheit. Dieser Masterplan ist die untergeordnete technische Umsetzung und bleibt dazu konsistent;
> bei Widerspruch gewinnt `PROJECT_VISION.md`.

---

## 0. Executive Summary

**CatchLingo ist eine gute, klare Produktidee mit einer starken, bereits ausformulierten
Identität — aber der eine Moment, der die App ausmacht, lässt sich in Flutter nicht in der
gewünschten Qualität bauen.** Genau dieser Moment ist der Grund für den Wechsel.

Die wichtigsten Befunde:

1. **Die Vision ist exzellent dokumentiert und sollte zu ~90 % übernommen werden.** Warmes
   Feldjournal, „die Welt wird zur Vokabelquelle", Discover → Catch → Remember, Specimen-Karte,
   Begleiter-Katze, grün/amber/cream. Das ist tragfähig und differenzierend. Nicht neu erfinden.

2. **Die „Magnet-/Staubsauger"-Mechanik ist kein neuer Wunsch, sondern der ursprüngliche
   North Star.** `VISION.md`, `SKILL.md` und das aktuelle Explore-Verhalten beschreiben alle
   automatisches Einsammeln. „Tap to Catch" war ein Umweg. Deine Entscheidung, zur Vacuum-Mechanik
   zurückzukehren, ist **konsistent mit dem dokumentierten Produktkern** — `NEXT_SESSION.md`
   (und meine alte Notiz „tap, not vacuum") sind dadurch überholt.

3. **Die Einsaug-Animation ist bereits dreimal in Flutter gescheitert.** Im Repo liegen
   `bad_sucking_animation.mp4`, `bad_bubble_animation.mp4` und `bad_bubble_animationv2.mp4` —
   drei aufgezeichnete, als unbefriedigend markierte Versuche. **Der identitätsstiftende
   Moment ist exakt das, was Flutters Widget-Animationsmodell nicht überzeugend liefert.**
   Das ist die stärkste sachliche Begründung für den Plattformwechsel.

4. **Die eigentliche Produktpremisse ist noch gar nicht gebaut.** Es gibt **keine echte
   Objekterkennung** und **keine echte Übersetzung** — beides ist hartkodiert in
   `mock_catch_words.dart` (56 Indonesisch-Wörter an festen Bildpositionen). Der „Sprach-Layer"
   und der „Real-World-Layer" sind beide simuliert. Das ist die größte ungelöste Aufgabe,
   unabhängig von der Plattform.

5. **Architektur & Persistenz sind Prototyp-Niveau.** State wird bei jeder Navigation neu von
   der Platte geladen (`FutureBuilder` + `SharedPreferences`), es gibt keine zentrale Quelle der
   Wahrheit, und die Collection liegt als JSON-Blob in den Prefs mit defensivem Doppel-Schreiben.
   In Compose wird das durch ViewModel + Flow + Room sauber gelöst.

6. **Es liegt nennenswerter toter/veralteter Code im Repo** (alte Indigo/Lasso-Ästhetik,
   ungenutzter Counter, Easter-Egg-Username-Policy, Legacy-Vokabel-Prototyp) und **16 teils
   widersprüchliche Doku-Dateien**. Der Neuaufbau ist die Gelegenheit, radikal zu entrümpeln.

> ### Die eine Kernentscheidung
> **Baue zuerst eine einzige vertikale Scheibe: den Catch-Moment.** Kamera (echt) → ein „bemerktes"
> Wort → spektakulärer Einsaug → das Wort wird zu einer **gehaltenen Specimen-Karte** →
> frame-synchrone Haptik über native `VibrationEffect.Composition`-Primitives
> (`QUICK_RISE → SPIN → THUD`). Erst wenn sich *dieser* Moment auf dem Gerät großartig anfühlt,
> wird der Rest gebaut. Alles andere (Dictionary, Review, Stats) ist vergleichsweise gelöstes Handwerk.
> Wenn dieser Moment nicht zündet, hat die App keinen Grund zu existieren.

---

## 1. Zusammenfassung des aktuellen Projekts

### 1.1 Was CatchLingo ist

Ein Android-first Flutter-MVP für **reale Vokabelentdeckung**: Man richtet die Kamera auf die
Welt, die App „bemerkt" Objekte, zeigt das Wort in der Zielsprache und sammelt es als
persönliche Erinnerung. Kernschleife: **Discover → Catch → Remember** (ausführlich:
Explore → Detect → Catch → Save → Review → Explore again). Emotionaler Kern: *„Ich habe das in
der echten Welt gesehen — jetzt kenne ich das Wort dafür."*

### 1.2 Tech-Stack (Ist-Zustand)

| Bereich | Umsetzung |
|---|---|
| Framework | Flutter / Dart, Material 3, `useMaterial3: true` |
| Kamera | `camera: ^0.12.0+1` (Flutter-Plugin) |
| Persistenz | `shared_preferences` (JSON-Blob + redundante „stable keys") |
| Haptik | **Native** `MethodChannel('catch_lingo/haptics')` → `VibrationEffect` in `MainActivity.kt` |
| State | `StatefulWidget` + `setState` + `FutureBuilder`, kein State-Management-Paket |
| DI | keine |
| Tests | `flutter_test` (3 Dateien, ~13 Widget-/Unit-Tests) |
| Erkennung/Übersetzung | **Mock** — hartkodierte Wortliste, feste Marker-Positionen |

### 1.3 Tatsächlich implementierter Funktionsumfang

- **Home** (`home_screen.dart`): warmer Einstieg, Tageszeit-Gruß, Katzen-Hero-Card,
  „Start Exploring", Statistik-Cards (caught/sightings/categories), Review-Empfehlung,
  Kategorie-Vorschau, gestaffelte Intro-Animation, Bottom-Nav.
- **Explore** (`explore_screen.dart`, ~1.790 Zeilen): echte Kamera-Preview mit Lifecycle-Handling,
  Permission-/Fallback-Pfad, bis zu 3 „bemerkte" Wörter, **automatischer Magnet-Einzug**
  (`_NoticedWordMarker` → `_magnetTimer` → Flugkurve `_magneticAttractorProgress` → Partikel-Trail
  → `onCollected`), **Specimen-Reveal-Karte** (`_CaughtWordReveal`) mit Amber-Bloom,
  Fortschrittsring oben rechts (`_SessionProgressPill`), Session-Ziel = 5, Session-Summary-Overlay,
  Dublettenschutz, „seen again"-Verstärkung.
- **Dictionary** (`dictionary_screen.dart`): Kategorie-Gruppierung, Suche, Filter-Chips,
  Wort-Detail-Bottom-Sheet (First caught / Sightings / Last seen), Specimen-Badge, Empty-State.
- **Review** (`review_screen.dart`): leichte Karteikarten, Easy/Hard-Mode, „Again" requeued,
  „gentle review" (älteste zuerst), „fading"-Markierung ab 7 Tagen, kein echtes SRS.
- **Quer**: warme Palette als Tokens (`app_theme.dart`), geteilte `WordSpecimenBadge`,
  `FadeThroughPageRoute`, Begleiter-Katze in Empty-States, adaptive App-Icons, native Haptik mit
  Flutter-Fallbacks, Launch-Background in Cream.

### 1.4 Reifegrad

Ein **funktionaler, optisch bereits warmer Prototyp** der *Hülle*. Die Schleife ist fühlbar,
das Design ist (entgegen `DESIGN_DIAGNOSIS.md`, die einen früheren Dark-Stand beschreibt)
inzwischen auf die warme Palette migriert. Aber: **die beiden Kern-Layer (Erkennung + Übersetzung)
sind Attrappe**, und die Architektur trägt nicht über den Prototyp hinaus.

### 1.5 Die drei historischen Catch-Konzepte (wichtig für das Verständnis)

Im Code sind drei Generationen sichtbar — das erklärt die Widersprüche in der Doku:

1. **Lasso/Cowboy** — `catch_lingo_background.dart` (505 Zeilen, Indigo/Mint, ein handgemalter
   Cowboy wirft ein Lasso nach schwebenden Wörtern). Vollständig verworfen, aber noch im Repo.
2. **Tap-to-Catch** — Sprint 3 in `NEXT_SESSION.md`: Marker antippen → Specimen-Karte. Als
   „nicht zurück zum Vacuum" dokumentiert.
3. **Automatischer Magnet/Vacuum** — der **aktuelle** Stand im Arbeitsbaum + oberster
   CHANGELOG-Eintrag. Wörter werden automatisch eingesaugt, dann Specimen-Reveal.

`NEXT_SESSION.md` und CHANGELOG **widersprechen sich** offen (tap vs. automatic). Auflösung:
Der dokumentierte *Langzeit*-Kern war immer automatisch (`VISION.md`, `SKILL.md`); Tap war die
Ausnahme. **Deine aktuelle Anweisung beendet diesen Zickzack-Kurs endgültig zugunsten des
ursprünglichen North Stars.**

---

## 2. Konzepte, die übernommen werden sollten (KEEP)

| Konzept | Warum behalten |
|---|---|
| **Produktvision & emotionaler Kern** (`VISION.md`, `DESIGN_VISION.md`) | Klar, differenzierend, tragfähig. „Feldjournal", nicht „Vokabel-DB". Nicht antasten. |
| **Core Loop Discover → Catch → Remember** | Sauber, fokussiert, jedes Feature lässt sich daran messen. |
| **Warme Palette als Identität** (cream `#F5F0E8`, grün `#4A7C59`, amber `#D4820A`) | Bereits durchgesetzt, emotional richtig, premium. 1:1 als Compose-Theme-Tokens übernehmen. |
| **Specimen-Karte als „gehaltenes Objekt"** | Das ist die Marken-Geste. Der Endzustand jedes Catches muss ein haltbares Objekt sein — **das** muss die neue Vacuum-Mechanik bewahren. |
| **Begleiter-Katze** (`welcome_cat.png`) | Gesicht = Bindung. Ausbauen zum reagierenden Charakter. |
| **Native Haptik-Philosophie** | Der MethodChannel beweist: Haptik ist zentral. In Kotlin wird das erstklassig (siehe §12). |
| **Dublettenschutz + „seen again"-Verstärkung** | Hält die Kamera auch ohne Neues belohnend (`SKILL.md`-Anforderung). Hartes Muss. |
| **Sanftes Review ohne Scham** (kein hartes SRS, „fading" statt „falsch") | Passt zur Anti-Homework-Regel. Beibehalten, später leichtes SRS dahinter. |
| **„Wo & wann" als Erinnerungs-Idee** | Vision-Kern — aber noch nicht umgesetzt (siehe §3/§6). Konzept behalten, Datenmodell endlich liefern. |
| **App-Icon-Assets** | Vollständige adaptive Icons vorhanden. Direkt portieren. |
| **Niedrige Informationsdichte / „ein Held pro Screen"** | Premium-Signal. Als Design-Prinzip verbindlich. |
| **Mock-als-austauschbares-Gerüst-Denken** (`SKILL.md`) | Sauberer Seam zwischen Mock und echter Pipeline ist goldrichtig — in Compose als Repository-Interface. |

---

## 3. Konzepte, die verworfen oder neu gedacht werden sollten (DROP / RETHINK)

| Konzept | Entscheidung | Begründung |
|---|---|---|
| **„Tap to Catch" als Interaktionsmodell** | **Verwerfen** | Per deiner Anweisung. War ohnehin ein Umweg. Agency kommt künftig übers **Zielen/Anvisieren**, nicht übers Antippen (siehe §6.3). |
| **Bisherige Einsaug-Animationen** (`bad_*`-Recordings) | **Verwerfen & neu bauen** | 3× gescheitert in Flutter. In Compose mit GPU/Shader/Partikeln neu konzipieren (§12.1). |
| **Feste Marker-Positionen über Live-Kamera** | **Verwerfen** | Der CHANGELOG flaggt es selbst: pinnt Labels auf Objekte, die evtl. nicht da sind. Bei echter Erkennung kommen Positionen aus Bounding-Boxes; bis dahin frei schweben lassen. |
| **`SharedPreferences` als Collection-Speicher** | **Verwerfen** | Falsches Werkzeug für eine wachsende Sammlung; das defensive Doppel-Schreiben ist ein Symptom. → **Room** (+ DataStore für Settings). |
| **Hartkodierte Mock-Übersetzungen als „Sprach-Layer"** | **Neu denken** | Der gesamte Sprachnutzen ist heute Attrappe. Echte Übersetzung + Sprachpaar-Strategie nötig (§6.4). |
| **Per-Navigation `pushReplacement` + Reload-from-Disk** | **Verwerfen** | Kein Single Source of Truth, Flackern, Empty-State-Blitzer. → zentrales State-Holding (ViewModel + Flow), Compose-Navigation mit echtem Back-Stack. |
| **`username_policy.dart` + Test** (Easter Egg „Slevie") | **Löschen** | Toter Code, kein Account-System. Reine Altlast. |
| **`collection_counter.dart`** | **Löschen** | Ungenutzt, noch im alten Dark-Theme (`#121615`, `mint`, `w900`). |
| **`catch_lingo_background.dart`** (505 Z.) | **Löschen** | Verworfene Indigo/Lasso-Ästhetik. Nicht portieren. |
| **`lib/legacy/vocabulary_prototype/`** | **Nicht portieren** | Vor-Pivot-Vokabel-App. Ideen ggf. mental mitnehmen, Code fällt weg. |
| **Session-Ziel = 5 + „gamiges" Summary** | **Hinterfragen** | Willkürlicher Zähler vs. „calm, no quota"-Vision. Eher als sanfter Rhythmus statt Quote (§12). |
| **16 Markdown-Dateien** | **Konsolidieren auf ~4** | Massive Redundanz und Widersprüche (`NEXT_SESSION` vs. CHANGELOG). Siehe Anhang A. |
| **`applicationId = com.example.catch_lingo`** | **Ändern** | Default-Namespace; vor Release auf echte Domain (z. B. `app.catchlingo`). |

---

## 4. Technische Risiken

Nach Schwere geordnet — das oberste Risiko ist **produktlich**, nicht plattformbezogen:

1. **Qualität der echten Objekterkennung (höchstes Risiko, plattformunabhängig).**
   Die gesamte Premise hängt daran, dass die App „nützliche Wörter" erkennt. Generische
   On-Device-Modelle (ML Kit Image Labeling/Object Detection) liefern **grobe, teils falsche
   Labels** und decken die Vision-Wunschliste (Stuhl, Markt, Schild, Motorrad …) nur teilweise ab.
   *Mitigation:* früher Spike mit echten Fotos aus Zielszenen; Label→Wort-Mapping mit Whitelist
   nützlicher Konzepte; Confidence-/Stabilitätsschwelle; ggf. später Custom-TFLite-Modell.
   **Diese Unsicherheit ist der Grund, warum der Catch-Moment zuerst mit Mock-Daten perfektioniert
   werden sollte — er muss auch mit unperfekter Erkennung tragen.**

2. **Übersetzungsqualität, Offline-Fähigkeit & Kosten.** ML Kit On-Device-Translation ist gratis
   und offline, aber qualitativ begrenzt; Cloud-Translation ist gut, aber kostet und braucht Netz.
   *Mitigation:* On-Device als Default mit lokalem Cache, Cloud optional; Zielsprachen-Modelle
   vorab herunterladen (Reise-Use-Case = oft offline!).

3. **„Spektakuläre" Animation = echte GPU-Arbeit mit API-Abhängigkeiten.**
   `RenderEffect`/Blur ab **API 31**, AGSL-`RuntimeShader` ab **API 33**, Haptik-Primitives ab
   **API 30**. *Mitigation:* graceful degradation, `minSdk 26` als Boden, Premium-Effekte hinter
   Capability-Checks, getestet auf Mittelklasse-Hardware.

4. **60 fps mit Kamera-Analyse + Animation + Haptik gleichzeitig.** Thermik/Akku bei Dauer-Analyse.
   *Mitigation:* CameraX `ImageAnalysis` mit Backpressure (`STRATEGY_KEEP_ONLY_LATEST`), Analyse
   gedrosselt (~1–3 fps), Analyse pausieren während der Catch-Choreografie.

5. **Kamera-Lifecycle & Permissions.** Pause/Resume, Entzug zur Laufzeit, kein Sensor.
   *Mitigation:* CameraX bindet an Lifecycle (löst die Klasse von Bugs, die `explore_screen.dart`
   heute manuell behandelt); klarer Permission-Rationale-Screen.

6. **Rewrite-Risiko + Verlust-Historie.** Das Team hat schon einmal Arbeit verloren
   (`NEXT_SESSION.md`, „recovered from a dangling tree"). Ein Rewrite ist ein heikler Moment.
   *Mitigation:* neues, sauberes Repo; das Flutter-Repo bleibt als Referenz unangetastet; keine
   destruktiven Git-Operationen; früh und oft committen.

7. **Scope-Falle.** Die Versuchung, „alles" neu zu bauen, während die Premise unvalidiert ist.
   *Mitigation:* strikte Roadmap (§10), vertikale Scheibe zuerst, MoSCoW (§11) hart einhalten.

---

## 5. UX-Schwächen (Ist-Zustand)

1. **Die Kern-Fantasie ist noch nicht real.** Ohne echte Erkennung fühlt sich die App wie ein
   schön gestyltes Mock an — genau das Fazit der projekteigenen `VISION_ALIGNMENT_REVIEW.md`
   (§5: „the MVP still feels like selecting vocabulary from a styled list").
2. **Kein echter „Wo & wann"-Kontext**, obwohl die Vision ihn als *emotionalen Kern* benennt.
   Das Datenmodell kennt nur `category` + Timestamps — kein Ort, keine Szene, keine Session-Identität.
   Das Feldjournal-Versprechen ist unerfüllt.
3. **Der Catch-Moment verlor in der Vacuum-Variante die Agency** (man schaut nur zu) — und in
   reinen Vacuum-Versionen auch das „gehaltene Objekt". Beide müssen in der neuen Mechanik
   gleichzeitig existieren (Agency *und* Specimen).
4. **Sprachverwirrung.** UI ist Englisch, die Vision-Grafik ist **Deutsch** („Entdecke deine Welt",
   „Mein Wörterbuch"), Zielsprache ist Indonesisch. Es gibt keine bewusste Sprach-Strategie
   (UI-Sprache vs. Quell-/Zielsprachpaar).
5. **State-Flackern.** Jede Navigation lädt neu von der Platte; `FutureBuilder` zeigt kurz leere
   Zustände. Wirkt weniger „instant", als die Vision verlangt.
6. **Quoten-Gefühl.** „Session-Ziel 5" + Summary-Statistiken stehen in leichter Spannung zur
   „calm, no streak-shaming"-Haltung.
7. **Kein Onboarding.** Erststart wirft einen direkt hinein, ohne die Geste/Idee in 10 Sekunden
   zu erklären (Cold-Start-Risiko für neue Nutzer).
8. **Dictionary mit Filtern/Suche** kippt minimal Richtung „Study-App" — niedrige Priorität, aber
   beobachten (die Alignment-Review merkt es selbst an).

---

## 6. Verbesserungsvorschläge (ohne Rücksicht auf die bestehende Implementierung)

### 6.1 Der Catch als vierphasige Choreografie (das Herzstück)
Statt „Wort fliegt in Zähler" → eine inszenierte Sequenz mit klarer Dramaturgie und
frame-synchroner Haptik (Detail in §12.1). Vier Phasen: **Notice → Lock/Charge → Suck → Hold**.
Der Endzustand ist **immer** die gehaltene Specimen-Karte. Das vereint deinen Wunsch nach
spektakulärem Einsaugen mit der Vision-Forderung nach einem „gehaltenen Fund".

### 6.2 Echtes Feldjournal-Datenmodell
Ein Wort ist nicht nur Quelle+Übersetzung, sondern eine **Erinnerung**: Ort (grob, optional),
Zeit, Szene/Session, und — der Hebel — **ein echtes Foto-Specimen** (siehe Hidden Gem §6.7).
Damit wird das Dictionary endlich zum visuellen Reisetagebuch statt zur Tabelle.

### 6.3 Agency durchs Anvisieren statt durchs Antippen
Der Nutzer „arbeitet" nicht (kein Tap-Zwang) und ist auch nicht passiv (kein reines Zuschauen).
Stattdessen: **wohin die Kamera zeigt, das wird angesaugt.** Ein ruhiges Fokus-Reticle in der Mitte,
ein Wort „lädt" sich auf, wenn es stabil im Fokus liegt, und wird dann eingesaugt. Optional eine
taktile „Halten zum Ziehen"-Variante als Power-User-Geste. Das ist die eleganteste Auflösung der
„Agency vs. Automatik"-Spannung, die das Projekt seit drei Sprints umtreibt.

### 6.4 Echte Sprachschicht mit bewusster Strategie
- **Quell-/Zielsprachpaar** explizit machen (Onboarding-Auswahl, intelligenter Default aus Geräte-Locale).
- **UI-Lokalisierung** von Anfang an (mindestens DE + EN, da Vision/Founder deutsch).
- Übersetzung: On-Device-Default + Cache, Cloud optional für Qualität.

### 6.5 Begleiter-Katze als echter Charakter
Heute ein statisches PNG in Empty-States. Künftig: reagiert auf Catches (freut sich), auf
„seen again" (begrüßt einen alten Fund), auf leere Tage (wartet geduldig, nie mahnend).
Rive/Lottie für 3–4 Zustände. Gesicht schafft Bindung — die `DESIGN_DIAGNOSIS` nennt es „sitting unused".

### 6.6 10-Sekunden-Onboarding
Ein einziger Screen, der die Geste zeigt („Richte die Kamera auf etwas — wir benennen es")
und das Sprachpaar wählen lässt. Kein Tutorial-Wall, keine Slides. Intelligente Defaults.

### 6.7 💎 Hidden Gem: Foto-Specimens (höchster Hebel, geringe Komplexität)
**Beim Catch wird der aktuelle Kamera-Frame (auf die Objekt-Bounding-Box gecroppt) als kleines
Foto gespeichert und wird zum „Specimen" des Wortes.** Das Dictionary zeigt dann nicht generische
Material-Icons, sondern **deine echten Fotos der Dinge, die du gefangen hast** — ein echtes,
persönliches Reise-Feldjournal. Liefert das „Wo & wann = Erinnerung"-Versprechen sofort, ist
emotional unschlagbar, erzeugt einen Teilen-/Souvenir-Moment (eine „Feldnotiz-Postkarte" einer
Session) und nutzt die Kamera, die ohnehin schon läuft. Technisch billig (CameraX liefert den
Frame, Crop + lokale Datei). **Das ist der Unterschied zwischen „nette Vokabel-App" und „die App,
mit der ich meine Reise nach Indonesien gesammelt habe".**

### 6.8 Disziplin: Was wir bewusst NICHT bauen (Anti-Scope-Creep)
Accounts, Cloud-Sync, Community/Feed, Marktplatz, vollwertiges SRS, AR-Anchoring, Video — alles
**nicht** in den ersten Versionen (deckt sich mit `ROADMAP.md` „What Not To Do Now"). Erst muss
die Schleife großartig sein.

---

## 7. Empfohlene Kotlin / Jetpack-Compose-Architektur

**Leitbild:** Single-Activity, 100 % Compose, unidirektionaler Datenfluss (MVI-light),
saubere Schichten, austauschbare Seams für Erkennung & Übersetzung.

### 7.1 Schichten

```
┌─────────────────────────────────────────────────────────────┐
│  PRESENTATION (Compose)                                      │
│  - Screens (composables, stateless) + Navigation             │
│  - ViewModels: StateFlow<UiState>, nehmen Intents/Events     │
│  - Catch-Choreografie als eigenständige UI-Komponente        │
├─────────────────────────────────────────────────────────────┤
│  DOMAIN (reines Kotlin, kein Android)                        │
│  - Models: CaughtWord, Detection, Session, FieldNote         │
│  - UseCases: CatchWordUseCase, ReinforceWordUseCase, ...      │
│  - Repository-Interfaces (Ports)                             │
├─────────────────────────────────────────────────────────────┤
│  DATA (Implementierungen / Adapter)                          │
│  - CollectionRepository  → Room (+ DataStore Settings)       │
│  - DetectionRepository   → MockDetector | MlKitDetector      │
│  - TranslationRepository → MlKitTranslate | CloudTranslate   │
│  - CameraController       → CameraX                          │
│  - HapticPlayer           → VibrationEffect.Composition       │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 Kernprinzipien
- **Single Source of Truth:** die Collection lebt als `Flow` aus Room im Repository; jeder Screen
  beobachtet sie. Schluss mit „Reload-from-Disk bei jeder Navigation".
- **Unidirektional:** UI sendet Intents → ViewModel mutiert `UiState` → Compose rekomponiert.
- **Austauschbare Seams (der wichtigste Architektur-Move):** `DetectionRepository` und
  `TranslationRepository` sind Interfaces. Heute Mock-Implementierung, später ML-Kit-Implementierung
  — **ohne dass die UI sich ändert.** Genau das verlangt `SKILL.md` („clean seams so mocks can be
  swapped").
- **Catch-Choreografie isoliert:** eine dedizierte Komponente besitzt die Animation+Haptik-Timeline,
  damit sie unabhängig perfektioniert und mit Screenshot-/Frame-Tests abgesichert werden kann.
- **DI mit Hilt:** macht den Mock↔Real-Tausch zu einer Ein-Zeilen-Modulentscheidung (`@Binds`).

### 7.3 Navigation
Type-safe Compose Navigation (Routen als `@Serializable`-Objekte). Eine `NavHost` mit
Bottom-Bar-Scaffold (Discover / Dictionary / Review) + Vollbild-Destination Explore. Der
Fade-Through aus `page_transitions.dart` wird zur globalen Compose-Transition.

---

## 8. Empfohlene Bibliotheken

| Bereich | Empfehlung | Warum / Vision-Fit |
|---|---|---|
| UI | **Jetpack Compose** (BOM) + **Material 3** | Deklarativ, animationsstark, exakte Kontrolle über den Catch-Moment. |
| Navigation | **Navigation-Compose** (type-safe, 2.8+) | Echter Back-Stack statt `pushReplacement`-Hacks. |
| DI | **Hilt** | Mock↔Real-Seam per Modul; testbar; Standard. |
| Kamera | **CameraX** (`camera-core/camera2/lifecycle/view`) | Lifecycle-gebunden (löst die manuellen Pause/Resume-Bugs), `ImageAnalysis` für Erkennung, `PreviewView`. |
| Erkennung | **ML Kit Object Detection & Tracking** + **Image Labeling** (on-device) | Bounding-Boxes fürs Anvisieren + Labels fürs Wort. Später Custom-TFLite. Kostenlos, offline. |
| Übersetzung | **ML Kit Translate** (on-device) + optional **Cloud Translation** | Offline-Default (Reise!), Cloud für Qualität. |
| Persistenz | **Room** (Collection, Sessions, FieldNotes) + **DataStore** (Settings/Sprachpaar) | Wächst sauber; reaktive `Flow`-Queries; ersetzt den fragilen Prefs-Blob. |
| Bilder | **Coil** (Compose) | Foto-Specimens & evtl. illustrierte Assets. |
| Async | **kotlinx-coroutines** + **Flow** | Reaktiver State, Backpressure bei der Analyse. |
| Serialisierung | **kotlinx-serialization** | Type-safe Nav + ggf. Export/Import. |
| Haptik | **Platform `Vibrator` + `VibrationEffect.Composition`** (kein Lib) + Compose `HapticFeedback` | Primitives `QUICK_RISE/SPIN/THUD/TICK` = exakt der Einsaug-Effekt (§12.1). |
| Animation | Compose-Animation, **`graphicsLayer`/`RenderEffect`** (Blur), **AGSL `RuntimeShader`** (API 33+) | Warp/Glas/Sog-Effekte nativ. Fallbacks darunter. |
| Companion | **Rive** (bevorzugt) oder **Lottie** | 3–4 Zustände der Katze, tasteful. |
| Permissions | **Accompanist Permissions** | Sauberer Rationale-Flow. |
| Tests | **JUnit5**, **Turbine** (Flows), **MockK**, **Compose UI Test**, **Roborazzi/Paparazzi** (Screenshot/Frame-Tests des Catches) | Der Signature-Moment wird visuell regressionsgesichert. |

> **Bewusst NICHT (vorerst):** RxJava, Dagger-pur (Hilt reicht), SQLDelight (Room genügt),
> ein Game-Engine/Unity (Compose + Canvas + Shader reichen für den Effekt). Keine unnötigen Pakete —
> ganz im Sinne von `TECHNICAL_DECISIONS.md`.

---

## 9. Vorschlag für Projektstruktur

Feature-basiert mit `core`-Modulen. Start als Single-Module mit Package-Trennung; bei Bedarf
später in Gradle-Module splitten.

```
app/
 └─ src/main/java/app/catchlingo/
    ├─ CatchLingoApp.kt              # @HiltAndroidApp
    ├─ MainActivity.kt               # Single Activity, setContent { CatchLingoRoot() }
    │
    ├─ core/
    │   ├─ designsystem/             # Theme, Color, Type, Spacing, Shapes (aus app_theme.dart)
    │   │   └─ component/            # WordSpecimenBadge, CompanionCat, PrimaryButton, ...
    │   ├─ haptics/                  # HapticPlayer + Composition-Patterns (grip/suck/land)
    │   ├─ camera/                   # CameraX-Wrapper, FrameProvider, Crop-Utils
    │   └─ ui/                       # gemeinsame Modifier, Animations-Helfer, Shader
    │
    ├─ domain/
    │   ├─ model/                    # CaughtWord, Detection, Session, FieldNote, LanguagePair
    │   ├─ repository/               # Interfaces: Collection/Detection/Translation
    │   └─ usecase/                  # CatchWord, ReinforceWord, GetReviewQueue, ...
    │
    ├─ data/
    │   ├─ collection/               # Room (Entities, DAO, RoomCollectionRepository)
    │   ├─ detection/                # MockDetectionRepository | MlKitDetectionRepository
    │   ├─ translation/              # MlKitTranslationRepository | CloudTranslationRepository
    │   ├─ settings/                 # DataStore
    │   └─ di/                       # Hilt-Module (bindet Mock vs. Real)
    │
    └─ feature/
        ├─ home/                     # HomeScreen + HomeViewModel
        ├─ explore/                  # ExploreScreen + ExploreViewModel (Kamera + Erkennung)
        │   └─ catch/                # CatchChoreography (Notice→Lock→Suck→Hold) + Haptik-Sync
        ├─ dictionary/               # Liste + Detail + Foto-Specimens
        ├─ review/                   # Remember-Flow (Easy/Hard)
        ├─ session/                  # Session-Summary + Feldnotiz-Postkarte
        └─ onboarding/               # 10-Sekunden-Einstieg + Sprachpaar
```

---

## 10. Priorisierte Roadmap für den Neuaufbau

> Leitsatz: **Erst das Risiko töten, das Flutter nicht lösen konnte (der Catch-Moment), dann die
> Premise (Erkennung/Übersetzung), dann die Breite.**

### Phase 0 — Fundament & Catch-Spike *(de-riskt das Wichtigste zuerst)*
- Neues Repo, Gradle/Hilt/Compose-Setup, `minSdk 26`, CI mit `lint`+`test`.
- Design-System-Tokens aus `app_theme.dart` portieren; App-Icons übernehmen.
- **Catch-Choreografie als vertikale Scheibe** mit **Mock**-Daten + echter CameraX-Preview:
  Notice → Lock → Suck → Hold-Specimen, mit `VibrationEffect.Composition`-Haptik.
- **Done:** Auf einem echten Mittelklasse-Gerät fühlt sich *ein* Catch großartig an (60 fps,
  spürbare Haptik, gehaltenes Objekt). Frame-/Screenshot-Test grün. *Das ersetzt die drei
  `bad_*`-Recordings durch ein „good_catch".*

### Phase 1 — Kernschleife end-to-end (mit Mock-Erkennung)
- Explore → Catch → Room-Persistenz → Dictionary → Review, alles über ViewModel+Flow.
- Dublettenschutz + „seen again". Bottom-Nav, Fade-Through, Empty-States, Companion.
- **Done:** Die ganze Schleife läuft auf echter Architektur; State flackert nicht mehr.

### Phase 2 — Feldjournal & Foto-Specimens
- Datenmodell um Session/Ort/Zeit + **Foto-Specimen** (Hidden Gem) erweitern.
- Session-Summary als teilbare „Feldnotiz". Dictionary wird visuell.
- **Done:** Ein gefangenes Wort ist eine Erinnerung mit echtem Foto, nicht eine Tabellenzeile.

### Phase 3 — Echte Erkennung (Seam tauschen)
- `MlKitDetectionRepository` hinter dem bestehenden Interface; Anvisieren über Bounding-Boxes;
  Label→Wort-Whitelist; Stabilitäts-/Confidence-Schwelle; Analyse-Drosselung.
- **Done:** Auf echte Objekte gerichtet erscheinen nützliche, stabile Wörter.

### Phase 4 — Echte Übersetzung, Sprachpaare & Onboarding
- On-Device-Translate + Cache; Sprachpaar-Auswahl; UI-Lokalisierung DE/EN; 10-Sek-Onboarding.
- **Done:** Echtes Quell-/Zielsprachpaar, offline nutzbar; neue Nutzer verstehen es in Sekunden.

### Phase 5 — Politur, Belohnung, Charakter
- Companion-Reaktionen (Rive), Mikro-Belohnungen, „erster Fund des Tages", sanfter Rhythmus
  statt Quote, Shader-Feinschliff, Accessibility-Pass, Performance-/Thermik-Tuning.
- **Done:** Die „Ich will noch ein Wort sammeln"-Erfahrung trägt über mehrere Sessions.

---

## 11. Feature-Liste mit Priorität (MoSCoW)

### Must (ohne diese ist es nicht CatchLingo)
- Echte CameraX-Preview, lifecycle-sicher
- **Spektakulärer Einsaug-Catch mit Specimen-Endzustand + frame-synchrone Haptik**
- Anvisieren als Agency-Modell
- Dublettenschutz + „seen again"-Verstärkung
- Collection-Persistenz (Room), Single Source of Truth
- Dictionary als persönliche Sammlung
- Leichtes Review (Easy/Hard, kein Scham-SRS)
- Warme Identität (Theme, Specimen, Companion-Katze)

### Should (macht es großartig statt nur funktional)
- **Foto-Specimens** (Hidden Gem)
- Session/Ort/Zeit-Kontext (Feldjournal)
- Echte On-Device-Erkennung + Übersetzung
- Sprachpaar-Auswahl + UI-Lokalisierung (DE/EN)
- 10-Sekunden-Onboarding
- Companion-Reaktionen

### Could (schöne Hebel, später)
- Teilbare Feldnotiz-Postkarte einer Session
- Sanfte Sammelsets / weiche Vollständigkeit
- Aussprache (TTS) am Wort
- Home-Screen-Widget „Wort des Tages"
- Cloud-Übersetzung als Qualitätsoption
- Leichtes SRS hinter dem Review

### Won't (bewusst vorerst nicht)
- Accounts, Cloud-Sync, Community/Feed/Marktplatz
- Vollwertiges Spaced-Repetition-System
- AR-Anchoring, Videoaufnahme
- Manuelle Vokabelverwaltung („add word")
- iOS (Android-first, wie dokumentiert)

---

## 12. Animationen, Microinteractions & Belohnungssysteme

> Filter für **jede** Idee: *Macht das „die Welt bemerken" wertvoller — oder lässt es die App
> mehr wie eine App wirken?* (die Leitfrage aus `DESIGN_VISION.md`). Warm, ruhig, nie Casino.

### 12.1 ⭐ Die Signatur: der Einsaug-Catch in vier Phasen
Das ist der Moment, den die Nutzer ihren Freunden beschreiben würden. Frame-synchron mit Haptik.

| Phase | Visuell | Haptik (`VibrationEffect.Composition`) | Dauer |
|---|---|---|---|
| **1 · Notice** | Wort taucht ruhig im Fokus auf, atmet leicht, sanftes Aufglimmen am Rand. | `PRIMITIVE_LOW_TICK` (kaum spürbar) | ~300 ms |
| **2 · Lock / Charge** | Reticle rastet ein, ein feiner Lichtsog beginnt nach innen zu wirbeln, Hintergrund leicht radial unscharf (`RenderEffect`). | `PRIMITIVE_QUICK_RISE` (anschwellender Zug) | ~250 ms |
| **3 · Suck** | Das Wort + ein kurzer Partikel-/Lichtschweif werden auf einer leicht gekrümmten Bahn in die Mitte gerissen; kurze Warp-/Glas-Verzerrung (AGSL-Shader, API 33+, sonst Skalierung+Blur). | `PRIMITIVE_SPIN` → unmittelbar `PRIMITIVE_QUICK_FALL` | ~280 ms |
| **4 · Hold** | Aus dem Sog **blüht die Specimen-Karte** mit einem **einzigen** weichen Amber-Bloom hervor, leicht überschwingend (easeOutBack), und „rastet" mit Gewicht ein. Übersetzung groß in Grün, Quelle darunter, Kontextzeile. | `PRIMITIVE_THUD` (das befriedigende „Einrasten") | ~540 ms, hält ~1,3 s |

Prinzipien: **kurz und lesbar** (Gesamt < 1,4 s aktive Animation), **ein** Lichtblüten-Akzent
statt Partikelflut, immer endend im **gehaltenen Objekt**. Das ist die Synthese aus „spektakulär"
(deine Forderung) und „kein Cheap Juice / gehaltener Fund" (Vision). Auf API < 33 ohne Shader,
auf API < 31 ohne Blur — der **Haptik- und der Specimen-Moment tragen den Effekt auch dann.**

### 12.2 Der Zähler-Ring & der „seen again"-Gruß
- Zentraler/oberer Fortschrittsring füllt sich Richtung sanftem Ziel; **ein** weicher Puls + Amber-Funke beim Einrasten. Kein hektisches Hochzählen.
- **„Seen again"**: ein bereits gefangenes Wort wird nicht doppelt gezählt, sondern **warm begrüßt** — die Katze nickt, ein kurzer Amber-Schimmer, „alter Freund wiedergesehen". Hält die Kamera auch ohne Neues belohnend (harte `SKILL.md`-Anforderung).

### 12.3 Specimen-Karten-Physik
Leichter Parallax/Tilt der gehaltenen Karte gegenüber dem Welt-Hintergrund (Gyroskop, sehr dezent),
weicher echter Schatten — sie fühlt sich an wie ein physisches, aufgehobenes Objekt. (Vision:
„objects you could pick up".)

### 12.4 Begleiter-Katze als emotionaler Verstärker
Reagiert: Freude beim neuen Fund, Begrüßung beim Wiedersehen, geduldiges Warten bei leeren Tagen
(nie mahnend). 3–4 Rive-Zustände. Erscheint in Empty-States, beim ersten Fund, in der Session-Feier.

### 12.5 Belohnungssysteme — warm, nicht gamifiziert
- **Erster Fund des Tages** bekommt einen Hauch mehr Glanz (Golden-Hour-Licht) — ein Grund, täglich kurz reinzuschauen, ohne Streak-Druck.
- **Sanfte Sammelsets** (Café, Markt, Natur …) füllen sich weich; „gathering", keine Quote.
- **Feldnotiz-Postkarte** am Session-Ende: die gefangenen Wörter auf dem Welt-Foto gepinnt, mit Ort/Zeit — teilbar, ein Souvenir.
- **„Fading memory"**-Visual im Review: lange nicht gesehene Wörter wirken zart verblasst und „frischen sich auf", wenn man sie wieder erinnert (baut auf der vorhandenen `_isFadingWord`-Idee auf, macht sie fühlbar).
- **Aussprache-Mikro-Moment**: Tippen auf das Wort spricht es warm aus (TTS) — kleiner, befriedigender Haken.

### 12.6 Microinteractions (durchgängig)
Bottom-Nav-Press-Scale, gestaffelte Karten-Einblendung, Karten-Press-Lift, ruhiger Fade-Through
zwischen Screens, Shared-Element vom Dictionary-Wort zum Detail. (Viele davon sind aus dem
`ANIMATION_AUDIT.md` bereits durchdacht — in Compose mit `animate*AsState`/`AnimatedContent`
trivial und performant.)

### 12.7 Anti-Pattern (verboten, aus der Vision)
Kein Konfetti/Casino, keine XP-Balken/Münzen, keine Badges überall, keine HUD/Scanner/Confidence-Overlays,
keine Streak-Scham, keine Dauer-Partikel, keine grellen Farben. Emotion vor Mechanik.

---

## Anhang A — Doku-Konsolidierung

16 Dateien mit hoher Redundanz und echten Widersprüchen (`NEXT_SESSION.md` ↔ CHANGELOG zur
Vacuum-Frage). Empfehlung fürs neue Repo — **4 Dokumente**:

| Neu | Speist sich aus |
|---|---|
| `VISION.md` | `VISION.md` + `DESIGN_VISION.md` + `REAL_WORLD_DISCOVERY_CONCEPT.md` |
| `DESIGN_SYSTEM.md` | `DESIGN_SYSTEM.md` + `UI_SPEC.md` + `ANIMATION_AUDIT.md` |
| `ARCHITECTURE.md` | dieses Dokument (§7–§9) + `TECHNICAL_DECISIONS.md` |
| `ROADMAP.md` | dieses Dokument (§10–§11) + `ROADMAP.md` |

Verzichtbar/archivieren: `DESIGN_DIAGNOSIS.md` (beschreibt einen bereits behobenen Dark-Stand),
`VISION_ALIGNMENT_REVIEW.md` (Snapshot), `NEXT_SESSION.md` (Recovery-Artefakt), `AGENTS.md`/`SKILL.md`
(Flutter-spezifisch → für Kotlin neu fassen).

## Anhang B — Konkrete Lösch-/Nicht-Portier-Liste (Dead Code)

- `lib/widgets/catch_lingo_background.dart` — verworfene Indigo/Lasso-Ästhetik (505 Z.)
- `lib/widgets/collection_counter.dart` — ungenutzt, altes Dark-Theme
- `lib/models/username_policy.dart` + `test/username_policy_test.dart` — Easter-Egg ohne Account-System
- `lib/legacy/vocabulary_prototype/**` — Vor-Pivot-Vokabel-App
- `screenshots/screenrecordings/bad_*` + `*_frames_4fps/` — abgeschlossene Negativ-Belege (archivieren)
- CHANGELOG-Referenz auf `lib/services/collection_store.dart` ist veraltet (Datei existiert nicht; heißt `caught_word_storage.dart`)

## Anhang C — Offene Produktentscheidungen (mit empfohlenem Default)

1. **UI-Sprache?** → *Default:* DE + EN von Beginn an (Founder/Vision deutsch).
2. **Quell-/Zielsprache wählbar oder fix?** → *Default:* wählbar, smarter Default aus Locale; Start mit DE/EN→ID.
3. **Übersetzung on-device oder cloud?** → *Default:* on-device + Cache; Cloud später als Qualitätsoption.
4. **Erkennung: generisches ML Kit oder Custom-Modell?** → *Default:* generisch starten, Whitelist nützlicher Konzepte; Custom nur wenn Qualität es erzwingt.
5. **Session-Ziel beibehalten?** → *Empfehlung:* durch sanften Rhythmus ersetzen (kein harter „5er"-Counter).
6. **`minSdk`?** → *Empfehlung:* 26 (Boden), Premium-Haptik ab 30, Shader ab 33 mit Fallbacks.

---

*Ende des Masterplans. Dieses Dokument ist als Diskussions- und Umsetzungsgrundlage gedacht;
die offenen Entscheidungen in Anhang C sollten vor Phase 0 kurz bestätigt werden.*
