# CatchLingo — Project Vision

> **Dies ist die einzige maßgebliche Quelle der Wahrheit für CatchLingo.**
>
> Dieses Dokument konsolidiert und ersetzt die zuvor verstreute Vision aus `VISION.md`,
> `DESIGN_VISION.md`, `DESIGN_SYSTEM.md`, `UI_SPEC.md`, `REAL_WORLD_DISCOVERY_CONCEPT.md`,
> `ROADMAP.md`, `TECHNICAL_DECISIONS.md`, `AGENTS.md`, `SKILL.md`, `README.md` und den
> Review-/Audit-Dateien. Wo ein anderes Dokument, ein Code-Kommentar oder eine ältere Notiz
> diesem hier widerspricht, **gewinnt dieses Dokument**, und das andere ist veraltet.
>
> Die detaillierte technische Umsetzung (Architektur, Bibliotheken, Roadmap) lebt in
> `KOTLIN_REWRITE_MASTERPLAN.md` — dieses Dokument ist diesem übergeordnet und gibt die Richtung vor.
>
> **Plattform:** Kotlin + Jetpack Compose (Android-first). · **Fangmechanik:** ausschließlich
> Magnet/Staubsauger. · **Tap-to-Catch ist vollständig verworfen.**
>
> Stand: 2026-06-13

---

# Non-Negotiable Product Principles

> **Diese Prinzipien sind unveränderlich.** Sie dürfen ausschließlich durch eine ausdrückliche
> Entscheidung des Product Owners geändert werden. **Autonome Sessions, Agents oder einzelne
> Implementierungsschritte dürfen sie weder eigenständig aufweichen, umgehen noch uminterpretieren** —
> auch nicht „vorübergehend", „zur Vereinfachung" oder „als Experiment". Im Konflikt mit jeder
> späteren Implementierungsentscheidung, Roadmap oder technischen Notiz **haben diese Prinzipien
> Vorrang**.

Die folgenden Grundprinzipien definieren den unverhandelbaren Kern von CatchLingo:

- **Android-First.** CatchLingo ist eine Android-First-App. Andere Plattformen sind nachrangig und
  geben die Richtung nicht vor.
- **Nativ in Kotlin + Jetpack Compose.** Die App wird nativ mit Kotlin und Jetpack Compose entwickelt.
- **Die Kamera ist das Herzstück.** Die Kamera ist die primäre Interaktionsfläche und der Kern des
  Produkts, nicht ein Nebenfeature.
- **Real-World Discovery ist der zentrale Gameplay-Loop.** Das Entdecken und Sammeln von Wörtern aus
  der echten Umgebung bildet die zentrale Schleife des Produkts.
- **Magnet/Staubsauger ist die einzige Fangmechanik.** Die Magnet-/Staubsauger-Mechanik ist die
  ausschließlich zulässige Art, Wörter zu fangen.
- **Kein Tap-to-Catch.** Tap-to-Catch, das manuelle Antippen einzelner Marker oder vergleichbare
  Konzepte dürfen **nicht** wieder eingeführt werden.
- **Premium-Animation & -Haptik sind Kern, nicht Kür.** Premium-Qualität bei Animationen und Haptik
  ist ein erstklassiger, unverzichtbarer Bestandteil des Produkts und keine optionale Verschönerung.
- **Visual polish und emotionale Erfahrung sind First-Class-Anforderungen.** *„Visual polish,
  delightful microinteractions and emotional user experience are first-class product requirements,
  not optional enhancements."* Sie werden behandelt wie Funktionalität, nicht wie ein nachträglicher
  Schliff.
- **Hochwertig, nie casinoartig.** Animationen sollen hochwertig, flüssig und emotional belohnend
  wirken — niemals überladen, hektisch oder casinoartig.
- **Collection & Discovery vor Gamification.** Sammeln und Entdecken haben stets Vorrang vor Quizzen,
  Minispielen oder klassischer Gamification.
- **Einfachheit gewinnt.** Im Zweifel hat ein einfacherer, eleganterer und intuitiverer UX-Flow
  Vorrang vor zusätzlicher Komplexität.

---

## 0. In einem Satz

