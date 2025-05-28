public class Enemy1 extends Enemy_Abstract{
    
    public Enemy1(String type, int health, int reward, int xOrt, int yOrt, int strafe, int speed) {
        super(type, health, reward, xOrt, yOrt, strafe, speed);
    }

  
    public Pathtype currentPathtype(){ //Pathtype fehlt
        return ...;
    }

    @Override
    public void moveEnemy(Pathtype end) {
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
