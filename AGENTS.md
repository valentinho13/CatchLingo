# AGENTS.md

Diese Datei ist das Arbeitsbriefing fuer kuenftige KI- und Coding-Sessions im Kotlin/Jetpack-Compose
Neustart von CatchLingo.

## Erst lesen

1. Lies [PROJECT_VISION.md](PROJECT_VISION.md) vollstaendig.
2. Behandle es als verbindlichen Produktvertrag und einzige Quelle der Wahrheit.
3. Nutze [docs/reference/KOTLIN_REWRITE_MASTERPLAN.md](docs/reference/KOTLIN_REWRITE_MASTERPLAN.md)
   nur als untergeordneten technischen Plan.

Wenn irgendeine Datei, Notiz, Implementierung oder alte Referenz widerspricht, gewinnt immer
`PROJECT_VISION.md`.

## Produktregeln

- CatchLingo ist Android-first und nativ in Kotlin + Jetpack Compose.
- Die Kamera ist das Herzstueck, nicht ein Nebenfeature.
- Real-World Discovery ist der zentrale Gameplay-Loop.
- Die einzige Fangmechanik ist Magnet/Staubsauger: Notice -> Pull -> Suck -> Hold.
- Der Fang endet immer in einer gehaltenen Specimen-Karte.
- Dubletten werden nicht dupliziert, sondern als warmes Wiedersehen behandelt.
- Premium-Animation und native Haptik sind Kernfunktionalitaet.

## Verbindliche Designreferenzen

Behandle diese Assets als Zielbild fuer die App, nicht als lose Inspiration:

- `docs/design/app_icon_reference.jpg`
- `docs/design/catchlingo_ui_vision_v1.png`

Das App-Icon mit der orangefarbenen Katze ist Grundlage der Markenidentitaet. Die Katze ist
sympathischer Mentor, Begleiter und Wiedererkennungsmerkmal.

`catchlingo_ui_vision_v1.png` gibt Aufbau, Stimmung und visuelle Richtung der Anwendung vor. Neue
Screens, Komponenten und Features sollen sich wie natuerliche Erweiterungen desselben Designs
anfuehlen: warm, hochwertig, organisch, mit Cream-/Beige-/Gruen-/Amber-Farben, viel Weissraum,
modernen Karten, hochwertigen Illustrationen, sanften Schatten, klarer Typografie, dezenter Motion
und spuerbarer Haptik.

Wenn eine technische Entscheidung oder ein UI-Vorschlag dieser Designvision widerspricht, hat die
Designvision Vorrang. Konsistenz ist wichtiger als Kreativitaet.

Wenn zwei Loesungen funktional gleichwertig sind, waehle die visuell begeisternde Variante, solange
Bedienbarkeit und Performance nicht leiden.

## Nie wieder einfuehren

- Flutter- oder Dart-Code
- Tap-to-Catch oder manuelles Marker-Antippen
- Lasso-/Cowboy-Fangbilder
- Dunkle Scanner-/HUD-Visionen
- Scan-Linien, Bounding Boxes, Confidence-Werte oder Debug-Sprache in der UI
- Laute Casino-Gamification, XP-Druck, Muenzen, Konfetti-Fluten
- Dichte Dashboards als Produktidentitaet
- Manuelle Vokabelverwaltung als primaerer Flow
- Generische Material-3-Demos ohne eigene CatchLingo-Identitaet

## Technische Leitplanken

- Folge bestehenden Kotlin-/Compose-Patterns im Repo.
- Halte Mock-Erkennung und Mock-Wortdaten hinter austauschbaren Schnittstellen.
- Keine Flutter-Abhaengigkeiten, keine Flutter-Konfiguration, kein Dart.
- Neue Abhaengigkeiten nur, wenn sie einen klaren Zweck im Kotlin-Neustart haben.
- Bei UI-Arbeit die warmen Tokens aus `PROJECT_VISION.md` verwenden, keine Purple-Defaults.
- Reale Kamera-Vorschau darf nicht durch einen dauerhaft mock-only Screen ersetzt werden.
- Tests/Build pruefen, wenn Verhalten oder Projektstruktur geaendert wurden.

## Entscheidungsfrage

Macht diese Entscheidung "die Welt bemerken" wertvoller, oder laesst sie CatchLingo mehr wie eine
generische App wirken?

Im Zweifel: Waerme, Raum, echte Entdeckung und das Gefuehl eines geschaetzten Fundes.
