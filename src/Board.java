/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import java.util.Scanner;

/**
 *
 * @author xxlig
 */
class Board {
    public ArrayList<ArrayList<Tile>> board;
    private ArrayList<Room> rooms;
    private ArrayList<String> roomNames;
    private int height;
    private int width;
    public ArrayList<Tile> startTiles;
    private Random rand1 = new Random();
    

    public Board() throws FileNotFoundException {
        this.board = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.roomNames = new ArrayList<>();
        this.startTiles = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File("board.csv"));
            loadBoard(sc);
        }
        catch(FileNotFoundException e) {
            System.out.println("File Not Found. Loading Default."); //will implement
            
        }
    }
    
    public final void loadBoard(Scanner scanner) {
        int x = 0, y = 0;
        
                
        String[] vals;
        Room room;
        
        while(scanner.hasNextLine()) {
            ArrayList<Tile> tiles = new ArrayList<>();
            vals = scanner.nextLine().split(",");
            y = 0;
            for(String val : vals) {
                switch(val) {
                    case "n": tiles.add(new NullTile(x,y)); break;
                    case "t": tiles.add(new Tile(x, y)); break;
                    case "C": 
                        tiles.add(new Tile(x, y));
                        this.startTiles.add(tiles.get(tiles.size()-1));
                        break;
                    default: 
                        //creates new room
                        if((val.contains("@"))) {
                            //gets room names before and after the '@'
                            String[] sRooms = val.split("@");
                            room = getRoom(sRooms[0]);
                            room.setShortcut(getRoom(sRooms[1]));
                        }
                           //door tiles end with a digit
                        else if(Character.isDigit(val.charAt(val.length()-1))) {
                            room = getRoom(val);
                            tiles.add(new RoomTile(x, y, room, true));
                            room.addDoor(tiles.get(tiles.size()-1));
                        }
                        //creates shortcut and links to the room
                        else {
                            room = getRoom(val);
                            tiles.add(new RoomTile(x, y, room, false));
                        }
                        //if it's first tile in room, room index is null
                        //sets that tile to room index
                        if(room.getRoomIndex() == null) {
                            room.setRoomIndex(tiles.get(tiles.size()-1));
                        }
                        break;
                }
                y++;
            }
            this.board.add(tiles);
            x++;
        }
        this.height = x;
        this.width = y;
        setSpecials();
        scanner.close();
    }
    
    public Room getRoom(String name) {
        Room room;
        name = name.replaceAll("[0-9C]+", "");
        if(!roomNames.contains(name)) {
            room = new Room(name);
            rooms.add(room);
            roomNames.add(name);
            }
        else room = rooms.get(roomNames.indexOf(name));
        return room;
    }
    
  public void setSpecials() {
        Random rand = new Random();
        Tile tile;
        int y;
        int x;
        for(int i = 0; i < 6; i++) {
            do {
                x = rand.nextInt(this.height-1);
                y = rand.nextInt(this.width-1);
                tile = board.get(x).get(y);
            } while(tile instanceof NullTile 
                 || tile instanceof RoomTile);
            board.get(x).remove(y);
            board.get(x).add(x, new SpecialTile(x, y));
        }
    }

    public ArrayList<Tile> getAdjTiles(Tile t){
        ArrayList<Tile> adjTile = new ArrayList<>();
        int y = t.getCoords().x;
        int x = t.getCoords().y;
        if(t instanceof RoomTile){
            if(!((RoomTile) t).getIsDoor()){
                adjTile.addAll(((RoomTile) t).getRoom().getDoors());
            }
        }
        else{
            if (x>0 && board.get(x-1).size()>y){
                if(landableTile(board.get(x-1).get(y))){
                    adjTile.add(board.get(x-1).get(y));
                }
            }
                if (x<board.size()-1 && board.get(x+1).size()>y){
                    if(landableTile(board.get(x+1).get(y))){
                        adjTile.add(board.get(x+1).get(y));
                    }
                }
            if (y>0){
                if(landableTile(board.get(x).get(y-1))){
                    adjTile.add(board.get(x).get(y-1));
                }
            }
            if (y<board.get(0).size()-1){
                if(landableTile(board.get(x).get(y+1))){
                    adjTile.add(board.get(x).get(y+1));
                }
            }
        }
        return adjTile;
    }

    public boolean landableTile(Tile t){
        boolean landable = false;
        if(t instanceof RoomTile){
            if(((RoomTile) t).getIsDoor()){
                landable= true;
            }
        }
        else if(t instanceof Tile || t instanceof SpecialTile){
            landable = true;
        }
        if(landable && t instanceof NullTile){
//            System.out.println("==============ERROR========\n"+t.toString());
            landable= false;
        }
        return landable;
    }

    /**
     *
     * @param s
     * @return
     */
    public HashMap<Integer,ArrayList<Tile>> getBFS(Tile s){
        HashMap<Integer,ArrayList<Tile>> hashMap = new HashMap<>();
        ArrayList<Tile> boardTiles = new ArrayList<>();
        ArrayList<Tile> visited = new ArrayList<>();
        Integer i = 0;
        boardTiles.add(s);
        visited.add(s);
        hashMap.put(i,boardTiles);
        while(!hashMap.get(i).isEmpty()){
            boardTiles = new ArrayList<>();
            for(Tile checkingTile: hashMap.get(i)){
                for(Tile adjacentTile: getAdjTiles(checkingTile)){
                    if(!visited.contains(adjacentTile)){
                        visited.add(adjacentTile);
                        boardTiles.add(adjacentTile);
                    }

                }
            }
            i++;
            hashMap.put(i,boardTiles);
        }
        return hashMap;
    }

    public ArrayList<Tile> humanDiceRoll(int value, Tile t){
        HashMap<Integer,ArrayList<Tile>> hashMap = getBFS(t);
        ArrayList<Tile> tilesInReach = new ArrayList<>();
        for(int i = 1; i<= value;i++){
            if(i<hashMap.size()){
                tilesInReach.addAll(hashMap.get(i));
            }
        }
        return tilesInReach;
    }
    
    public ArrayList<String> getRooms() {
        return this.roomNames;
    }
    
