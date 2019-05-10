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

    /**
     * Coordinates to uniquely identify the position of the tile on the game board
     */
    protected class Coords{
        public int x;
        public int y;
        public Coords(int x, int y){
            this.y = x;
            this.x = y;
        }
    }

    protected Coords c;

    /**
     * Tile Constructor
     * @param x
     * @param y
     */
    public Tile(int x, int y){
        this.c = new Coords(x,y);
    } 
    
    public Coords getCoords(){return c;}
    @Override
    public String toString(){return this.getClass().getSimpleName()+ "  x:"+c.x+"   y:"+c.y;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tile tile = (Tile) o;
        return c.x == tile.c.x && c.y == tile.c.y;
    }
}

/**
 * Sub class of Tile. Only created if Tile contains a room
 */
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

/**
 * Sub class of Tile. Only created if Tile is a Special tile
 */
class SpecialTile extends Tile{  
    public SpecialTile(int x, int y) {
        super(x, y);
    }  
}

/**
 * Sub class of tile. Only created if the tile is not a landable tiles for players on the board.
 */
class NullTile extends Tile{   
    public NullTile(int x, int y) {
        super(x, y);
    }
    
}