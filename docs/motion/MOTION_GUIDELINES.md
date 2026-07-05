# CatchLingo — Motion Guidelines

> **Zweck:** Vollständige, plattformneutrale Spezifikation des CatchLingo Motion Systems —
> Bewegungssprache, Animation-Tokens, Haptik-Choreografie und Microinteractions. Dieses Dokument
> ist eine **reine Spezifikation, kein Code.** Es dient Codex (und jedem Implementierer) als direkte
> Umsetzungsgrundlage für die Kotlin- / Jetpack-Compose-Neuentwicklung.
>
> **Übergeordnet:** [PROJECT_VISION.md](PROJECT_VISION.md) ist die einzige verbindliche Quelle der
> Wahrheit. Dieses Dokument konkretisiert deren §4 (Fangmechanik), §6 (Visuelle Identität, Motion-
> Dauern) und §7 (Screens) sowie `KOTLIN_REWRITE_MASTERPLAN.md` §12 (Animationen & Belohnung). Bei
> jedem Widerspruch gewinnt `PROJECT_VISION.md`.
>
> **Visuelle Identität:** [VISUAL_IDENTITY.md](VISUAL_IDENTITY.md) macht die Referenz-Assets unter
> `assets/design/` (App-Icon-Katze + `catchlingo_ui_vision_v1`) zum verbindlichen Zielbild. Motion
> verstärkt diese warme, hochwertige Designsprache — sie führt sie nie vor.
>
> **Plattform:** Kotlin + Jetpack Compose (Android-first). · **Fangmechanik:** ausschließlich
> Magnet/Staubsauger. · **Tap-to-Catch ist vollständig verworfen.**
>
> Stand: 2026-06-14

---

## Inhaltsverzeichnis

