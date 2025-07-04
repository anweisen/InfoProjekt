Nach aktuellem Stand handelt es sich bei diesem GitHub-Projekt um die Entwicklung eines Tower-Defense-Spiels. 
Es ist davon auszugehen, dass die Projektbeschreibung im weiteren Verlauf noch ergänzt und präzisiert wird

Nützliche Extentions:  
    (Draw.io und J-Diagram) => nötig für die .drawio Datei (Klassendiagramm)
    [Draw.io](https://marketplace.visualstudio.com/items?itemName=hediet.vscode-drawio)
    [J-Diagram](https://marketplace.visualstudio.com/items?itemName=OH318.j-diagram)  


# Aufbau: Dokumentation

Die Hauptklasse `Game` ist der Startpunkt des Programms und startet das JavaFx-Programm.  
  
Damit auf Bildschirmen verschiedener Größen immer ein gleich großes Spielfeld vorliegt, 
und nicht auf manchen mehr oder weniger Türme platziert werden können, nutzt das Spiel ein internes Koordinatensystem unabhängig von der Auflösung des Bildschirms.  
Diese ist mit `Game.VIRTUAL_WIDTH` und `Game.VIRTUAL_HEIGHT` festgelegt (derzeit: 1600x900).  
Alle Positionen beziehen sich dann auf diese Größe. Es wird im Nachhinein auf die passende Anzeigegröße skaliert.
  
Da es ein JavaFx-Projekt ist, muss `Game` von `Application` erben. Dadurch sind die Methoden `init()` und `start(...)` verfügbar.  
Zunächst werden in `init` die Ressourcen geladen (also die JSON-Konfiguration ausgelesen und benötigte Bilder geladen) und die verfügbaren Maps und Towertypen registriert (also in einer Liste gespeichert).  
In `start` wird das Fenster sowie die Zeichenoberfläche erstellt und der GameLoop gestartet.

## GameLoop

Die Hauptklasse `Game` beinhaltet den sogenannten GameLoop, welcher sich um die regelmäßige Ausführung der Spiellogik und das Anzeigen des Spiels kümmert.  
Dieser ist in 2 Phasen unterteilt:
- `render`: Das eigentliche Spiel wird gezeichnet (gerendert), also z.B. die Türme/Gegner/Projektile an ihrer derzeitigen Position, sowie UI-Elemente werden gezeichnet
- `update`: Die Spiellogik im Hintergrund wird ausgeführt (der Spielzustand, der später gezeichnet wird, wird aktualisiert): Gegner bewegen sich / spawnen, Projektile fliegen weiter, Leben werden abgezogen, ...

Diese beiden Operationen werden immer abwechselnd ausgeführt um ein flüssiges Spielerlebnis zu erzeugen (zwischen jedem Zeichnen muss sich z.B. der Gegner ein Stück bewegen, dass es flüssig aussieht).  

#### update und deltaTime
Die Frame-Rate (FPS), also die Anzahl wie oft das Spiel pro Sekunde neu gezeichnet wird, beinflusst somit auch die Häufigkeit wie oft die Spiellogik berechnet wird.  
Würde sich ein Gegner in jedem `update` fest um 10 Pixel bewegen, so wäre er bei 60fps doppelt so schnell wie bei 30fps.  
  
Dafür gibt es `deltaTime` (wird bei `update` immer übergeben):  
`deltaTime` ist die Zeit in Sekunden die seit dem letzten Update vergangen ist (übersteigt aber niemals `1`: double zwischen `0` und `1`).  
Soll sich bspw. ein Gegner mit einer Geschwindigkeit von 100 Pixel pro Sekunde über den Bildschrim bewegen, so kann die Bewegungslogik `x += 100 * deltaTime` in der `update` Methode lauten.
Bei 30fps wäre `deltaTime` also jedes mal doppelt so groß wie bei 60fps, der Gegner würde sich zwischen jedem Frame immer doppelt so weit bewegen. Uber den gleichen Zeitraum hinweg aber in beiden Fällen genau gleich weit!  
`deltaTime` ist auf `1` begrenzt um bei Lags unerwartetes Verhalten zu verhindern, das in manchen Fällen zu einer Abwärtsspirale der Performance führen kann. (lange Zeit zwischen Frames -> großes deltaTime -> große, unerwartete Bewegungen)

#### render
Alle `render` Operationen beziehen sich immer auf ein übergebenes `GraphicsContext` Objekt (von JavaFx).  
Auf dieses kann mit Methoden wie `fillRect`, `drawImage`, `fillText` gezeichnet werden.  
  
Spielobjekte (`GameObject`) können die Vereinfachung über ein `Model` nutzten (wird für Türme bspw. automatisch geladen).
Dabei genügt `model.render(graphics, x, y)` um das Model (also das Bild) des Objekt an der gewünschten Position zu rendern (es stehen weitere Model-Methoden zu Verfügung).
`model.renderRotated(graphics, x, y, angle)` zeichnet das Model um den angegebenen Winkel in Grad, um bspw. Türme in die Richtung zu drehen in die sie schießen.

## State
`Game` beinhaltet `currentState` (Typ: `State` *abstract*).  
Der GameLoop gibt die `render` und `update` Operationen an den derzeitigen State weiter, da je nach derzeitigem Spielzustand andere Logik erfolderlich ist.  
Es gibt dabei verschiedene Arten von States wie: 
- `MenuState`: Zeigt das Hauptmenü an und hat Logik wie die Map-Auswahl 
- `GameState`: das eigentliche Spiel
   
außerdem werden Mausklicks über `handleClick(x, y)` an den `State` weiter gegeben.  

Wird der `currentState` per `setCurrentState` geändert wird `dispose()` des alten States aufgerufen. Dieser sollte gehaltene Resourcen freigeben (`render` oder `update` werden nicht mehr aufgerufen).

## GameState
Der `GameState` ist der Ausgangspunkt für die eigentliche Spielprogrammierung. Dieser enthält alle Daten zum aktuellen Spielstand wie platzierte Türme, die derzeit gespielte Karte, übrige Leben, Geld usw...  
  
Platzierte Türme, Gegner und andere Spielobjekte (Projektile) werden jeweils in einer Liste gespeichert.  
Da während einem Listen-Durchlauf standardmäßig keine Elemente aus dieser entfernt werden können, müssen zu entfernende Spielobjekte `markForRemoval()` aufrufen. 
Nach dem `update`-Durchlauf werden alle zu entfernende Objekte aus den Listen entfernt. 

Das Spiel wird nach folgender Reihenfolge gerendert (gezeichnet):
- Map (Hintergrund)
- Tower (platzierte Türme)
- Enemies (derzeitige Gegner)
- Projectiles (Projektile)
- Particles (Partikel wie Explosionen)
- Shop
- UI (weitere Elemente wie Leben und Geld)

*Die nach Typen getrennte Reihenfolge soll eine höhere Konsistenz sowohl in der Anzeige als auch in der Logik ermöglichen (Gegner immer über Türmen / Bewegung Gegner immer vor Schüssen / ...).
Dafür ist eine je eigene Liste der Entitäten die wohl effizienteste und einfachste Lösung*

## GameObject
Jedes Spielobjekt auf dem Bildschirm (z.B. Türme, Gegner, Projektile) sind auch je ein `GameObject`.  
`GameObject` ist eine `abstract`-Klasse und legt bereits folgendes für alle Objekte fest:
- `x, y`: Die aktuelle Position des Objekts auf dem Spielfeld (als `double`), kann per `=` geändert oder `getX()` / `getY()` ausgelesen werden
- `width, height`: Die Größe (Breite, Höhe) dieses Objekts (als Rechteck), für Kollisions-Checks oder Mausklick-Checks (bei `Tower` aus der Größe des Models übernommen)
- `state`: Jedes `GameObject` hält eine Referenz zum passenden `GameState` für Zugriff auf spielspezifische Methoden

Abstrakte Methoden ("zugesichert")
- `render`: Soll das jeweilige Objekt auf den übergebenen `GraphicsContext` zeichnen (also auf den Bildschrim)
- `update`: Soll Spiellogik dieses Objekts berechnen (wie Bewegung, Schießen, ...)

Hilfs-Methoden
- `markForRemoval()`: Setzt dieses Objekt als "zu entfernen" und wird nach diesem `update`-Durchlauf entfernt (siehe `GameState` für Details)
- `distanceTo(...)`: Errechnet den Abstand zu einem anderen `GameObject` oder eine Position
- `angleInDirection(...)`: Errechnet den Winkel/Richtung zu einem anderen `GameObject` oder eine Position in die dieses Objekt "schauen" müsste.  
  Dieser kann dann anschließend in `render` verwendet werden (via `model.renderRotated(...)`), um das Objekt auch wirklich in die Richtung gedreht anzuzeigen (z.B. bei Türmen in Richtung des Schusses).

## Tower
Jeder Tower-Typ hat seine eigene Klasse (`extends AbstractTower`) und eine passende JSON-Konfiguration (siehe unten!).  
Die Klasse definiert dabei die Logik wie dieser jeweilige Turm funktioniert (Schießlogik, Projektile, Boosts, ...).  
In der JSON-Datei stehen Daten wie Name, Beschreibung, Preis, Schaden, Model, und auch Upgrades.  
Jeder platzierte Turm ist ein Objekt seiner jeweiligen Tower-Klasse.  
  
Irgendwo müssen alle verfügbaren Arten von Türmen von gespeichert sein und es muss eine allgemeine Möglichkeit geben den jeweiligen Turmtypen zu erzeugen (also zu platzieren).  
Dafür gibt es die `TowerType` Klasse (jedes Objekt repräsentiert einen Turmtypen).  
Die JSON-Konfiguration wird ausgelesen und ein `TowerType.Config` Objekt mit den entsprechenden Daten erstellt. 
Um diese Daten mit der jeweiligen Turm-Klasse zu verknüpfen speichert sich jeder `TowerType` jeweils die `TowerType.Config` und die Tower-Klasse (bzw. den passenden Konstuktur dieser Klasse über eine `TowerConstructor` Instanz).  
Ein neuer Turm dieses Typs (bzw. der entsprechenden Klasse) kann dann über den gespeicherten Konstruktor erzeugt werden.  

Die Towertypen werden wie bereits erwähnt in `init` in `Game` geladen und registriert.
Dies geschieht über die `registerTower`- Methode, ein Beispiel dafür sähe wie folgt aus:  
(`towerName.json` ist die JSON-Konfiguration des Turmes (siehe weiter unten), befindet sich in `/assets/conf/tower/` und enthält jegliche Daten zum Typ.  
`TowerKlasse` ist die Klasse des Turms, die von `AbstractTower` erbt. `TowerKlasse::new` ist der Konstruktor der Klasse, siehe unten für Details)
```java
registerTower(TowerType.Config.load("towerName.json"), TowerKlasse::new);
```

Alle verfügbaren Turmtypen werden in einer TowerType-List in Game gespeichert. (`Game#getTowerTypes()`)  
  
Die jeweilige Turm-Klasse erhält Zugriff auf die Daten aus der JSON-Datei über ihren Konstruktor.  
Es sollte immer der Konstruktor der Form `(GameState, TowerType.Config, double, double)` vorliegen.  
- `GameState`: gibt Zugriff auf spielbezogene Methoden und z.B. Gegner
- `TowerType.Config`: enthält die Daten aus der entsprechenden JSON-Konfiguration
- `double`: die X-Position des zu platzierenden Turms
- `double`: die Y-Position des zu platzierenden Turms

dabei sollte einfach `super(...)` mit den übergebenen Parametern aufgerufen werden, da diese in `AbstractTower` dann gespeichert werden. Verschiedene Getter geben Zugriff auf alle benötigten Daten.
  
Standardmäßig implementiert `AbstractTower` bereits `update` und `render`.  
- `render` zeigt in dieser Implementation das Model (also das Bild) zum aktuell passenden Upgrade
- `update` timed automatisch die Schüsse (je nach `speed` in der Konfiguration bzw. den der Upgrades) und ruft im passenden Interval `shoot()` aus

*(können bei Bedarf auch erneut überschrieben werden)*  

Die `shoot()` Methode (*abstract*) sollte `true` zurückgeben, wenn es einen Gegner in der Range gab und geschossen hat und der Countdown zum nächtsen Schuss wieder gestartet werden soll. Gibt sie `false` zurück wird der Countdown nicht zurückgesetzt und versucht `shoot()` im nächtsen `update` erneut bis ein Gegner gefunden wurde (also `true` zurück gegeben wurde).  
  
`AbstractTower` speichert auch bereits das `level` (`int`) des Turms sowie welcher der beiden Upgrade-Optionen gewählt wurde (`upgradeTreeOne` als `boolean`: `true` -> Option 1)  
Es werden auch Getter für die zum aktuellen Upgrade passenden Attribute (Reichweite, Schaden, ...) gegeben, sowie Getter für Level, Config, usw.

## Particle

`Particle` ist eine `abstract` Klasse, die eine simple Grundlage für Partikel wie Explosionen oder andere einfache Animationen bilden soll.  
Alle derzeitig sichtbaren Partikel werden in einer Liste im `GameState` gespeichert und können über `GameState#registerParticle(Particle)` hinzugefügt werden.
Sie befolgen dieselbe Logik wie `GameObject` (es wird extended, also geerbt), also Methoden wie `render`, `update`, `markForRemoval` usw.  
`Particle` implementiert bereits standardmäßig `render` und `update`
- `update` zählt die interne Zeit (den Fortschritt der Animation) `progress` von 0 bis 1 hoch, wobei `1` das Ende der Animation ist.  
  Dabei gibt `lifetime` die Zeit in Sekunden an, wie lange der Partikel sichtbar sein soll, also wie lange es dauert bis von `0` bis `1` hochzählt wurde.
- `render` ruft die `abstract` Methode `render(GrahpicsContext, double)` auf, wobei der zusätzliche Parameter `time` (`double`) den Fortschritt der Partikel-Animation darstellt.  
  Jedoch ist `time` nicht gleich `progress`, sondern errechnet sich anhand des vorgebenden `Timing` (via `Timing#translate(double)`), um eine nicht-lineare Animation zu ermöglichen.  
  `Timing` stellt konstante Optionen wie `Timing.LINEAR` (linear), `Timing.EASE_OUT_CUBIC` (zum Ende hin langsamer) oder `Timing.EASE_IN_CUBIC` (am Anfang langsamer) bereit.

Eine einfache Partikel Implementierung wird durch `Particle.Image` bereitgestellt, die ein übergebenes `Model` von Anfang bis Ende hochskaliert, 
und somit eine simple Partikel-Animation erzeugen kann, wie eine Explosion (eigentlich ein 2D-Bild davon) die langsam entsteht und größer wird, anstatt einfach aus dem Nichts sichtbar zu werden und wieder zu verschwinden.  

Es sind aber natürlich auch weitere Particle-Implementation denkbar, auch wenn so nur einfache Animationen möglich sind.

## Map

Eine Map-Instanz ist eine Karte, auf der das Spiel gespielt werden kann und hält alle Daten zu dieser.  
Eine Liste an Maps wird in `Game` gespeichert und kann über `Game#getMaps()` abgerufen werden.  
Maps werden in `init` geladen und registriert, ähnlich wie die Tower-Typen:
```java
registerMap(Map.loadMap("mapName.json"));
```
Aus der JSON-Konfiguration gehen bereits die relevanten Daten für diese hervor (siehe unten `Assets > Maps`).
Diese werden in der `Map` Klasse gespeichert und können über Getter abgerufen werden:
- `getName()`: `String`: Name der Map 
- `getImage()`: `Image`: Hintergrundbild der Map (wird automatisch aus dem angegeben `img` Pfad geladen)
- `getCanPlace(x, y)`: `boolean`: Gibt an, ob auf der Map an der Position `x, y` ein Turm platziert werden kann (oder ob es auf einem Pfad oder Hindernis liegt).  
  Diese Informationen gehen aus einem Bild hervor (`allowPlace` Pfad in der JSON), welches erlaubte Positionen mit schwarzen Pixeln markiert.
- `getStart()`: `Waypoint`: Startpunkt der Karte (Spawn-Punkt der Gegner)
- `getEnd()`: `Waypoint`: Endpunkt der Karte (Ziel-Punkt der Gegner)
- `getWaypoints()`: `Waypoint[]`: Punkte die, wenn verbunden, den Pfad der Karte erzeugen (für Wegfindung benötigt).  
- `getSplinePoints()`: `List<Waypoint>`: Punkte die aus den Waypoints mithilfe *Catmull-Rom-Spline-Interpolation* errechnet werden (liegen näher zusammen).
  Dabei liegen alle diese Punkte auf einer glatten Kurve ohne Knicke, die durch alle angegebenen Waypoints verläuft (für eine flüssige Bewegung der Gegner, die nicht abgehackt ist).  
  (*sie werden im Vorhinein berechnet, um die Anzahl komplexer mathematischer Berechnungen pro update-Call zu minimieren*)

## Assets
Alle Ressourcen (Bilder und Konfigurationen) sind in `/assets` bzw. in den passenden Unterordnern, um eine bessere Übersicht zu haben.
```
assets
├── conf        -> JSON-Konfigurationen
│   ├── tower
│   ├── map
│   └── ...
└── img         -> Bilddateien
    ├── enemy
    ├── map
    ├── menu
    ├── tower
    └── ...
```
Dabei werden die Bilddateien (`/img`) von den JSON-Konfigurationen (`/conf`) getrennt.  
Diese werden dann jeweils noch nach Kategorie (wie tower oder map) sortiert.  
Um ein Bild zu laden, empfiehlt sich `Model.loadImage(category, filename)`, wobei `category` zweitere Kategorie (wie tower oder map) ist und `filename` der Name der Datei mit Endung (wie `test.png`).

### Maps (JSON-Schema)
in `/assets/conf/map/x.json`
```jsonc
{
    "name": "Anzeigename der Karte",
    "img": "Dateiname des Hintergrundbildes, in /assets/img/map",
    "allowPlace": "Bilddateiname: erlaubte Platzierungen, in /assets/img/map",

    "start": { // Startpunkt der Gegner
        "x": 123,
        "y": 456
    },
    "end": { // Zielpunkt der Gegner
        "x": 123,
        "y": 456
    },
    "waypoints": [ // Zwischenpunkte des Map-Pfades für Wegfindung
        {
            "x": 123,
            "y": 456
        },
        {
            "x": 123,
            "y": 456
        }
    ]
}
```
*Alle Positionen beziehen sich dabei auf die Größe des internen Spielfelds*

### Tower (JSON-Schema)
in `/assets/conf/tower/x.json`  
per `TowerType.Config.load(...)` geladen
```jsonc
{
    "name": "Anzeigename des Turms",
    "info": "Beschreibung",

    "price": 100, // Preis des Turms

    // Basiswerte des Turms (Level 0)
    "range": 100, // Angriffsreichweite
    "damage": 10, // Schaden
    "speed": 1.5, // Schüsse pro Sekunde (Durchschnitt)
    "targets": 1, // Anzahl gleichzeitiger Ziele o.ä.
    // die einzelnen Attribute können von der jeweiligen Turm-Klasse auch anders interpretiert werden

    "upgrades1": [ // Upgrades: Upgrade-Pfad Möglichkeit 1
        {
            "name": "Upgrade-Name",
            "info": "Beschreibung",
            "price": 100, // Preis des Upgrades

            // Attribute die geupgraded werden (Werte werden addiert!)
            // (gleichbleibende können entfernt werden)
            "range": 100,
            "damage": 10,
            "speed": 1.5,
            "targets": 1
        }
    ],
    "upgrades2": [ // Upgrades: Möglichkeit 2
        // ... (wie bei upgrades1)
    ],

    "model": {
        "img": "tower.png", // Dateiname in /assets/img/tower/...
        "width": 50, // Größe des Bilds (interne Größe!)
        "height": 50,
    },

    "models1": [ // Bilder für geupgradete Türme
        {
            "img": "tower-level-1.png", // Dateiname in /assets/img/tower/...
            "width": 50, // Größe des Bilds (interne Größe!)
            "height": 50,
        }
    ],
    "models2": [
        // ...
    ]
}
```