> **CatchLingo verwandelt die echte Welt in deine Vokabelquelle: Du richtest die Kamera auf
> die Dinge um dich herum, ein Magnet zieht die Wörter, die du bemerkst, sanft zu dir herein,
> und du behältst jedes als kleinen, schönen Fund.**

Kurzform der Schleife: **Discover → Catch → Remember.**

---

## 1. Was CatchLingo ist

CatchLingo ist eine **Entdeckungs-App für reale Sprache**. Man bewegt sich durch ein Zimmer,
ein Café, einen Markt, einen Bahnhof, ein Reiseziel — richtet die Kamera auf Objekte, Schilder,
Essen, Fahrzeuge, Möbel — und CatchLingo benennt sie in der Zielsprache und bewahrt sie als
persönliche Erinnerung.

Der emotionale Kern ist dieses Gefühl:

> *„Ich habe das in der echten Welt gesehen. Jetzt kenne ich das Wort dafür."*

Das Wort ist wertvoll, **weil der Nutzer ihm begegnet ist** — gebunden an Ort, Situation, Moment.

### CatchLingo ist NICHT
- ein Wörterbuch, ein Übersetzer, eine Schul-App, eine Karteikarten-App
- eine technische Objekterkennungs-Demo
- ein generischer Vokabelverwalter

### CatchLingo IST
- ein **Reisebegleiter** und ein **persönliches Wort-Gedächtnis**
- eine leichte, visuelle, neugierige Lern-Erfahrung
- ein **Feldjournal für Sprache, die in der echten Welt gefunden wurde**

> **Die Welt ist das Lehrbuch.** Andere Apps starten mit vorbereiteten Wortlisten. CatchLingo
> startet mit deiner Umgebung. Ein Hotelzimmer wird zu Vokabular. Ein Markt wird zu Vokabular.
> Eine Reise wird zu Vokabular.

---

## 2. Der Core Loop

```
Explore → Detect → Catch → Save → Remember → Explore again
   │         │        │       │        │
 Kamera   App      Magnet   Wort    Review
 auf die  bemerkt  zieht es als     (sanft,
 Welt     ein Wort herein   Fund     kein Drill)
                            bewahrt
```

Jedes Feature muss diese Schleife stärken. Tut es das nicht, wird es nicht gebaut (außer
ausdrücklich gewünscht). Die beiden emotionalen Ergebnisse, die die Erkennung bedienen muss:

1. **Neues Wort:** erkannt → übersetzt → mit minimaler Reibung ins Wörterbuch → warme Belohnung.
   Gefühl: *„Ich habe ein neues Wort in der Welt gefunden."*
2. **Bereits gefangenes Wort:** als bekannt erkannt → **niemals dupliziert** → kleiner, warmer
   Wiedererkennungs-Moment. Gefühl: *„Ich habe etwas wiedergesehen, das ich schon entdeckt hatte."*

Das zweite Ergebnis ist Pflicht: Die Kamera muss auch dann Freude machen, wenn nichts Neues
gefunden wird. **Dublettenschutz ist eine harte Anforderung.**

---

## 3. Die zwei Welten — eine Geste

CatchLingo lebt am Treffpunkt zweier visueller Welten, und seine Identität entsteht aus dem
**Kontrast zwischen ihnen**:

- **Die echte Welt** — warm, fotografisch, sonnenbeschienen, gelebt. Weiche Tiefenschärfe,
  goldenes Licht, echte Oberflächen. Hier werden Wörter gefunden. Nie steril, nie kalt, nie ein
  technischer Kamera-Feed.
- **Der gefundene Gegenstand** — eine einzelne, ruhige, klare **Specimen-Karte**, die das Wort
  trägt. Sauber, leicht erhöht über der Szene. Der Moment der Klarheit, herausgehoben aus dem
  Rauschen der Realität.

> Die Signatur-Geste von CatchLingo ist das **Hereinziehen eines Wortes aus der warmen Welt,
> bis es zu einer sauberen, gehaltenen Karte wird** — wie ein gepresstes Blatt, ein Museumsetikett,
> ein Polaroid einer Erinnerung. Verliert ein Screen eine dieser zwei Welten, ist er nicht mehr CatchLingo.

