/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

/**
 *
 * @author xxlig
 */

class Tile {
    
    protected class Coords{
        public int x;
        public int y;
        public Coords(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
      
    protected Coords c;
    
    public Tile(int x, int y){
        this.c = new Coords(x,y);
    } 
    
    public Coords getCoords(){return c;}
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
                System.out.println(this+" already been added to doors in "+r.getName());
            }    
        }  
    }
    
    public Room getRoom(){return this.r;}
    public boolean isDoor(){return this.isDoor;}
    
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