// This method might need a redo
    public ArrayList<Tile> getReachableTiles(int value, Tile position){
//        System.out.println("value:"+ value + " tile:" + position.toString());
//        ArrayList<Tile> tilesInReach = new ArrayList<>();
//        HashMap<Integer,ArrayList<Tile>> hashMap = getBFS(position);
//        System.out.println("hashMap size:"+hashMap.size());
//        for (int i =0; i<value;i++){
//            if(i<hashMap.size()){
//                tilesInReach.addAll(hashMap.get(i));
//            }
//        }
//         tilesInReach = hashMap.get(new Integer(1));
//        System.out.println("tiles in reach:" + tilesInReach.size());
//        return tilesInReach;
        return null;
    }
    
    public ArrayList<RoomTile> getReachableRooms(int value, Tile position){
        HashMap<Integer,ArrayList<Tile>> hashMap = getBFS(position);
        ArrayList<RoomTile> roomsInReach = new ArrayList<>();
        for(Integer i = 1; i <= value;i++){
            for(Tile t : hashMap.get(i)) {
                if (t instanceof RoomTile) {
                    if (((RoomTile) t).getIsDoor()) {
                        // ensures the room tile added
                        // references the 'room index' tile
                        roomsInReach.add((RoomTile) ((RoomTile) t).getRoom().getRoomIndex());
                    }
                }
            }
        }
        return roomsInReach;
    }
    
    
    @Override
    public String toString() {
        String s = "";
        for(ArrayList line: board) {
            for(Object tile : line) {
                if(tile instanceof NullTile) s += "NULL   ";
                else if(tile instanceof RoomTile) {
                    s +="r_"+ ((RoomTile) tile).getRoom().getName().substring(0, 3);
                    if( ((RoomTile) tile).getIsDoor() ){
                        s+= "D ";
                    }
                    else{s+="  ";}
                }
                else if(tile instanceof SpecialTile) s+="SPEC   ";
                else if(tile instanceof Tile) s+="Tile   ";
                
            }
            s += "\n";
        }
        return s;
    }
    
    
    public String toStringWithPlayers(ArrayList<Player> PL) {
        String s = "";
        HashMap<Tile,Player> tileLocation = new HashMap<>(); 
        for(Player p: PL){
            tileLocation.put(p.getPosition(), p);
        }
        
        for(ArrayList line: board) {
            for(Object tile : line) {
                if(tileLocation.containsKey(tile)){
                    s +="#"+ tileLocation.get(tile).name.substring(1, 6)+"#";
                }
                else{
                    if(tile instanceof NullTile) s += "NULL   ";
                    else if(tile instanceof RoomTile) {
                        s +="r_"+ ((RoomTile) tile).getRoom().getName().substring(0, 3);
                        if( ((RoomTile) tile).getIsDoor() ){
                            s+= "D ";
                        }
                        else{s+="  ";}
                    }
                    else if(tile instanceof SpecialTile) s+="SPEC   ";
                    else if(tile instanceof Tile) s+="Tile   ";
                }
            }
            s += "\n";
        }
        return s;
    }
}