Diese zwei Schichten heißen im weiteren Verlauf:
- **Real-World-Layer** — was der Nutzer sieht (Szene, Kamerabild, Kontext). Antwortet: *„Was ist mir begegnet?"*
- **Language-Layer** — was es bedeutet (Zielwort, Quellbedeutung, später Aussprache). Antwortet: *„Wie heißt das?"*

Der Catch verbindet beide: Vor dem Fang sieht man das Objekt, nach dem Fang ist das Wort die Belohnung.

---

## 4. Die Fangmechanik — Magnet / Staubsauger *(verbindlich)*

> Dies ist die **einzige** Fangmechanik von CatchLingo. Es gibt keine Alternative.

### 4.1 Das Prinzip
Der Nutzer **richtet die Kamera auf die Welt**. CatchLingo *bemerkt* nützliche Wörter und **zieht
sie wie ein Magnet/Staubsauger sanft herein**, bis jedes zu einer **gehaltenen Specimen-Karte**
wird. Das Sammeln ist die natürliche Folge des Hinschauens — kein manuelles Arbeiten.

Die Agency des Nutzers liegt im **Ausrichten der Kamera**: Worauf man zeigt, das wird hereingezogen.
Der Nutzer dirigiert seine Aufmerksamkeit auf die Welt — er bedient keine Maschine und tippt keine
Liste ab.

