public class Enemy extends Enemy_Abstract{
    
    private String enemyType;
    private int enemyHealth;
    private int killReward;
    private int movementSpeed;
    private int penalty;
    private int x,y;


    public Enemy (String type, int health, int reward, int xOrt, int yOrt, int strafe, int speed){
        enemyType = type;
        enemyHealth = health;
        killReward = reward;
        x = xOrt; //spawn ort
        y = yOrt;
        penalty = strafe;
        movementSpeed = speed;
    }

    public void reduceHealth(int damage){
        enemyHealth -= damage;
        if(enemyHealth<=0){
            die();
        }
    }

    private int die() {
        //enemy Objekt entfernen
        return killReward;

    }

    public int getHealth(){
        return enemyHealth;
    }


    public int RandomTime(){

    }


    public int RandomPlace(){

    }

    public boolean darfIchSpawnen(int x, int y){ //Spielfeldgröße: 1920x1080
        if(x>384) return true; //pixel, 20% des rechten Randes sind kein Spielfeld
        else return false;
    }

    public Pathtype currentPathtype(){ //Pathtype fehlt
        return ...;
    }

    public void moveEnemy(Pathtype end){
        if(currentPathtype()==end) //Endposition
        {
            die();
           // return penalty; //wenn Gegner den Endpunkt erreicht, bekommt der Spieler Strafe
            
        }
        else{
            if(currentPathtype()==path1){
                //gerade nach vorne
                setPosition(x,y+100);
                //Bewegung Animation??
            }
            if(currentPathtype()==path2){
                //quer
                setPosition(x+100,y);
            }
        }
       
        
    }

    public Enemy spawn(){
        //put enemy in position X, Y
    }


}
