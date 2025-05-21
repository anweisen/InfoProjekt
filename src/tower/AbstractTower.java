package tower;

public abstract class AbstractTower {

    double x, y; // Position auf dem Spielfeld (2D-Koordinaten)
    double range; // Reichweite des Turms
    double damage; // Schaden pro Schuss
    double fireRate; // Zeit zwischen Schüssen (z.B. in Millisekunden oder Ticks)
    long lastShotTime; // Zeitstempel des letzten Schusses
    String projectileType; // Art des Projektils ("Pfeil", "Feuerball", etc.)
    double cost; // Baukosten
    double level; // Upgrade-Level

    public abstract double getX();

    public abstract double getY();

}
