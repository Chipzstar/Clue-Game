/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;


import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author xxlig
 */
class Board {
    ArrayList<ArrayList<Tile>> board;

    public Board(){
        board = new ArrayList<>();
    }

    public ArrayList<Tile> getAdjTiles(Tile t){
        ArrayList<Tile> adjTile = new ArrayList<>();
        int x = t.getCoords().x;
        int y = t.getCoords().y;
        if(t instanceof RoomTile){
            if(!((RoomTile) t).getIsDoor()){
                adjTile.addAll(((RoomTile) t).getRoom().getDoors());
            }
        }
        else{
            if (x>0){
                if(landableTile(board.get(x-1).get(y))){
                    adjTile.add(board.get(x-1).get(y));
                }
            }
            if (x<board.size()){
                if(landableTile(board.get(x+1).get(y))){
                    adjTile.add(board.get(x+1).get(y));
                }
            }
            if (y>0){
                if(landableTile(board.get(x).get(y-1))){
                    adjTile.add(board.get(x).get(y-1));
                }
            }
            if (y<board.get(0).size()){
                if(landableTile(board.get(x).get(y-1))){
                    adjTile.add(board.get(x).get(y-1));
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
        for(Integer i = 1; i<= value;i++){
            tilesInReach.addAll(hashMap.get(i));
        }
        return tilesInReach;
    }
}
