public abstract class Enemy_Abstract {
    
    private String enemyType;
    private int enemyHealth;
    private int killReward;
    private int movementSpeed;
    private int penalty;
    private int x,y;

    public Enemy_Abstract (String type, int health, int reward, int xOrt, int yOrt, int strafe, int speed){
        enemyType = type;
        enemyHealth = health;
        killReward = reward;
        x = xOrt; //spawn ort
        y = yOrt;
        penalty = strafe;
        movementSpeed = speed;
    }

    public abstract void moveEnemy(Pathtype end); // Wie werden Wege gespeichert?

    public abstract Pathtype currentPathtype(); // Gibt den aktuellen Pfadtyp zurück

    public String getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(String enemyType) {
        this.enemyType = enemyType;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public void setEnemyHealth(int enemyHealth) {
        this.enemyHealth = enemyHealth;
    }

    public boolean darfIchSpawnen(int x, int y){ //Spielfeldgröße: 1920x1080
        if(x>384) return true; //pixel, 20% des rechten Randes sind kein Spielfeld
        else return false;
    }

    public void reduceHealth(int damage) {
        enemyHealth -= damage;
        if (enemyHealth <= 0) {
            die();
        }
    }

    public int die() {
        // enemy objekt entfernen
        return getKillReward(); 
    }

    public int getKillReward() {
        return killReward;
    }

    public void setKillReward(int killReward) {
        this.killReward = killReward;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