1. [Grundprinzipien der Bewegungssprache](#1-grundprinzipien-der-bewegungssprache)
2. [Motion Personality von CatchLingo](#2-motion-personality-von-catchlingo)
3. [App-weite Animation Tokens](#3-app-weite-animation-tokens)
4. [Scanner-Animationen (der Catch)](#4-scanner-animationen-der-catch)
5. [Haptik-Choreografie](#5-haptik-choreografie)
6. [Page-Transitions](#6-page-transitions)
7. [Bottom-Navigation-Motion](#7-bottom-navigation-motion)
8. [Cards, Chips, Buttons und Dialoge](#8-cards-chips-buttons-und-dialoge)
9. [Collection- / Dictionary-Microinteractions](#9-collection--dictionary-microinteractions)
10. [Review-Screen-Animationen](#10-review-screen-animationen)
11. [Anti-Patterns](#11-anti-patterns)
12. [Implementierungs-Empfehlung für Compose](#12-implementierungs-empfehlung-für-compose)

---

## 1. Grundprinzipien der Bewegungssprache

Bewegung in CatchLingo ist nie Dekoration. Sie hat immer einen der drei Jobs: **Aufmerksamkeit
führen**, **eine Zustandsänderung erklären** oder **einen Moment emotional belohnen**. Bewegung,
die keinen dieser drei Jobs erfüllt, wird entfernt.

**Die sechs Leitsätze (verbindlich):**

1. **Motion dient der Welt, nicht der App.** Jede Animation soll das Gefühl „ich bemerke etwas in
   der echten Welt" verstärken, nie das Gefühl „eine App rendert eine UI". Filterfrage für jede
   Bewegung: *Macht das „die Welt bemerken" wertvoller — oder lässt es CatchLingo mehr wie eine App
   wirken?* (Leitfrage aus `PROJECT_VISION.md`).
2. **Ein Held pro Screen, ein Held pro Moment.** Es bewegt sich immer nur eine Sache wirklich
   prominent. Beim Fang ist das das Wort. Alles andere ordnet sich unter (langsamer, leiser, kleiner).
   Konkurrierende Animationen zerstören das Premium-Gefühl.
3. **Physik statt Mechanik.** Dinge beschleunigen und verzögern wie echte Objekte mit Gewicht und
   Trägheit. Lineare Bewegung ist verboten (außer für kontinuierliche Loops wie einen sich füllenden
   Ring). Specimen-Karten fühlen sich an wie aufhebbare Gegenstände, nicht wie eingeblendete `View`s.
4. **Kurz, lesbar, dann aus dem Weg.** Microinteractions liegen bei 100–250 ms, Screen-Wechsel bei
   250–400 ms, der gesamte aktive Catch unter 1,4 s. Bewegung darf den Nutzer nie warten lassen oder
   die Szene blockieren. Im Zweifel kürzer.
5. **Warm und sicher, nie hektisch.** Easing ist weich, Federn schwingen sanft (nicht gummiartig).
   Nichts blinkt, ruckelt oder pulsiert nervös. Die App ist ihrer selbst sicher — Bewegung
   transportiert Ruhe, nicht Aufregung.
6. **Haptik ist Teil der Animation, nicht ein Zusatz.** Schlüsselmomente sind frame-synchron mit
   Haptik unterlegt. Die Animation und das Vibrationsmuster werden zusammen entworfen, nie nachträglich.

**Hierarchie der Bewegung** (wie viel Energie ein Moment bekommen darf):

| Tier | Beispiele | Energie |
|---|---|---|
| **Hero** | Der Einsaug-Catch, die Specimen-Hold-Phase | Höchste — spektakulär im Sinne von Handwerk |
| **Signal** | „Seen again"-Gruß, Fortschrittsring-Puls, erster Fund des Tages | Mittel — ein klarer, warmer Akzent |
| **Functional** | Page-Transition, Card-Press, Nav-Wechsel, Chip-Toggle | Niedrig — schnell, unauffällig, dienend |
| **Ambient** | Companion-Atmen, „schaue mich um…"-Andeutung, leichtes Notice-Atmen | Minimal — kaum bewusst wahrnehmbar |

Energie wird **nur dort ausgegeben, wo sie emotional verdient ist.** 95 % der App ist Functional/
Ambient, damit die wenigen Hero-Momente kostbar bleiben.

---

## 2. Motion Personality von CatchLingo

Wäre CatchLingos Bewegung eine Person, wäre sie der **warme, neugierige Begleiter auf einem
Morgenspaziergang** aus `PROJECT_VISION.md` §5 — jemand, der dich sanft auf etwas aufmerksam macht
(„schau — *kopi*"), kurz mit dir innehält, und dann weitergeht. Nie ein Coach mit Stoppuhr, nie ein
Spielautomat, nie ein Lehrer an der Tafel.

**Wäre die Bewegung ein physisches Objekt:** das Umblättern in einem leinengebundenen Feldjournal,
das Aufheben eines gepressten Blatts, das Einrasten eines Polaroids. Taktil, gewichtet, leise premium.

**Motion-Adjektive (das Ziel):**

- *warm* — Easing ist weich und einladend, nie schneidend.
- *gewichtet* — Dinge haben Masse; sie federn beim Stoppen leicht nach, fallen nicht hart.
- *flüssig* — Übergänge sind kontinuierlich, nie sprunghaft; 60+ fps ist Pflicht, nicht Ziel.
- *taktil* — die Schlüsselmomente fühlt man in der Hand (Haptik), nicht nur im Auge.
- *zurückhaltend* — die App bewegt sich selten und dann mit Bedeutung; Stille ist die Grundlinie.
- *belohnend* — der Catch fühlt sich an wie ein kleiner, befriedigender „Klick" des Einrastens.

**Motion-Anti-Adjektive (verboten):** verspielt-hektisch, gummiartig, casinohaft, blinkend, „bouncy
for fun", technisch/HUD-artig, daueranimiert, aufmerksamkeitsheischend.

> **Der Signatur-Moment in einem Satz:** Ein Wort wird aus der warmen, unscharfen Welt sanft
> herangezogen, kurz und kraftvoll hereingerissen, und blüht mit einem einzigen weichen Lichtakzent
> zu einer gehaltenen Karte auf, die mit spürbarem Gewicht einrastet.

---

## 3. App-weite Animation Tokens

> Diese Tokens sind die **einzige zulässige Quelle** für Dauer, Easing und Spring-Verhalten. Kein
> Screen definiert eigene „magic numbers". Werte basieren auf den Motion-Dauern aus
> `PROJECT_VISION.md` §6.2 und werden hier zu einem benannten System ausgebaut.

### 3.1 Dauer-Tokens

| Token | Wert | Einsatz |
|---|---|---|
| `duration.instant` | 80 ms | Press-Down-Feedback, Tap-Acknowledgement |
| `duration.micro` | 150 ms | Chip-Toggle, Icon-State, kleine Farb-/Alpha-Wechsel |
| `duration.short` | 220 ms | Card-Press-Lift, Button-State, Tab-Indicator |
| `duration.medium` | 320 ms | Page-Transition (Fade-Through), Sheet-Auf/Zu, Listen-Stagger-Item |
| `duration.long` | 480 ms | Specimen-Hold-Bloom, größere Reveal-Momente |
| `duration.holdRead` | 1300 ms | Verweildauer der gehaltenen Specimen-Karte (Lesezeit) |
| `duration.ambientLoop` | 2600 ms | Companion-Atmen, „schaue mich um…"-Pulse (eine Periode) |

**Regeln:**
- Catch-Phasen nutzen eigene, präzise abgestimmte Werte (siehe §4) — nicht diese generischen Tokens.
- Listen-Stagger: `duration.medium` pro Item, Versatz `staggerStep = 40 ms`, max. 6 Items gestaffelt,
  danach erscheinen weitere ohne Versatz (verhindert träges „Auftröpfeln" langer Listen).

### 3.2 Easing-Tokens

CatchLingo nutzt einen kleinen, disziplinierten Satz von Kurven. Material-Standard-Easing ist
**nicht** der Default — es fühlt sich generisch an.

| Token | Charakter | Kurve (Richtwert, kubische Bézier) | Einsatz |
|---|---|---|---|
| `easing.standard` | weich rein/raus | `(0.4, 0.0, 0.2, 1.0)` | Allzweck-Übergänge, Farbe, Alpha |
| `easing.entrance` | sanft ankommend | `(0.0, 0.0, 0.2, 1.0)` (decelerate) | Elemente, die einfliegen/erscheinen |
| `easing.exit` | zügig verschwindend | `(0.4, 0.0, 1.0, 1.0)` (accelerate) | Elemente, die wegfliegen/ausblenden |
| `easing.emphasized` | betont, leicht überschwingend | `(0.2, 0.0, 0.0, 1.0)` | Hero-Reveals, wichtige State-Changes |
| `easing.overshootSoft` | weiches Nachfedern | `easeOutBack`, Overshoot ≈ 1.1 | Specimen-Hold-Einrasten, Card-Lift |
| `easing.suck` | langsam ziehen, schnell reißen | custom: flache Anfahrt → steiler Endteil | Einsaug-Bahn des Worts (Phase 3) |

**Verbot:** `linear` ist ausschließlich für endlose Loops erlaubt (sich füllender Ring, Companion-
Atem-Phase). Niemals für diskrete A→B-Übergänge.

### 3.3 Delay-Tokens

| Token | Wert | Einsatz |
|---|---|---|
| `delay.none` | 0 ms | Direktes Feedback (jede Nutzeraktion antwortet sofort) |
| `delay.stagger` | 40 ms | Versatz zwischen gestaffelten Listen-Items |
| `delay.beat` | 120 ms | Bewusste Mikro-Pause vor einem Belohnungsakzent (lässt den Moment „atmen") |
| `delay.settle` | 200 ms | Pause nach Hold-Bloom, bevor sekundäre Infos (Kontextzeile) erscheinen |

**Regel:** Nutzer-initiierte Bewegung hat **nie** Delay auf dem ersten sichtbaren Feedback. Delays
gibt es nur, um Belohnungsmomente zu dramatisieren, nie um Reaktionen zu verzögern.

### 3.4 Scale-Tokens

| Token | Wert | Einsatz |
|---|---|---|
| `scale.pressDown` | 0.96 | Buttons, Cards, Nav-Items beim Druck |
| `scale.pressDownSubtle` | 0.98 | Große Flächen (ganze Card), wo 0.96 zu viel wäre |
| `scale.tapPop` | 1.04 | Kurzer „Pop" bei Bestätigung (z. B. Chip aktiviert) |
| `scale.revealFrom` | 0.85 | Startgröße eines Elements, das hereinblüht (Specimen) |
| `scale.noticeBreath` | 1.00 ↔ 1.03 | Sanftes Atmen eines bemerkten Worts (Ambient) |

**Regel:** Scale-Werte bleiben dezent. Nichts skaliert unter 0.92 (wirkt „eingedrückt"/billig) oder
über 1.06 (wirkt aufdringlich), außer in der dramatisierten Suck-Phase (dort schrumpft das Wort
gezielt gegen ~0.2 beim Hereinreißen).

### 3.5 Fade-Tokens

| Token | Wert | Einsatz |
|---|---|---|
| `fade.in` | 0 → 1 über `duration.medium`, `easing.entrance` | Standard-Einblendung |
| `fade.out` | 1 → 0 über `duration.short`, `easing.exit` | Standard-Ausblendung (schneller als rein) |
| `fade.throughMid` | Kreuzblende mit 50 % Überlappung | Page-Transitions (Fade-Through) |
| `fade.ghost` | Zielalpha 0.45 | „Fading memory"-Zustand im Review (lange nicht gesehen) |

**Prinzip:** Ausblenden ist immer etwas schneller als Einblenden — Dinge verschwinden zügig, kommen
sanft. Das wirkt aufgeräumt und ruhig.

### 3.6 Spring-Verhalten

Federn sind der Default für **alles, was sich anfühlen soll wie ein physisches Objekt** (Specimen,
Card-Lift, Nav-Indicator). Standard-Animationen (Farbe, Alpha) nutzen dagegen Dauer+Easing.

| Token | Stiffness | Damping Ratio | Charakter | Einsatz |
|---|---|---|---|---|
| `spring.gentle` | niedrig (~200) | 0.85 | weich, kaum Overshoot | Card-Lift, Sheet-Position |
| `spring.snappy` | mittel (~400) | 0.75 | flott mit leichtem Nachfedern | Nav-Indicator, Chip-Pop |
| `spring.settle` | mittel (~350) | 0.65 | sichtbares, befriedigendes Einrasten | Specimen-Hold (Phase 4) |
| `spring.heavy` | niedrig (~150) | 0.9 | träge, gewichtig | große Sheets, Specimen-Parallax |

**Verbot:** Keine Feder mit Damping < 0.6 (wirkt gummiartig/bouncy = casinohaft). Overshoot ist ein
*Hauch*, kein Trampolin.

---

## 4. Scanner-Animationen (der Catch)

> Dies ist das Herzstück. Der Catch ist die **Signatur-Sequenz** von CatchLingo und folgt dem
> vierteiligen Rhythmus aus `PROJECT_VISION.md` §4.2 (**Notice → Lock/Charge → Suck → Hold**),
> verfeinert in `KOTLIN_REWRITE_MASTERPLAN.md` §12.1. Der Endzustand ist **immer** die gehaltene
> Specimen-Karte. Ein Fang, der nur einen Zähler hochzählt, ist unvollständig und verboten.

### 4.1 Kamera öffnet sich (Eintritt in Explore)

Der Übergang in den Explore-Screen ist kein harter Schnitt auf einen Kamera-Feed.

- **Sequenz:** Cream-Canvas hält kurz → Kamera-Preview blendet sanft auf (`fade.in`,
  `duration.long`) und „atmet" minimal von `scale 1.04 → 1.00` (`easing.entrance`), als ob die Welt
  langsam in Fokus kommt.
- **Wärme zuerst:** Über das Bild legt sich sofort die warme Rand-Vignette (kein dunkler Scrim,
  `PROJECT_VISION.md` §6.3). Keine Scan-Linien, kein „Initialisiere Kamera"-Text.
- **Ambient-Andeutung:** Solange nichts bemerkt wird, läuft eine extrem ruhige „schaue mich um…"-
  Andeutung — ein kaum sichtbares Pulsieren der Vignette oder ein langsam wanderndes warmes Glühen
  (`duration.ambientLoop`, `linear` ok). Nie ein Spinner, nie „Searching…".
- **Companion (optional):** Die Katze kann am Rand kurz erscheinen und sich setzen, dann ruhig atmen.

### 4.2 Objekt / Wort wird erkannt — Phase 1 · **Notice**

> Dauer ≈ **300 ms**. Haptik: `PRIMITIVE_LOW_TICK` (kaum spürbar).

- Ein bemerktes Wort taucht **ruhig** im Fokusbereich auf — kein Aufploppen, sondern ein sanftes
  `fade.in` + `scale.revealFrom → 1.0` mit `easing.entrance`.
- Es **atmet leicht** (`scale.noticeBreath`, langsamer Loop) — es lebt, wartet, drängt aber nicht.
- Ein zartes, warmes Aufglimmen am Rand des Worts (kein Neon, kein harter Ring). Amber-Hauch, sehr
  niedrige Intensität.
- Bei mehreren bemerkten Wörtern (max. 3, `PROJECT_VISION.md` Explore): leicht gestaffelt
  (`delay.stagger`), nie gleichzeitig — sonst entsteht visuelles Rauschen.
- **Kein** Bounding-Box-Rahmen, **kein** Confidence-Wert, **kein** Marker-Pin. Das Wort schwebt
  ruhig, bis echte Erkennung Positionen aus Bounding-Boxes liefert (dann frei innerhalb der Box,
  ohne Rahmen sichtbar).

### 4.3 Magnet / Sog startet — Phase 2 · **Lock / Charge**

> Dauer ≈ **250 ms**. Haptik: `PRIMITIVE_QUICK_RISE` (anschwellender Zug, frame-synchron).

- Der Magnet „greift": ein **spürbar anschwellender Sog** beginnt. Visuell startet ein feiner
  Lichtsog, der nach innen Richtung Bildmitte/Fokus zu wirbeln beginnt.
- Der Hintergrund (die Welt) wird **leicht radial unscharf** zur Mitte hin (`RenderEffect`-Blur ab
  API 31; darunter: dezente Sättigungs-/Helligkeitsabsenkung als Fallback). Das fokussiert die
  Aufmerksamkeit auf das gleich gefangene Wort.
- Das Wort selbst „lädt sich auf": minimaler Scale-Anstieg + zunehmendes warmes Glühen — die
  Spannung vor dem Reißen.
- Die Haptik-Rampe (`QUICK_RISE`) läuft **synchron** zum visuellen Anschwellen — beides wächst
  gemeinsam. Das ist der Moment, in dem der Nutzer den „Zug" körperlich fühlt.

### 4.4 Wort wird eingesaugt — Phase 3 · **Suck**

> Dauer ≈ **280 ms**. Haptik: `PRIMITIVE_SPIN` → unmittelbar `PRIMITIVE_QUICK_FALL`.

- Das Wort wird **auf einer leicht gekrümmten Bahn** in die Mitte gerissen — kurz, kraftvoll,
  befriedigend (`easing.suck`: flache Anfahrt, steiler Endteil = „erst zieht es, dann reißt es").
- Ein **kurzer** Partikel-/Lichtschweif folgt dem Wort (ein Schweif, **keine** Partikelflut). Das
  Wort schrumpft beim Hereinreißen Richtung ~0.2 Scale.
- Optional (API 33+, AGSL `RuntimeShader`): eine kurze **Warp- / Glas-Verzerrung** entlang der Bahn,
  als würde der Raum kurz eingesaugt. Fallback: Skalierung + Blur tragen den Effekt allein.
- Haptik: ein `SPIN` während des Reißens, sofort gefolgt von `QUICK_FALL` als „es ist drin"-Abriss —
  frame-synchron zum Ankommen in der Mitte.

### 4.5 Label / Specimen erscheint — Phase 4 · **Hold**

> Dauer ≈ **480 ms** Bloom, hält ≈ **1300 ms** (`duration.holdRead`). Haptik: `PRIMITIVE_THUD`
> (das befriedigende Einrasten).

- Aus dem Sog **blüht die Specimen-Karte** hervor: von `scale.revealFrom (0.85) → 1.0` mit
  `spring.settle` (sichtbares, gewichtiges Einrasten, leichtes Überschwingen via `easeOutBack`).
- **Ein einziger** weicher **Amber-Bloom** begleitet das Erscheinen (ein Lichtakzent, kein
  Lichtgewitter). Er pulst einmal auf und klingt ab — `duration.long`.
- Inhalt der Karte (gestaffelt, damit die Hierarchie atmet):
  1. Objektbild / Foto-Specimen + große **Zielsprache** in Grün erscheinen mit dem Bloom.
  2. Nach `delay.settle`: Quellwort darunter (`fade.in`).
  3. Danach: Kontextzeile „gerade gefangen · Kategorie" (kleiner, gedämpft, `fade.in`).
- Die `THUD`-Haptik fällt **exakt auf den Moment des Einrastens** (wenn die Feder ihren ersten Stopp
  erreicht) — Auge und Hand bestätigen gemeinsam: „du hast das jetzt".
- **Specimen-Physik (Hold-Verweildauer):** sehr dezenter Parallax/Tilt der Karte gegen den
  Welt-Hintergrund (Gyroskop, `spring.heavy`) + weicher echter Schatten — sie fühlt sich an wie ein
  aufgehobenes physisches Objekt (`KOTLIN_REWRITE_MASTERPLAN.md` §12.3).
- **Austritt:** Nach der Lesezeit zieht sich die Karte ruhig zurück/ein (kein Wegblitzen), die Welt
  schärft sich wieder (`fade.throughMid` zurück zum Live-Bild). Das Wort ist bereits gespeichert.

**Gesamtbudget:** Aktive Animation (Notice→Hold-Bloom) **< 1,4 s**. Lesbar, kurz, endet im
gehaltenen Objekt. Das ist die Synthese aus „spektakulär" (Handwerk) und „kein Cheap Juice"
(`PROJECT_VISION.md` §4.3).

### 4.6 Collection-Counter reagiert — der Fortschrittsring

> `PROJECT_VISION.md` §4.6: Fortschritt wird **gefühlt**, nicht als Quote gezählt.

- Ein **kleiner, nicht-blockierender** Fortschrittsring (oben, am Rand) füllt sich sanft Richtung
  eines weichen Ziels.
- Beim Einrasten des Specimens (Phase 4): **ein** weicher Puls + ein kurzer Amber-Funke am Ring
  (`scale.tapPop`, `spring.snappy`). **Kein** hektisches Hochzählen, **keine** große Zahl in der
  Bildmitte, **kein** Quoten-/Streak-Druck.
- Der Ring blockiert **nie** die Szene und konkurriert nie mit dem Hero-Moment (er pulst *nach* dem
  THUD, nicht währenddessen — Tier „Signal", nicht „Hero").

### 4.7 Dublette — warmes Wiedersehen („Seen again")

> `PROJECT_VISION.md` §4.5: Ein bereits gefangenes Wort wird **nicht** doppelt gezählt.

- Kein voller Catch. Stattdessen ein **kurzer, warmer Wiedererkennungs-Moment**: das Wort glimmt
  kurz amber auf, die Companion-Katze nickt (falls sichtbar), ein leiser „alter Freund
  wiedergesehen"-Schimmer.
- Haptik: nur ein einzelner `PRIMITIVE_LOW_TICK` (siehe §5). Bewusst leiser als ein neuer Fang —
  der Unterschied zwischen „neu" und „wiedergesehen" muss **fühlbar** sein.
- Hält die Kamera auch dann belohnend, wenn nichts Neues gefunden wird (harte Anforderung).

---

## 5. Haptik-Choreografie

> Haptik ist erstklassiger Bestandteil der Mechanik (`PROJECT_VISION.md` §4.4), kein Detail. Umsetzung
> über native `VibrationEffect.Composition`-Primitives (API 30+). Alle Schlüsselmomente sind
> **frame-synchron** mit der Animation. Unterhalb von API 30: bestmöglicher Fallback über einfache
> `VibrationEffect`-Pattern; der visuelle + Specimen-Moment trägt den Effekt notfalls allein.

**Designregeln für Haptik:**
- Haptik unterstreicht **Zustandswechsel und Belohnung**, nie kontinuierliche Vorgänge (kein
  Dauer-Brummen).
- Intensität ist **bedeutungstragend**: neu > wiedergesehen, selten > normal, Landung > Notice.
- Haptik ohne passende Animation ist verboten und umgekehrt — sie werden als Paar entworfen.
- Respektiere System-Einstellungen (Haptik aus → still degradieren, nie erzwingen).

### 5.1 Wort erkannt (Notice)
- **Muster:** `PRIMITIVE_LOW_TICK`, sehr niedrige Skalierung (~0.2).
- **Gefühl:** „etwas ist da" — kaum mehr als ein Hauch. Soll nicht ablenken, nur unbewusst bestätigen.
- **Timing:** exakt zum Erscheinen des bemerkten Worts.

### 5.2 Magnet startet (Lock / Charge)
- **Muster:** `PRIMITIVE_QUICK_RISE`, Skalierung anschwellend (~0.4 → 0.7).
- **Gefühl:** ein wachsender Zug in der Hand, synchron zum visuellen Sog. Der Nutzer fühlt den
  Magneten greifen.
- **Timing:** Rampe deckt sich mit der ~250 ms-Lock-Phase.

### 5.3 Wort landet (Suck → Hold)
- **Muster:** `PRIMITIVE_SPIN` (während des Reißens) → `PRIMITIVE_QUICK_FALL` (Ankunft) →
  kurze Pause → `PRIMITIVE_THUD` (Einrasten der Karte).
- **Gefühl:** das Herzstück — Hereinreißen, dann das satte, befriedigende „Klick" des Einrastens.
  Der `THUD` ist der körperliche Höhepunkt und fällt auf den ersten Feder-Stopp der Hold-Phase.
- **Timing:** strikt frame-synchron mit Phase 3 → Phase 4.

### 5.4 Mehrere Wörter werden eingesaugt
- **Nicht** mehrere volle Catch-Pattern überlagern (würde zu chaotischem Dauer-Buzz verschmelzen).
- Stattdessen: Wörter werden **leicht versetzt nacheinander** gefangen (`delay.stagger`, ~80–120 ms),
  jeder mit seinem eigenen, ggf. leicht **gedämpften** `THUD`. Der **letzte** Fang der Serie bekommt
  den vollen `THUD` als Abschluss-Akzent.
- **Gefühl:** ein rhythmisches „tk-tk-*tunk*", nicht ein Vibrationsbrei. Sauber abgegrenzte Landungen.

### 5.5 Seltenes / besonderes Wort
- **Muster:** der normale Lande-Akzent, aber der `THUD` wird durch eine reichere Sequenz ersetzt:
  `PRIMITIVE_THUD` → kurze Pause → `PRIMITIVE_TICK` → `PRIMITIVE_SPIN` (ein feines „Funkeln" nach dem
  Einrasten).
- **Visuell begleitet von** einem etwas wärmeren/längeren Amber-Bloom + (optional) Golden-Hour-Glanz
  (vgl. „erster Fund des Tages", `KOTLIN_REWRITE_MASTERPLAN.md` §12.5).
- **Diszipliniert:** Auch das bleibt warm und kurz — **kein** Casino-Jackpot-Buzz. Das Besondere ist
  ein *feineres*, nicht ein *lauteres* Gefühl.

### 5.6 Fehler / Abbruch
- **Verloren / entwischt** (Wort konnte nicht gefangen werden, Nutzer schwenkt weg): **keine**
  negative/strafende Haptik. Das Wort blendet still aus (`fade.out`), höchstens ein einzelner sehr
  weicher `LOW_TICK` als „weg". CatchLingo beschämt nie.
- **Aktion nicht möglich / Permission verweigert:** ein einzelner, neutraler `PRIMITIVE_TICK`
  zusammen mit einer ruhigen visuellen Erklärung. Kein doppelter „Error-Buzz", kein scharfes Muster.
- **Destruktive Bestätigung nötig** (Wort löschen, siehe §8): ein dezenter `TICK` beim Öffnen des
  Bestätigungsdialogs — markiert „dies ist eine bewusste Entscheidung", ohne zu alarmieren.

---

## 6. Page-Transitions

> `PROJECT_VISION.md` §7: **ruhige Fade-Through-Übergänge** zwischen allen Screens.

- **Standard zwischen Top-Level-Screens (Discover · Dictionary · Review):** Fade-Through
  (`fade.throughMid`, `duration.medium`, `easing.standard`). Der alte Screen blendet zur Cream-Canvas
  aus, der neue blendet aus ihr ein — mit ~50 % Überlappung. Es gibt **keinen** harten horizontalen
  Slide (das wirkt mechanisch/„App-ig").
- **Begleitende Mikro-Geste:** der eintretende Screen darf einen Hauch von oben kommen (Versatz
  ~8–12 dp, `easing.entrance`) — kaum wahrnehmbar, gibt aber Tiefe und Wärme.
- **Eintritt in Explore (Vollbild-Kamera):** etwas bedeutungsvoller — Fade auf Schwarz-zu-Welt mit
  dem „Kamera atmet in Fokus"-Effekt aus §4.1, nicht der Standard-Fade-Through.
- **Detail-Navigation (Dictionary-Wort → Detail):** **Shared-Element-Transition** — die Specimen-
  Karte aus der Liste wächst weich in die Detailansicht (`spring.gentle`). Das verstärkt das Gefühl
  „ich nehme genau dieses Objekt in die Hand". Zurück: dieselbe Bewegung rückwärts.
- **Bottom-Sheets / Dialoge:** von unten mit `spring.heavy` (gewichtig), Hintergrund-Scrim blendet
  sanft ein (warm, nicht hart-schwarz). Schließen: `fade.out` + nach unten, etwas schneller.
- **Regel:** Transitions sind **Tier „Functional"** — schnell, dienend, nie der Star. Sie dürfen den
  Nutzer keine spürbare Wartezeit kosten.

---

## 7. Bottom-Navigation-Motion

Drei Modi: **Discover · Dictionary · Review** (plus Vollbild-Explore).

- **Tab-Wechsel:** Der Indicator (oder der aktive Icon-Hintergrund) gleitet mit `spring.snappy` zum
  neuen Ziel — flott mit einem Hauch Nachfedern, nie ein hartes Springen.
- **Press-Feedback:** Beim Antippen schrumpft das Icon kurz auf `scale.pressDown` (`duration.instant`)
  und federt zurück (`scale.tapPop` → 1.0, `spring.snappy`) — der „Pop", der das Tippen bestätigt.
- **Icon-Zustand aktiv/inaktiv:** weicher Farb-/Fill-Crossfade (`duration.micro`, `easing.standard`).
  Aktives Icon: kräftigeres Grün; inaktiv: gedämpft. Kein abrupter Wechsel.
- **Label (falls vorhanden):** sanftes `fade.in`/Größen-Tween beim Aktivwerden.
- **Haptik:** ein einzelner sehr leichter `LOW_TICK` beim Tab-Wechsel (nicht beim bloßen Antippen des
  bereits aktiven Tabs). Dezent — Navigation ist Functional, kein Belohnungsmoment.
- **Verbot:** keine springenden/hüpfenden Icons, keine Badges, die wackeln, keine Dauer-Animation an
  der Nav.

---

## 8. Cards, Chips, Buttons und Dialoge

**Allgemeines Prinzip:** Berührbare Dinge reagieren **sofort** (`delay.none`) und **physisch**
(Press-Down + Feder zurück). Alles fühlt sich an wie ein leicht gewichtetes, echtes Objekt.

### 8.1 Buttons (primär „Start Exploring" etc.)
- **Press-Down:** `scale.pressDown` (0.96) über `duration.instant`, leichte Schatten-Reduktion (Button
  „senkt sich").
- **Release:** Feder zurück (`spring.snappy`) auf 1.0, Schatten kehrt zurück.
- **Haptik:** primäre Aktion → ein klarer `PRIMITIVE_TICK` beim Auslösen.
- **Disabled→Enabled:** weicher Farb-/Alpha-Crossfade, nie ein hartes Umschalten.

### 8.2 Cards
- **Press-Lift:** Beim Drücken senkt sich die Card minimal (`scale.pressDownSubtle`, 0.98) und der
  Schatten verringert sich; beim Loslassen „hebt" sie sich zurück (`spring.gentle`).
- **Erscheinen in Listen:** gestaffeltes `fade.in` + leichter Aufwärts-Versatz (`easing.entrance`,
  `delay.stagger`, max. 6 gestaffelt — siehe §3.1).
- **Schatten:** weich und warm (nie hartes Schwarz) — verstärkt das „physisches Objekt"-Gefühl.

### 8.3 Chips (Filter / Kategorien im Dictionary)
- **Toggle aktiv:** kurzer `scale.tapPop` (1.04 → 1.0, `spring.snappy`) + Farb-Crossfade
  (`duration.micro`). Das „Pop" macht das Aktivieren befriedigend.
- **Haptik:** leichter `LOW_TICK` beim Aktivieren.
- **Verbot:** Chips wackeln/springen nicht dauerhaft; kein „bouncy" Overshoot > 1.06.

### 8.4 Dialoge & Bestätigungen
- **Erscheinen:** `fade.in` + leichter Scale-Up von `scale.revealFrom (0.85) → 1.0` (`spring.gentle`),
  warmer Scrim blendet ein.
- **Destruktive Aktionen** (Wort löschen/ändern): ruhige Bestätigung **immer** (`PROJECT_VISION.md`
  §10 „Vertrauen") — die Animation ist hier bewusst **unaufgeregt**, nicht alarmierend. Ein dezenter
  `TICK` beim Öffnen.
- **Undo-Snackbar:** gleitet sanft von unten ein (`spring.gentle`), bleibt ruhig stehen, blendet aus.
  Der Nutzer verliert nie unerwartet einen Fund.
- **Schließen / Bestätigen:** `fade.out` + leichter Scale-Down, zügig (`duration.short`).

---

## 9. Collection- / Dictionary-Microinteractions

> Das Dictionary fühlt sich an wie eine **Sammlung realer Funde**, nicht wie eine Datenbanktabelle
> (`PROJECT_VISION.md` §7.4).

- **Neuer Eintrag erscheint:** Kehrt der Nutzer nach einem Fang ins Dictionary zurück, „landet" der
  neue Specimen mit einem kurzen Hervorheben (sanfter Amber-Schimmer, `scale.tapPop` einmalig) — ein
  leises „du hast das jetzt", kein lauter Score.
- **Listen-Aufbau:** gestaffeltes Erscheinen der Specimen-Karten (§8.2). Kategorien/Sets bauen sich
  ruhig auf.
- **Specimen → Detail:** Shared-Element-Transition (§6) — die Karte wächst in die Detailansicht.
- **Foto-Specimens** (Hidden Gem, `KOTLIN_REWRITE_MASTERPLAN.md` §6.7): Bilder erscheinen mit weichem
  `fade.in` (nie hartes „Bild-Pop"), während sie aus dem lokalen Cache laden — ein dezenter
  Shimmer/Platzhalter in Cream, kein Spinner.
- **Sanfte Sets / weiche Vollständigkeit:** ein Set füllt sich „gathering"-artig — ein weicher
  Fortschritt, **nie** eine harte Quote, **nie** ein „X/Y geschafft"-Balken mit Druck.
- **Aussprache-Mikro-Moment (optional, Could):** Tippen auf das Wort → kurzer `LOW_TICK` + ein
  dezenter Pulse am Wort, während TTS es warm ausspricht.
- **Suche/Filter:** Ergebnisse blenden per Crossfade (`duration.micro`), nie hartes Neu-Aufbauen der
  Liste. (Hinweis: Filter/Suche niedrig priorisieren — kippt sonst Richtung „Study-App",
  `KOTLIN_REWRITE_MASTERPLAN.md` §5.8.)
- **Leerer Zustand:** die Companion-Katze erscheint ruhig (kein mahnender Ton), atmet sanft (Ambient).

---

## 10. Review-Screen-Animationen

> Review ist **Erinnern, keine Schule** (`PROJECT_VISION.md` §7.5). Kein rot/grün richtig-falsch,
> keine Noten, keine Schul-Energie.

- **Karten-Erscheinen:** die Review-Karte gleitet ruhig herein (`fade.in` + leichter Versatz,
  `easing.entrance`).
- **Aufdecken (Antwort zeigen):** sanftes Reveal — entweder ein weiches Flip (3D-Rotation,
  `spring.gentle`, **nicht** schnell/gummiartig) oder ein Crossfade der Karteninhalte. Die Zielsprache
  erscheint warm, nicht als „Lösung".
- **„Erinnert" (Easy):** die Karte gleitet ruhig weg (`fade.out` + sanfter Versatz), ein leiser
  warmer Akzent (kleiner Amber-Schimmer) + ein einzelner `LOW_TICK`. **Kein** Erfolgs-Grün-Blitz,
  **kein** Häkchen-„Ding".
- **„Nochmal" (Hard / Again):** die Karte wird **nicht** als „falsch" markiert. Sie gleitet sanft
  zurück in die Queue (`spring.gentle`) — neutral, ohne Strafe, ohne rote Farbe, ohne harte Haptik.
- **„Fading memory"-Visual:** lange nicht gesehene Wörter erscheinen zart verblasst (`fade.ghost`,
  Zielalpha 0.45). Beim erfolgreichen Erinnern **frischt sich das Wort sichtbar auf** — Alpha steigt
  weich zurück auf 1.0 (`duration.long`, `easing.standard`) + ein warmer Akzent. Das ist ein leises,
  befriedigendes „aufgefrischt", das die `_isFadingWord`-Idee fühlbar macht
  (`KOTLIN_REWRITE_MASTERPLAN.md` §12.5).
- **Sitzungsende:** ruhiger Abschluss-Moment (Companion, warmer Akzent), nie eine Score-/Noten-Wand.

---

## 11. Anti-Patterns

> Diese Liste ist verbindlich und leitet sich direkt aus `PROJECT_VISION.md` §4.7, §6.7 und
> `KOTLIN_REWRITE_MASTERPLAN.md` §12.7 ab. Jede Bewegung, die hier landet, wird **nicht gebaut**.

### 11.1 Keine Casino-Effekte
- Kein Konfetti, keine Münzregen, keine Jackpot-Sounds/-Vibrationen, keine XP-Balken, keine Badges
  „überall", keine Coin-Pops. Belohnung ist **still und warm**, ein einzelner Lichtakzent.
- Keine Damping-Ratio < 0.6 (gummiartig/bouncy). Overshoot ist ein Hauch, kein Trampolin.

### 11.2 Kein hektisches Blinken
- Nichts blinkt, flackert oder pulsiert nervös. Keine schnellen, repetitiven Aufmerksamkeits-Loops.
- Ambient-Bewegung (Companion-Atem, Vignette-Pulse) ist langsam (`duration.ambientLoop`) und kaum
  bewusst wahrnehmbar.

### 11.3 Keine Tap-to-Catch-Interaktion
- **Vollständig verworfen** (`PROJECT_VISION.md` §4.7). Keine antippbaren Marker, kein „Catch"-Button,
  keine Marker-Pins, die Labels auf Objekte heften. Agency entsteht durch **Ausrichten der Kamera /
  Anvisieren**, nicht durch Antippen. Keine Animation darf eine Tap-to-Catch-Geste suggerieren.

### 11.4 Keine generischen Standard-Animationen ohne Produktgefühl
- Kein nacktes Material-Default-Easing als Default (wirkt generisch). CatchLingo nutzt seinen eigenen
  Easing-/Spring-Satz (§3).
- Kein harter horizontaler Page-Slide, keine Standard-„Ripple-only"-Buttons ohne Press-Lift.
- Kein technisches HUD: keine Scan-Linien, keine Bounding-Box-Debug-Rahmen, keine Confidence-Werte,
  keine Radar-/Science-Icons, kein „Prototype · simulated detection"-Badge, keine „Scanning…"-Spinner.
- Kein szeneblockierender zentraler Zähler, keine Quoten-/Streak-Druck-Animation.
- Keine Dauer-Partikel, keine Magnet-Trails „überall", keine Aura/Rattern.
- **Faustregel:** Wenn eine Animation in jeder beliebigen App stehen könnte, gehört sie nicht in
  CatchLingo. Jede Bewegung muss nach *diesem* Produkt schmecken: warm, gewichtet, zurückhaltend.

---

## 12. Implementierungs-Empfehlung für Compose

> Dieser Abschnitt ist die Brücke zur Umsetzung. Er bleibt **Spezifikation** (Struktur,
> Verantwortlichkeiten, Prinzipien) — kein Produktionscode. Konsistent mit der Architektur in
> `KOTLIN_REWRITE_MASTERPLAN.md` §7–§9.

### 12.1 Motion Tokens zentral definieren
- Lege **eine** zentrale Stelle für alle Tokens an (z. B. `core/designsystem/motion/`): `Durations`,
  `Easings` (als `Easing`/`CubicBezierEasing`), `Springs` (als `SpringSpec`/`spring()`-Konfigs),
  `Scales`, `Fades`, `Delays`.
- Stelle sie über das Theme bereit (eigenes `MotionTokens`-Objekt, idealerweise via
  `CompositionLocal`, analog zu Color/Type/Shape), damit kein Screen lokale Magic Numbers nutzt.
- Die Werte in §3 sind die Sollwerte. Eine Änderung dort ist eine bewusste Design-Entscheidung, kein
  Ad-hoc-Tweak im Screen.

### 12.2 Zentrale Haptic Engine
- Eine einzige `HapticPlayer`-Abstraktion in `core/haptics/` kapselt `Vibrator` /
  `VibrationEffect.Composition`. Sie bietet **benannte, semantische Pattern** statt roher Primitives:
  z. B. `notice()`, `magnetCharge()`, `landNew()`, `landSeenAgain()`, `landRare()`, `error()`,
  `navTick()`, `buttonTick()`.
- Die Engine handhabt: API-Level-Fallbacks (< API 30), Respektieren der System-/Nutzer-Einstellung
  (Haptik aus → No-Op), und Capability-Checks.
- **Frame-Sync:** Catch-Haptik wird von der Catch-Choreografie-Komponente an den definierten
  Animations-Keyframes ausgelöst (z. B. `THUD` exakt am ersten Feder-Stopp der Hold-Phase), nicht
  „ungefähr gleichzeitig" per separatem Timer.

### 12.3 Wiederverwendbare Animation Components
- **Catch-Choreografie isolieren:** Die Notice→Lock→Suck→Hold-Sequenz lebt in einer **dedizierten,
  in sich geschlossenen Komponente** (`feature/explore/catch/`), die die kombinierte Animation+Haptik-
  Timeline besitzt. So lässt sich der Signatur-Moment unabhängig perfektionieren und mit
  Screenshot-/Frame-Tests (Roborazzi/Paparazzi) visuell regressionssichern. Ziel: das „good_catch",
  das die drei `bad_*`-Recordings ersetzt.
- **Geteilte Building Blocks** in `core/designsystem/component/` bzw. `core/ui/`: `PressableScale`-
  Modifier (Press-Down + Feder), `StaggeredAppear` (Listen-Stagger), `FadeThrough` (Page-Transition),
  `AmberBloom` (der eine Belohnungs-Lichtakzent), `WordSpecimenBadge`, `CompanionCat`.
- **Standard-Mechanismen:** `animate*AsState` / `updateTransition` für diskrete State-Changes,
  `AnimatedContent` für Inhaltswechsel (mit den Motion-Tokens als `transitionSpec`), `Animatable` +
  Coroutinen für die orchestrierte Catch-Timeline. Federn über `spring(...)` mit den Token-Werten.
- **Effekte:** `graphicsLayer` für Scale/Alpha/Tilt; `RenderEffect`-Blur ab API 31 hinter einem
  Capability-Check; AGSL `RuntimeShader` (Warp/Glas) ab API 33 mit Fallback. Der Catch muss auch ohne
  Blur/Shader tragen (Haptik + Specimen-Moment).

### 12.4 Keine Logik im Compose-Build
- **Composables bleiben zustandslos und deklarativ.** Sie empfangen `UiState` und emittieren Events/
  Intents — sie treffen **keine** Geschäfts- oder Animations-Auslöse-*Entscheidungen* selbst.
- **Wann animiert wird**, entscheidet der State (vom `ViewModel` über `StateFlow`): z. B. ein
  `CatchPhase`-State (`Notice/Lock/Suck/Hold/Idle`) steuert, was die Catch-Komponente rendert. Die
  Komponente führt nur aus, was der State sagt.
- **Kein** State-Reload-bei-jeder-Navigation, keine Animation, die von Disk-IO oder Timern im
  Composable-Body abhängt. `LaunchedEffect`/`rememberCoroutineScope` orchestrieren Sequenzen, an
  State-Keys gebunden — keine Seiteneffekte in der reinen Komposition.
- So bleibt Bewegung deterministisch, testbar und frei von Recomposition-Überraschungen.

### 12.5 Performance — 60+ fps (Pflicht, kein Ziel)
- **Auf den Compositor verlagern:** Bevorzugt `graphicsLayer`-Eigenschaften (translation, scale,
  alpha, rotation) animieren — diese laufen ohne Layout/Re-Measure. **Keine** Animation von Layout-
  beeinflussenden Werten (Padding/Size) im Hot Path.
- **Recomposition-Scope klein halten:** Animations-State so tief wie möglich lesen
  (`Modifier.graphicsLayer { ... }`-Lambda, `derivedStateOf`), damit nicht ganze Subtrees pro Frame
  rekomponieren. Catch-Animation darf nicht den ganzen Explore-Screen rekomponieren.
- **Kamera + Analyse + Animation gleichzeitig:** CameraX `ImageAnalysis` mit Backpressure
  (`STRATEGY_KEEP_ONLY_LATEST`), Erkennung gedrosselt (~1–3 fps), **Analyse während der aktiven
  Catch-Choreografie pausieren** (kein Konkurrenzkampf um GPU/CPU im Hero-Moment).
- **Effekte budgetieren:** Blur/Shader nur in den kurzen Catch-Phasen, nicht dauerhaft; hinter
  Capability-Checks; auf Mittelklasse-Hardware getestet. Bei Hitze/Low-End graceful degradieren.
- **Partikel/Schweif sparsam:** ein kurzer Schweif, begrenzte Partikelzahl, kurze Lebensdauer — keine
  Dauer-Partikelsysteme.
- **Messen, nicht raten:** Frame-Timing (Macrobench/JankStats) am Catch-Moment auf echtem Gerät
  prüfen. Der Signatur-Moment ist der Performance-kritischste Pfad der App.

---

*Ende. Dieses Dokument ist die Motion-Spezifikation für CatchLingo. Bei jeder offenen Bewegungs-
Entscheidung gilt die Leitfrage aus `PROJECT_VISION.md`: macht das „die Welt bemerken" wertvoller —
oder lässt es CatchLingo mehr wie eine App wirken? Im Zweifel: Wärme, Raum, Gewicht, ein einziger
warmer Akzent.*
