/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

/**
 *
 * @author xxlig
 */
class Room {
    private ArrayList<Tile> doors;
    private Tile roomIndex;
    private Room shortcut;
    private String name;

    /**
     *
     * @param name
     */
    public Room(String name){
        this.roomIndex = null;
        this.shortcut = null;
        this.name = name;
        doors = new ArrayList<>();
    }

    /**
     *
     * @param r
     * @return
     */
    public boolean setShortcut(Room r){
        if(this.shortcut ==null){
                this.shortcut = r;
                return true;
        }
        return false;
    }

    /**
     *
     * @param t
     * @return
     */
    public boolean setRoomIndex(Tile t){
        if(this.roomIndex ==null){
                this.roomIndex = t; 
                return true;
        }
        return false;
    }

    /**
     *
     * @param t
     * @return
     */
    public boolean addDoor(Tile t){
        if (!doors.contains(t)){
            doors.add(t);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        return doors == room.doors &&
                roomIndex == room.roomIndex &&
                shortcut == room.shortcut &&
                name.equals(room.name);
    }
    /**
     *
     * @return
     */
    public ArrayList<Tile> getDoors(){return doors;}
    public String getName(){return name;}
    public Tile getRoomIndex(){return roomIndex;}
    public Room getShortcut(){return shortcut;} 
}
