Nach aktuellem Stand handelt es sich bei diesem GitHub-Projekt um die Entwicklung eines Tower-Defense-Spiels. 
Es ist davon auszugehen, dass die Projektbeschreibung im weiteren Verlauf noch ergänzt und präzisiert wird

Nützliche Extentions:  
    (Draw.io und J-Diagram) => nötig für die .drawio Datei (Klassendiagramm)
    [Draw.io](https://marketplace.visualstudio.com/items?itemName=hediet.vscode-drawio)
    [J-Diagram](https://marketplace.visualstudio.com/items?itemName=OH318.j-diagram)  


# Aufbau: Dokumentation

Die Hauptklasse `Game` ist der Startpunkt des Programms und startet das JavaFx-Programm.  
  
Damit auf Bildschirmen verschiedener Größen immer ein gleichgroßes Spielfeld vorliegt, und nicht auf manchen mehr oder weniger Türme platziert werden können, nutzt das Spiel ein internes Koordinatensystem unabhängig von der Auflösung des Bildschirms.  
Diese ist mit `Game.VIRTUAL_WIDTH` und `Game.VIRTUAL_HEIGHT` festgelegt (derzeit: 1600x900).  
Alle Positionen beziehen sich dann auf diese Größe. Es wird im nachhinein auf die passende Anzeigegröße skaliert.
  
Da es ein JavaFx-Projekt ist, muss `Game` von `Application` erben. Dadurch sind die Methoden `init()` und `start(...)` verfügbar.  
Zunächst werden in `init` die Resourcen geladen (also die JSON-Konfiguration ausgelesen und benötigte Bilder geladen) und die verfügbaren Maps und Towertypen registriert (also in einer Liste gespeichert).

## GameLoop

Die Hauptklasse `Game` beinhaltet den sogenannten GameLoop, welcher sich um die regelmäßige Ausführung der Spiellogik und das Anzeigen des Spiels kümmert.  
Dieser ist in 2 Phasen unterteilt:
- `render`: Das eigentliche Spiel wird gezeichnet (gerendert), also z.B. die Türme/Gegner/Projektile an ihrer derzeitigen Position, sowie UI-Elemente werden gezeichnet
- `update`: Die Spiellogik im Hintergrund wird ausgeführt (der Spielzustand wird aktualisiert): Gegner bewegen sich / spawnen, Projektile fliegen weiter, Leben werden abgezogen, ...

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
Dabei genügt `model.render(x, y)` um das Model (also das Bild) des Objekt an der gewünschten Position zu rendern (es stehen weitere Model-Methoden zu Verfügung).

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
Da während einem Listen-Durchlauf standartmäßig keine Elemente aus dieser entfernt werden können, müssen zu entfernende Spielobjekte `markForRemoval()` aufrufen. Nach dem `update`-Durchlauf werden alle zu entfernende Objekte aus den Listen entfernt. 

Das Spiel wird nach folgender Reihenfolge gerendert (gezeichnet):
- Map (Hintergrund)
- Tower (platzierte Türme)
- Enemies (derzeitige Gegner)
- Projectiles (Projektile)
- Shop
- UI (weitere Elemente wie Leben und Geld)

## GameObject
Jedes Spielobjekt das auf dem Bildschirm ist (z.B. Türme, Gegner, Projektile) sind auch je ein `GameObject`.  
`GameObject` ist eine `abstract`-Klasse und legt bereits folgendes für alle Objekte fest:
- `x, y`: Die aktuelle Position des Objekts auf dem Spielfeld (als `double`), kann per `=` geändert oder `getX()` / `getY()` ausgelesen werden
- `width, height`: Die Größe (Breite, Höhe) dieses Objekts (als Rechteck), für Kollisions-Checks oder Mausklick-Checks (bei `Tower` aus der Größe des Models übernommen)
- `state`: Jedes `GameObject` hält eine Referenz zum passenden `GameState` für Zugriff auf spielspezifische Methoden
Abstrakte Methoden (zugesichert)
- `render`: Soll das jeweilige Objekt auf den übergebenen `GraphicsContext` zeichnen (also auf den Bildschrim)
- `update`: Soll Spiellogik dieses Objekts berechnen (wie Bewegung, Schießen, ...)
Hilfs-Methoden
- `markForRemoval()`: Setzt dieses Objekt als "zu entfernen" und wird nach diesem `update`-Durchlauf entfernt
- `distanceTo(...)`: Errechnet den Abstand zu einem anderen `GameObject` oder eine Position
- `angleInDirection(...)`: Errechnet den Winkel/Richtung zu einem anderen `GameObject` oder eine Position in die dieses Objekt "schauen" müsste

## Tower
Jeder Tower-Typ hat seine eigene Klasse (`extends AbstractTower`) und eine passende JSON-Konfiguration (siehe unten!).  
Die Klasse definiert dabei die Logik wie dieser jeweilige Turm funktionert (Schießlogik, Projektile, Boosts, ...).  
In der JSON-Datei stehen Daten wie Name, Beschreibung, Preis, Schaden, Model, und auch Upgrades.  
Jeder platzierte Turm ist ein Objekt seiner jeweiligen Tower-Klasse.  
  
Irgendwo müssen alle verfügbaren Arten von Türmen von gespeichert sein und es muss eine allgemeine Möglichkeit geben den jeweiligen Turmtypen zu erzeugen (also zu platzieren).  
Dafür gibt es die `TowerType` Klasse (jedes Objekt repräsentiert einen Turmtypen).  
Die JSON-Konfiguration wird ausgelesen und ein `TowerType.Config` Objekt mit den entsprechenden Daten erstellt. 
Um diese Daten mit der jeweiligen Turm-Klasse zu verknüpfen speichert sich jeder `TowerType` jeweils die `TowerType.Config` und die Tower-Klasse (bzw. den passenden Konstuktur dieser Klasse über eine `TowerConstructor` Instanz).  
Ein neuer Turm dieses Typs (bzw. der entsprechenden Klasse) kann dann über den gespeicherten Konstruktor erzeugt werden.  
Alle verfügbaren Turmtypen werden in einer TowerType-List in Game gespeichert. (`Game#getTowerTypes()`)  
  
Die jeweilige Turm-Klasse erhält Zugriff auf die Daten aus der JSON-Datei über ihren Konstruktor.  
Es sollte immer der Konstruktor der Form `(GameState, TowerType.Config, double, double)` vorliegen.  
- `GameState`: gibt Zugriff auf spielbezogene Methoden und z.B. Gegner
- `TowerType.Config`: enthält die Daten aus der entsprechenden JSON-Konfiguration
- `double`: die X-Position des zu platzierenden Turms
- `double`: die Y-Position des zu platzierenden Turms
dabei sollte einfach `super(...)` mit den übergebenen Parametern aufgerufen werden, da diese in `AbstractTower` dann gespeichert werden. Verschiedene Getter geben Zugriff auf alle benötigten Daten.
  
Standartmäßig implementiert `AbstractTower` bereits `update` und `render`.  
- `render` zeigt in dieser Implementation das Model (also das Bild) zum aktuell passenden Upgrade
- `update` timed automatisch die Schüsse (je nach `speed` in der Konfiguration bzw. den der Upgrades) und ruft im passenden Interval `shoot()` aus
*(können bei Bedarf auch erneut überschrieben werden)*
Die `shoot()` Methode (*abstract*) sollte `true` zurückgeben, wenn es einen Gegner in der Range gab und geschossen hat und der Countdown zum nächtsen Schuss wieder gestartet werden soll. Gibt sie `false` zurück wird der Countdown nicht zurückgesetzt und versucht `shoot()` im nächtsen `update` erneut bis ein Gegner gefunden wurde (also `true` zurück gegeben wurde).  
  
`AbstractTower` speichert auch bereits das `level` (`int`) des Turms sowie welcher der beiden Upgrade-Optionen gewählt wurde (`upgradeTreeOne` als `boolean`: `true` -> Option 1)  
Es werden auch Getter für die zum aktuellen Upgrade passenden Attribute (Reichweite, Schaden, ...) gegeben, sowie Getter für Level, Config, usw.

## Assets
Alle Resourcen (Bilder und Konfigurationen) sind in `/assets` bzw. in den passenden Unterordnern, um eine bessere Übersicht zu haben.
```
assets
├── conf        -> JSON-Konfigurationen
│   ├── tower
│   ├── map
└── img         -> Bilddateien
    ├── enemy
    ├── map
    ├── menu
    ├── tower
    └── ...
```
Dabei werden die Bilddateien (`/img`) von den JSON-Konfigurationen (`/conf`) getrennt.  
Diese werden dann jeweils noch nach Kategorie (wie tower oder map) sortiert.  
Um ein Bild zu laden empfielt sich `Model.loadImage(category, filename)`, wobei `category` zweitere Kategorie (wie tower oder map) ist und `filename` der Name der Datei mit Endung (wie `test.png`).

### Maps (JSON-Schema)
in `/assets/conf/map/x.json`
```json
{
    "name": "Anzeigename der Karte",
    "img": "Dateiname des Hintergrundbildes, in /assets/img/map",

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
```json
{
    "name": "Anzeigename des Turms",
    "info": "Beschreibung",

    "price": 100, // Preis des Turms

    // Basiswerte des Turms (Level 0)
    "range": 100, // Angriffsreichweite
    "damage": 10, // Schaden
    "speed": 1.5, // Schüsse pro Sekunde (Durchschnitt)
    "targets": 1, // Anzahl gleichzeitiger Ziele o.ä.
    // die einzelnen Attribute können von der jweiligen Turm-Klasse auch anders interpretiert werden

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
            "img": "tower-level-1.png" // Dateiname in /assets/img/tower/...
            "width": 50, // Größe des Bilds (interne Größe!)
            "height": 50,
        }
    ],
    "models2" [
        // ...
    ]
}
```