### 4.2 Der vierteilige Rhythmus eines Fangs
1. **Notice** — Ein bemerktes Wort taucht ruhig in der Szene auf und atmet leicht.
2. **Pull** — Der Magnet greift: ein spürbarer, anschwellender Sog beginnt.
3. **Suck** — Das Wort wird auf einer flüssigen Bahn hereingerissen — kurz, kraftvoll, befriedigend.
4. **Hold** — Es **blüht zu einer gehaltenen Specimen-Karte** auf (Objektbild + große Zielsprache
   + Quellwort + „gerade gefangen · Kontext"), mit **einem** weichen Lichtakzent, und rastet mit
   Gewicht ein. Dies ist der emotionale Höhepunkt.

Der Endzustand ist **immer** das gehaltene Objekt. Ein Fang, der nur einen Zähler hochzählt,
ist unvollständig.

### 4.3 „Spektakulär" richtig verstanden *(aufgelöster Widerspruch)*
Frühere Vision-Texte forderten „gentle magic, not spectacle". Das bleibt wahr — aber „Spektakel"
meint hier das **Verbotene**: Casino, Konfetti-Flut, Fake-Juice, Effekte, die von der Sache ablenken.

Der Einsaug-Moment ist gleichzeitig **spektakulär im Sinne von Handwerk**: flüssig, taktil, premium,
hochwertig genug, dass man es sofort wieder tun will. Spektakulär = **gekonnt und befriedigend**,
nicht laut und chaotisch. Die Disziplin bleibt: ein Held-Moment, kurz, endet im gehaltenen Specimen,
kein Casino.

### 4.4 Spürbare Haptik
Der Einsaug-Moment ist **frame-synchron mit Haptik** unterlegt — der wachsende Sog, das Hereinreißen
und das Einrasten sind körperlich fühlbar. Native Haptik ist ein erstklassiger Bestandteil der
Mechanik, kein nachträgliches Detail. (Mapping auf konkrete Vibrationsmuster: siehe
`KOTLIN_REWRITE_MASTERPLAN.md`.)

### 4.5 Dublette = warmes Wiedersehen
Ein bereits gefangenes Wort wird **nicht doppelt gezählt**, sondern warm begrüßt — ein kurzer
Wiedererkennungs-Moment („alter Fund wiedergesehen"), der die Erinnerung stärkt.

### 4.6 Fortschritt wird gefühlt, nicht als Quote gezählt
Session-Fortschritt erscheint als ruhiges, kleines Signal (z. B. ein sanft füllender Ring), das
beim Fang kurz pulsiert. **Kein harter Zähler-Block in der Bildmitte, keine Quote, kein Streak-Druck.**
Der Fortschritt blockiert nie die Szene.

### 4.7 Ausdrücklich verworfen
- **Tap-to-Catch** (Marker, die man antippt) — vollständig verworfen.
- Ein „Catch"-Button in einem unteren Bedienpanel.
- Ein szeneblockierender zentraler Zähler.
- Feste Marker-Positionen, die Labels auf Objekte pinnen, die evtl. gar nicht da sind.
- Das alte Lasso-/Cowboy-Fangbild.
- Über-Animation: Magnet-Trails überall, Aura, Rattern, Partikelfluten.

---

## 5. Emotionale Identität & Persönlichkeit

Das Design soll konsistent einen kleinen, klaren Satz von Gefühlen erzeugen:

- **Wärme** — der Nutzer fühlt sich willkommen, wie in einem sonnigen Raum. Wärme ist die Grundstimmung, keine Dekoration.
- **Stilles Staunen** — ein Wort zu entdecken fühlt sich an, als sähe man endlich etwas, an dem man hundertmal vorbeigegangen ist. Sanfte Freude, nie Hype.
- **Besitz** — ein gefangenes Wort gehört *dir*, es kam aus deinem Tag, deiner Welt. Die App lässt einen eine persönliche, bedeutsame Sammlung anlegen — keine Aufgaben abarbeiten.
- **Ruhige Sicherheit** — die App ist unaufgeregt und ihrer selbst sicher. Sie nervt nicht, beschämt nicht, schreit nicht.

**Persönlichkeit:** Wäre CatchLingo eine Person, wäre es ein **warmer, neugieriger Begleiter auf
einem Morgenspaziergang**, der dich sanft auf Dinge aufmerksam macht und flüstert *„schau — kopi."* —
nie ein Lehrer vorne an der Tafel, nie ein Coach mit Stoppuhr, nie ein Spielautomat. Die App spricht
**über die Welt**, nicht über sich selbst (nicht über ihr „Scannen", ihre „Detektionen", ihren „Prototyp-Status").

**Wäre CatchLingo ein Gegenstand:** ein schön gemachtes Taschen-Feldjournal eines nachdenklichen
Reisenden — leinengebunden, warmes Papier, ein paar gepresste Funde und handgezeichnete Notizen,
ein Lesebändchen. Persönlich, taktil, leise premium.

---

## 6. Visuelle Identität & Design-System

### 6.1 Farb-Philosophie
Farbe trägt emotionale Rollen, nicht visuelle Vielfalt. Jeder Ton bedeutet etwas; Zurückhaltung ist die Regel.

- **Cream & warme Neutraltöne sind die Leinwand.** Die Grundstimmung jedes Screens ist warm und ruhig — nicht grellweiß, nicht dunkel.
- **Grün ist Leben und Entdeckung.** Markiert das Finden, das Lebendige, das „los". Frisch und wachsend, nie Neon, nie klinisches „System-Erfolgsgrün".
- **Amber ist Belohnung und Schatz.** Markiert den Wert-Moment — das gefangene Wort, der Begleiter. Sparsam eingesetzt, damit es kostbar bleibt.

### 6.1.1 Verbindliche Designreferenzen

Die folgenden Referenz-Assets sind **verbindliche Zielbilder** für die visuelle Identität der
Kotlin-/Jetpack-Compose-App, nicht bloße Inspiration:

- `docs/design/app_icon_reference.jpg`
- `docs/design/catchlingo_ui_vision_v1.png`

Das App-Icon mit der orangefarbenen Katze bildet die Grundlage der Markenidentität. Die Katze ist
sympathischer Mentor, Begleiter und Wiedererkennungsmerkmal.

`catchlingo_ui_vision_v1.png` repräsentiert den gewünschten Aufbau, die Stimmung und die visuelle
Richtung der gesamten Anwendung. Neue Screens, Komponenten und Features müssen sich wie natürliche
Erweiterungen derselben Designsprache anfühlen.

Verbindlich zu übernehmen sind: warmer, freundlicher, hochwertiger Gesamteindruck; natürliches
Cream-/Beige-/Grün-/Amber-Farbschema; ruhige, elegante Gestaltung statt technischer Scanner-Optik;
organische Formen und weiche Rundungen; moderne Kartenlayouts mit viel Weißraum; hochwertige
Illustrationen; Premium-Anmutung statt verspielt oder billig; emotionale und einladende
Benutzerführung; klare Informationshierarchie; harmonische Typografie; sanfte Schatten und
Tiefenwirkung; dezente Mikroanimationen; hochwertige Haptik und Motion.

Ausgeschlossen sind dunkle Cyberpunk-, HUD- oder Neonästhetik, überladene Gamification und
generische Material-3-Demos ohne eigene CatchLingo-Identität.

Falls eine technische Entscheidung oder ein UI-Vorschlag dieser Designvision widerspricht, hat die
Designvision Vorrang. Konsistenz ist wichtiger als Kreativität: lieber dieselbe hochwertige
Designsprache konsequent fortführen, als neue visuelle Richtungen einzuführen.

Wenn zwei Lösungen funktional gleichwertig sind, gewinnt die visuell begeisternde Variante, solange
Bedienbarkeit und Performance nicht leiden.

### 6.2 Palette (verbindliche Tokens)

| Rolle | Token | Hex |
|---|---|---|
| Discovery-Grün (Primary/Seed) | `green` | `#4A7C59` |
| Reward-Amber | `amber` | `#D4820A` |
| Canvas / Hintergrund (Cream) | `canvas` | `#F5F0E8` |
| Warme Oberfläche (Cards) | `warmSurface` | `#FAF6EE` |
| Discovery-Fläche | `discoverySurface` | `#F0EAD6` |
| Text primär | `textPrimary` | `#2C2418` |
| Text gedämpft | `textMuted` | `#7A6E5F` |
| Erfolg (Fläche/Rand/Text/Icon) | `success*` | `#EAF2EC` / `#6BAF7E` / `#2A5C3A` / `#3A7D50` |

**Spacing:** 4 / 8 / 12 / 16 / 24 / 32 (screen = 24). **Radius:** Button 18 · Card 24 · Panel 28 · Chip rund.
**Motion-Dauern (Richtwerte):** Mikro-Feedback 100–250 ms · Zustands-/Chip-Übergang 180–250 ms ·
Screen-Übergang 250–400 ms · In-App-Feedback 1,5–2 s.

### 6.3 Licht
Wärme entsteht in CatchLingo vor allem durch **Licht**. Die Welt hinter den Wörtern ist wie
**goldene Stunde / sanfter Morgen** beleuchtet — direktional, warm, mit weichen Schatten und etwas
Atmosphäre. Selbst die Specimen-Karten sitzen in diesem Licht mit weichem, glaubhaftem Schatten, als
wären sie echte Gegenstände. Flaches, schattenfreies, gleichmäßig ausgeleuchtetes Design entzieht
dem Produkt die Seele. Über das Kamerabild werden **keine dunklen Scrims** gelegt — höchstens eine
zarte, warme Rand-Vignette, damit die Bedienelemente sitzen.

### 6.4 Informationsdichte — „ein Held pro Screen"
CatchLingo ist **bewusst leise**. Beim Fangen gibt es ein Wort. Beim Feiern ein enthülltes Wort.
Beim Wiederholen eine Karte. Begleitende Infos (Zähler, Kontext) bleiben klein und sekundär. Dieser
großzügige Freiraum ist eines der stärksten Premium- und Emotions-Signale — er wird kompromisslos verteidigt.
Dashboards, dichte Raster, Zahlenwände sind das Gegenteil dieses Produkts.

### 6.5 Typografie
Klar, selbstbewusst, editorial. Das Wort ist gut lesbar (auch in Bewegung/draußen). **Eine** große
Held-Zeile pro Screen; alles andere ruhiger und gedämpfter. Nicht alles fett — durchgehendes
Maximal-Fett zerstört Hierarchie und damit das Gefühl von Kostbarkeit.

### 6.6 Specimen & Begleiter
- Das gefangene Wort wird als **warmer, handwerklich gestalteter Specimen** dargestellt — ein
  illustriertes Objekt mit Sammler-Charme, ein und dieselbe Behandlung überall (Fang, Wörterbuch, Review).
- Ein **freundlicher Begleiter** (die Katze, `welcome_cat.png`) ist ein warmer Führer — mehr Haustier
  als Lehrer. Er reagiert auf Funde, begrüßt alte Bekannte, wartet geduldig an leeren Tagen — nie mahnend.

### 6.7 Ausdrücklich verworfene visuelle Richtungen
| Verworfen | Ersetzt durch |
|---|---|
| Kühles Indigo `#5B5FEF` + Mint `#7DF1BD` | Warmes Grün `#4A7C59` + Amber `#D4820A` |
| Dunkle „Night"-Flächen (`#070908`, `#121615`, `#111414`) | Cream-Leinwand `#F5F0E8` |
| Technisches AR-/HUD-Bild: Scan-Linien, Bounding-Box-Debug, Confidence-Werte, Radar-/Science-Icons, „Prototype · simulated detection"-Badge | Warme Welt + ein gehaltener Specimen |
| Schul-/Karteikarten-Energie, rot/grün richtig-falsch, Noten | Sanftes „Erinnern", verblassende statt falsche Wörter |
| Laute Gamification: Konfetti, XP-Balken, Münzen, Badges überall | Stille, warme Belohnung; Fortschritt wird gefühlt |
| Dichte Dashboards, Zahlenwände | Ein Held pro Screen, großzügiger Raum |

---

## 7. Die Screens & die Erfahrung

Navigation: **Bottom-Navigation mit drei Modi — Discover · Dictionary · Review** (plus Vollbild-Explore).
Ruhige Fade-Through-Übergänge zwischen allen Screens.

### 7.1 Home / Discover
Warmer Einstieg, der das Produkt in Sekunden vermittelt: *die Welt um dich kann zu Vokabular werden.*
Tageszeit-Gruß, Begleiter-Katze, **eine** primäre Aktion „Start Exploring", ruhiger Sammelstatus,
deine Kategorien. Nicht überladen — ein Hero, eine klare Hauptaktion.

### 7.2 Explore — das Herz
Der wichtigste Screen. **Echte Vollbild-Kamera-Vorschau** der Welt. Bemerkte Wörter werden vom
Magneten hereingezogen (§4) und werden zu gehaltenen Specimens. Kleiner, nicht-blockierender
Fortschritt. Eine ruhige „schaue mich um…"-Andeutung, während nichts bemerkt wird. Keine technischen
Overlays, kein HUD, keine Statusmeldungen über die Maschine.

> Bis echte Erkennung existiert, dürfen **Mock-Detektionen** über der echten Kamera erscheinen —
> aber nur als **austauschbares Gerüst** hinter einer sauberen Schnittstelle, nie als Endzustand und
> nie mit „Mock"-Sprache an der Oberfläche.

### 7.3 Session-Erinnerung
Eine Explore-Session fühlt sich wie ein kleiner, vollständiger Moment an — *„hier sind die Wörter
aus diesem Augenblick"*, nicht ein Datenexport. Neue vs. wiedergesehene Funde, die Wörter der Session,
sanfte Wege zu „Review" oder „weiter erkunden". Wörter werden sofort beim Fang gespeichert — die
Zusammenfassung ist ein Belohnungs-Moment, kein Speicher-Schritt.

### 7.4 Dictionary — das persönliche Wort-Gedächtnis
Fühlt sich an wie **eine Sammlung realer Funde**, nicht wie eine Datenbanktabelle. Specimen-Karten,
Zielwort prominent, Quellbedeutung klar, Kategorie und **Wann-Kontext** („zuerst gefangen", „zuletzt
gesehen"). Ein Wort-Detail zeigt die Fakten ruhig und führt direkt zu „dieses Wort wiederholen".

> **Ziel der Reifung:** Das Wörterbuch soll zunehmend die Frage beantworten *„Wo bin ich diesem Wort
> begegnet?"* — über Ort, Zeit und Szene. Kontext ist das, was ein Wort zur Erinnerung macht.

### 7.5 Review — Erinnern, keine Schule
Leichtes Wiederholen gefangener Wörter, um sie zu behalten. **Nicht** die Identität des Produkts,
sondern Unterstützung der Entdeckungs-Schleife. Sanft: „erinnert" statt „richtig/falsch", lange nicht
gesehene Wörter wirken zart verblasst und frischen sich beim Erinnern auf. Kein hartes
Spaced-Repetition-System, kein Druck, keine Schul-Energie.

---

## 8. Sammlung & Erinnerung

Sammeln ist der Motivator — und es muss sich anfühlen wie **das Aufheben geschätzter Funde**, nicht
wie das Anhäufen von Punkten:

- Das gefangene Wort wird ein **sauberes, gehaltenes Objekt** — ein Souvenir, das man behalten will.
- Ein kleiner warmer Akzent markiert den Moment des Hinzufügens — ein leises „du hast das jetzt", kein lauter Score.
- Die Sammlung ist ein **persönliches Feldjournal**: jeder Eintrag erinnert nicht nur das Wort,
  sondern *wo und wann* es gefunden wurde.
- **Sanfte Sets/Kategorien** geben der Sammlung Form und ein weiches Gefühl von Vollständigkeit —
  als Sammeln gerahmt, nie als Quote.

> Sammeln in CatchLingo ist die Freude eines Souvenir-Regals, nicht der Druck eines Fortschrittsbalkens.

---

## 9. Sprache & Stimme (Copywriting)

Die App spricht wie ein warmer Begleiter über die Welt — nicht wie ein System über sich selbst.

**Bevorzugte Wörter:** discover, catch, caught, noticed, recognized, remembered, explore,
real-world words, session, found.

**Vermiedene Wörter:** homework, lesson, study, database, entry, object detection demo,
vocabulary management, confidence score, debug, ML demo, manual add.

Beispiel der Geste in Worten — vor dem Fang: das Objekt; nach dem Fang: das Wort als Belohnung
(z. B. groß die Zielsprache, darunter die Quellbedeutung, darunter „gerade gefangen · Kategorie").

**Sprach-Strategie:** UI-Lokalisierung von Beginn an (mindestens Deutsch + Englisch). Quell-/
Zielsprachpaar ist eine bewusste Wahl mit intelligentem Default aus der Geräte-Sprache. Die
Beispiel-Zielsprache der bisherigen Inhalte ist Indonesisch (`kopi`, `kursi` …).

---

## 10. Prinzipien & Leitplanken

- **Anti-Homework:** Im Zweifel Neugier und Entdeckung vor Lernstruktur — außer die Lernqualität
  litte klar darunter. Die App soll man öffnen wollen, aus Neugier, nicht aus schlechtem Gewissen.
- **Anti-Demo:** Der Nutzer soll nie denken „diese App erkennt Objekte", sondern „diese App hilft
  mir, Wörter um mich herum zu entdecken". Die Kamera ist wichtig, aber **die Kamera ist nicht das
  Produkt** — das Produkt ist die Erfahrung.
- **Effortless / intelligente Defaults:** wenig konfigurieren, viel führen. Reibung reduzieren.
- **Politur ist Funktion:** der Catch-Moment und die Haptik sind kein „nice to have".
- **Vertrauen:** Überraschung gehört in die Freude, nie in die Daten. Vor jeder löschenden oder
  verändernden Aktion an der Sammlung steht eine ruhige Bestätigung oder ein Undo — der Nutzer
  verliert nie unerwartet einen Fund. Feedback ist klar, die App ändert nichts hinter dem Rücken
  des Nutzers.

### Die Entscheidungsfrage für jede künftige Wahl
> **Macht das „die Welt bemerken" wertvoller — oder lässt es CatchLingo mehr wie eine App wirken?**
> Im Zweifel: Wärme, Raum, das Gefühl eines geschätzten Fundes.

### Tie-Breaker
Reale Entdeckung > Vokabelverwaltung · automatisches Erkennen > manuelle Eingabe · Fangen > Verwalten ·
Wärme und Freude > technische Komplexität · der kleinste nützliche Schritt > ein großer unfertiger Umbau.

### Was wir bewusst NICHT bauen (vorerst)
Accounts · Cloud-Sync · Community/Feed/Marktplatz · vollwertiges Spaced-Repetition-System ·
AR-Anchoring · Videoaufnahme · manuelle Vokabelverwaltung („add word") · schwere Einstellungen ·
iOS (Android-first). Erst muss die Schleife großartig sein.

---

## 11. Plattform-Richtung — Kotlin / Jetpack Compose *(festgeschrieben)*

CatchLingo wird **nativ in Kotlin + Jetpack Compose** gebaut (Android-first). Die frühere
Flutter-Implementierung ist ein Prototyp und gibt die Richtung nicht mehr vor.

**Warum nativ:** Der identitätsstiftende Moment — der spektakuläre Einsaug-Fang mit spürbarer
Haptik (§4) — ließ sich in Flutter wiederholt nicht in der gewünschten Qualität bauen. Nativ schaltet
genau das frei: frame-synchrone Haptik-Primitives, GPU-Effekte (Blur/Shader), saubere Kamera-
Lebenszyklus-Bindung und echten reaktiven State.

**Verbindliche technische Eckpfeiler** (Details: `KOTLIN_REWRITE_MASTERPLAN.md`):
- 100 % Jetpack Compose, Single-Activity, type-safe Compose-Navigation.
- Unidirektionaler State (ViewModel + Flow) als **eine Quelle der Wahrheit** — kein Neuladen von der Platte bei jeder Navigation.
- **Austauschbare Schnittstellen** für Erkennung und Übersetzung (heute Mock, später echt) — ohne UI-Änderung.
- CameraX (Kamera/Lebenszyklus/Analyse), ML Kit (Erkennung/Übersetzung), Room + DataStore (Persistenz), Hilt (DI).
- Native Haptik über `VibrationEffect.Composition`.
- Warme Palette (§6.2) als Compose-Theme-Tokens.

**Reihenfolge:** Zuerst eine vertikale Scheibe — der **Catch-Moment** (echte Kamera + Mock-Daten +
Einsaug + gehaltener Specimen + Haptik). Erst wenn sich *das* auf dem Gerät großartig anfühlt, wird
die Breite gebaut.

---

## 12. Geltungsbereich & abgelöste Inhalte

Dieses Dokument ist die einzige Quelle der Wahrheit. Folgendes ist damit **veraltet** und darf der
Vision nicht mehr widersprechen:

| Veraltet / abgelöst | Gültig ist jetzt |
|---|---|
| Tap-to-Catch (`NEXT_SESSION.md` Sprint 3; alte Memory-Notiz) | Magnet/Staubsauger als **einzige** Mechanik (§4) |
| „Do not reintroduce the automatic vacuum" (`NEXT_SESSION.md`) | Vacuum ist die kanonische Mechanik; gescheitert war die *Umsetzung*, nicht das Konzept |
| Kühle Indigo/Mint-Palette & dunkles Kamera-HUD (`DESIGN_DIAGNOSIS.md` beschreibt einen behobenen Altstand) | Warme Palette §6.2, kein HUD |
| „Keine Bottom-Navigation, bis gerechtfertigt" (`UI_SPEC.md`) | Bottom-Nav mit Discover/Dictionary/Review (§7) |
| „Kamera ist im MVP simuliert/Mock" | Echte Kamera-Vorschau ist Kern; Mock nur als austauschbares Gerüst |
| Flutter als Plattform, Flutter-/Codex-Implementierungsregeln (`AGENTS.md`, `SKILL.md`, `README.md`, `flutter analyze/test`) | Kotlin/Jetpack Compose (§11); für Kotlin neu zu fassen |
| Lasso-/Cowboy-Fangbild, „Mock discovery field", „Collection shelf preview", Legacy-Vokabel-Prototyp | Ersatzlos entfernt |
| Harte Session-Quote als Belohnungstreiber | Fortschritt wird gefühlt, kein Quoten-/Streak-Druck (§4.6) |

**Verhältnis zu anderen Dateien:** `KOTLIN_REWRITE_MASTERPLAN.md` ist die untergeordnete technische
Umsetzungsbegleitung (Architektur, Bibliotheken, Roadmap, Feature-Priorisierung) und muss zu diesem
Dokument konsistent bleiben. Alle übrigen Markdown-Dateien dürfen als historische Referenz bestehen
bleiben, sind aber diesem Dokument untergeordnet; bei Widerspruch gilt PROJECT_VISION.md.

---

*Ende. Wenn eine Entscheidung unklar ist, wähle Wärme, Raum und das Gefühl eines geschätzten Fundes —
und frage: macht das „die Welt bemerken" wertvoller, oder lässt es CatchLingo mehr wie eine App wirken?*
