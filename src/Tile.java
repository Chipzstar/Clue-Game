/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author xxlig
 */

class Tile {
    
    protected class Coords{
        public int x;
        public int y;
        public Coords(int x, int y){
            this.y = x;
            this.x = y;
        }
    }
      
    protected Coords c;
    
    public Tile(int x, int y){
        this.c = new Coords(x,y);
    } 
    
    public Coords getCoords(){return c;}
    @Override
    public String toString(){return this.getClass().getSimpleName()+ "  x:"+c.x+"   y:"+c.y;}
}

class RoomTile extends Tile{
    private Room r;
    private boolean isDoor;
    
    public RoomTile(int x, int y, Room r, boolean b) {
        super(x, y);
        this.r = r;
        this.isDoor = b;
        if(this.isDoor){
            if(!r.addDoor(this)){
                System.out.println(this+" already been added to doors in "+r);
            }    
        }  
    }
    
    public Room getRoom(){return r;}
    public boolean getIsDoor(){return isDoor;}
     public String toString(){return this.getClass().getSimpleName()+ "Room: "+this.getRoom().getName()
             +"    x:"+c.x+"   y:"+c.y;}
    
}

class SpecialTile extends Tile{  
    public SpecialTile(int x, int y) {
        super(x, y);
    }  
}

class NullTile extends Tile{   
    public NullTile(int x, int y) {
        super(x, y);
    }
    
}